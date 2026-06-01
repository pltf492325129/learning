#include "depth_overlay.hpp"
#include <dispatch/eglproc_auto.hpp>

// ====================================================================
// Unified logging — grep "[DOV]" to find ALL depth overlay logs
// Frame ID set by hook.cpp before UpdateDepthOverlay().
// For tcp_egltrace_auto.cpp capture logs, use:
//   DBG_LOG("[DOV][F%d] ...", g_frame_id, ...);
// ====================================================================

int gOverlayFrameId = 0;

#define DOV_LOG(fmt, ...) \
    DBG_LOG("[DOV][F%d] " fmt, gOverlayFrameId, ##__VA_ARGS__)

#ifdef DEPTH_OVERLAY_DEBUG
#define DOV_DIAG(fmt, ...) DOV_LOG("[DIAG] " fmt, ##__VA_ARGS__)
#else
#define DOV_DIAG(fmt, ...) ((void)0)
#endif

// ====================================================================
// GL error / FBO status macros
// ====================================================================

#define DO_CHECK_GL(msg) do { \
    GLenum _e = _glGetError(); \
    if (_e != GL_NO_ERROR) DOV_LOG("GL ERROR %s: 0x%x", msg, _e); \
} while(0)

#define CHECK_FBO(fbo, name) do { \
    GLenum _s = _glCheckFramebufferStatus(GL_FRAMEBUFFER); \
    if (_s != GL_FRAMEBUFFER_COMPLETE) \
        DOV_LOG("FBO %s(%d) incomplete: 0x%x", name, fbo, _s); \
} while(0)

// ====================================================================
// Globals
// ====================================================================

bool gDepthOverlayEnabled = true;
std::atomic<bool> gOverlayTogglePending{false};
std::mutex gOverlayMutex;

GLuint gOverlayFBO = 0;
GLuint gOverlayColorTex = 0;
GLuint gDepthBlitFBO = 0;
GLuint gDepthBlitColorTex = 0;
GLuint gDepthToColorProgram = 0;
GLuint gDepthTestVisProgram = 0;
GLboolean gOverlayInitDone = GL_FALSE;
GLuint gLastDepthBlitTargetFBO = 0;

int gOverlayWidth = 0;
int gOverlayHeight = 0;

// Temp resources for N-pass depth visualization
static GLuint gTempFBO = 0;
static GLuint gTempDepthTex = 0;
static GLuint gTempColorTex = 0;
static int gTempW = 0, gTempH = 0;

int gStableFrameCount = 0;
int gLastStableWidth = 0;
int gLastStableHeight = 0;
bool gDepthAvailableReported = false;

float gFrameTimeMs = 0.0f;
int gFrameCount = 0;

// Capture globals (written by glBlitFramebuffer hook, read here)
GLuint gCapturedDepthFBO = 0;
int gCapturedDepthWidth = 0;
int gCapturedDepthHeight = 0;
bool gDepthCapturedThisFrame = false;

// ====================================================================
// Shader sources
// ====================================================================

// Samples a depth texture and outputs grayscale
static const char* DEPTH_TO_COLOR_VS = R"(
attribute vec4 a_position;
attribute vec2 a_texCoord;
varying vec2 v_texCoord;
void main() {
    gl_Position = a_position;
    v_texCoord = a_texCoord;
})";

static const char* DEPTH_TO_COLOR_FS = R"(
precision mediump float;
uniform sampler2D u_depthTexture;
varying vec2 v_texCoord;
void main() {
    float d = texture2D(u_depthTexture, v_texCoord).r;
    gl_FragColor = vec4(d, d, d, 1.0);
})";

// Outputs fixed brightness — used with GPU hardware depth test (N-pass)
static const char* DEPTH_TEST_VIS_VS = R"(
attribute vec4 a_position;
void main() {
    gl_Position = a_position;
})";

static const char* DEPTH_TEST_VIS_FS = R"(
precision mediump float;
uniform float u_brightness;
void main() {
    gl_FragColor = vec4(u_brightness, u_brightness, u_brightness, 1.0);
})";

// ====================================================================
// Shader helpers
// ====================================================================

static GLuint compile_shader(GLenum type, const char* src) {
    GLuint s = _glCreateShader(type);
    if (!s) return 0;
    _glShaderSource(s, 1, &src, NULL);
    _glCompileShader(s);
    GLint ok = 0;
    _glGetShaderiv(s, GL_COMPILE_STATUS, &ok);
    if (!ok) {
        GLint len = 0;
        _glGetShaderiv(s, GL_INFO_LOG_LENGTH, &len);
        if (len > 1) {
            char* log = new char[len];
            _glGetShaderInfoLog(s, len, NULL, log);
            DOV_LOG("shader compile fail(0x%x): %s", type, log);
            delete[] log;
        }
        _glDeleteShader(s);
        return 0;
    }
    return s;
}

static GLuint link_program(GLuint vs, GLuint fs) {
    GLuint p = _glCreateProgram();
    if (!p) return 0;
    _glAttachShader(p, vs);
    _glAttachShader(p, fs);
    _glLinkProgram(p);
    GLint ok = 0;
    _glGetProgramiv(p, GL_LINK_STATUS, &ok);
    if (!ok) {
        GLint len = 0;
        _glGetProgramiv(p, GL_INFO_LOG_LENGTH, &len);
        if (len > 1) {
            char* log = new char[len];
            _glGetProgramInfoLog(p, len, NULL, log);
            DOV_LOG("program link fail: %s", log);
            delete[] log;
        }
    }
    // Shaders can be detached+deleted after link
    _glDetachShader(p, vs);
    _glDetachShader(p, fs);
    _glDeleteShader(vs);
    _glDeleteShader(fs);
    return p;
}

// ====================================================================
// Temp resource management (for N-pass depth visualization)
// ====================================================================

static bool EnsureTempResources(int w, int h) {
    if (gTempFBO != 0 && gTempW == w && gTempH == h)
        return true;

    if (gTempFBO) _glDeleteFramebuffers(1, &gTempFBO);
    if (gTempDepthTex) _glDeleteTextures(1, &gTempDepthTex);
    if (gTempColorTex) _glDeleteTextures(1, &gTempColorTex);

    _glGenFramebuffers(1, &gTempFBO);
    _glGenTextures(1, &gTempDepthTex);
    _glGenTextures(1, &gTempColorTex);

    _glBindTexture(GL_TEXTURE_2D, gTempDepthTex);
    _glTexStorage2D(GL_TEXTURE_2D, 1, GL_DEPTH24_STENCIL8, w, h);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_NONE);

    _glBindTexture(GL_TEXTURE_2D, gTempColorTex);
    _glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, w, h);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    _glBindFramebuffer(GL_FRAMEBUFFER, gTempFBO);
    _glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT,
                            GL_TEXTURE_2D, gTempDepthTex, 0);
    _glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                            GL_TEXTURE_2D, gTempColorTex, 0);

    GLenum st = _glCheckFramebufferStatus(GL_FRAMEBUFFER);
    if (st != GL_FRAMEBUFFER_COMPLETE) {
        DOV_LOG("temp FBO incomplete: 0x%x", st);
        return false;
    }
    _glBindFramebuffer(GL_FRAMEBUFFER, 0);
    gTempW = w;
    gTempH = h;
    return true;
}

static void CleanupTempResources() {
    if (gTempFBO) { _glDeleteFramebuffers(1, &gTempFBO); gTempFBO = 0; }
    if (gTempDepthTex) { _glDeleteTextures(1, &gTempDepthTex); gTempDepthTex = 0; }
    if (gTempColorTex) { _glDeleteTextures(1, &gTempColorTex); gTempColorTex = 0; }
    gTempW = gTempH = 0;
}

// RAII: swaps gTempFBO's depth attachment, restores on scope exit
struct DepthAttachSwap {
    GLuint fbo;
    GLuint origTex;
    DepthAttachSwap(GLuint f, GLuint swapTo, GLuint orig)
        : fbo(f), origTex(orig) {
        _glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        _glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT,
                                GL_TEXTURE_2D, swapTo, 0);
    }
    ~DepthAttachSwap() {
        _glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        _glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT,
                                GL_TEXTURE_2D, origTex, 0);
    }
};

// ====================================================================
// Init / Cleanup
// ====================================================================

void InitDepthOverlay(int width, int height) {
    DOV_LOG("InitDepthOverlay %dx%d (was %dx%d, done=%d)",
            width, height, gOverlayWidth, gOverlayHeight, gOverlayInitDone ? 1 : 0);

    if (gOverlayInitDone && gOverlayWidth == width && gOverlayHeight == height)
        return;

    CleanupDepthOverlay();

    // Overlay FBO (side-by-side output, color only)
    _glGenFramebuffers(1, &gOverlayFBO);
    _glGenTextures(1, &gOverlayColorTex);
    _glBindTexture(GL_TEXTURE_2D, gOverlayColorTex);
    _glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, width, height);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    _glBindFramebuffer(GL_FRAMEBUFFER, gOverlayFBO);
    _glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                            GL_TEXTURE_2D, gOverlayColorTex, 0);
    CHECK_FBO(gOverlayFBO, "overlay");

    // Depth blit FBO (depth→grayscale output, color only)
    _glGenFramebuffers(1, &gDepthBlitFBO);
    _glGenTextures(1, &gDepthBlitColorTex);
    _glBindTexture(GL_TEXTURE_2D, gDepthBlitColorTex);
    _glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, width, height);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    _glBindFramebuffer(GL_FRAMEBUFFER, gDepthBlitFBO);
    _glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                            GL_TEXTURE_2D, gDepthBlitColorTex, 0);
    CHECK_FBO(gDepthBlitFBO, "depthBlit");

    // Shaders
    GLuint vs1 = compile_shader(GL_VERTEX_SHADER, DEPTH_TO_COLOR_VS);
    GLuint fs1 = compile_shader(GL_FRAGMENT_SHADER, DEPTH_TO_COLOR_FS);
    gDepthToColorProgram = (vs1 && fs1) ? link_program(vs1, fs1) : 0;

    GLuint vs2 = compile_shader(GL_VERTEX_SHADER, DEPTH_TEST_VIS_VS);
    GLuint fs2 = compile_shader(GL_FRAGMENT_SHADER, DEPTH_TEST_VIS_FS);
    gDepthTestVisProgram = (vs2 && fs2) ? link_program(vs2, fs2) : 0;

    _glBindFramebuffer(GL_FRAMEBUFFER, 0);
    gOverlayWidth = width;
    gOverlayHeight = height;
    gOverlayInitDone = GL_TRUE;
    gStableFrameCount = 0;

    DOV_LOG("init done: FBOs=(%d,%d) progs=(%d,%d)",
            gOverlayFBO, gDepthBlitFBO, gDepthToColorProgram, gDepthTestVisProgram);
}

void CleanupDepthOverlay() {
    DOV_LOG("CleanupDepthOverlay");
    if (gOverlayFBO) { _glDeleteFramebuffers(1, &gOverlayFBO); gOverlayFBO = 0; }
    if (gOverlayColorTex) { _glDeleteTextures(1, &gOverlayColorTex); gOverlayColorTex = 0; }
    if (gDepthBlitFBO) { _glDeleteFramebuffers(1, &gDepthBlitFBO); gDepthBlitFBO = 0; }
    if (gDepthBlitColorTex) { _glDeleteTextures(1, &gDepthBlitColorTex); gDepthBlitColorTex = 0; }
    if (gDepthToColorProgram) { _glDeleteProgram(gDepthToColorProgram); gDepthToColorProgram = 0; }
    if (gDepthTestVisProgram) { _glDeleteProgram(gDepthTestVisProgram); gDepthTestVisProgram = 0; }
    CleanupTempResources();
    gOverlayInitDone = GL_FALSE;
    gOverlayWidth = gOverlayHeight = 0;
    gStableFrameCount = 0;
}

// ====================================================================
// Render — called from UpdateDepthOverlay each frame
// ====================================================================

void RenderDepthOverlay(int width, int height) {
    auto t0 = std::chrono::high_resolution_clock::now();

    // ---- Save GL state ----
    GLint sFBO, sProg, sVP[4], sABuf, sTex2D, sActiveTex;
    GLint sScissor, sBlend, sDepth, sStencil, sColorMask[4];
    _glGetIntegerv(GL_FRAMEBUFFER_BINDING, &sFBO);
    _glGetIntegerv(GL_CURRENT_PROGRAM, &sProg);
    _glGetIntegerv(GL_VIEWPORT, sVP);
    _glGetIntegerv(GL_ARRAY_BUFFER_BINDING, &sABuf);
    _glGetIntegerv(GL_TEXTURE_BINDING_2D, &sTex2D);
    _glGetIntegerv(GL_ACTIVE_TEXTURE, &sActiveTex);
    _glGetIntegerv(GL_SCISSOR_TEST, &sScissor);
    _glGetIntegerv(GL_BLEND, &sBlend);
    _glGetIntegerv(GL_DEPTH_TEST, &sDepth);
    _glGetIntegerv(GL_STENCIL_TEST, &sStencil);
    _glGetIntegerv(GL_COLOR_WRITEMASK, sColorMask);

    // Stack array for vertex attrib enables (no heap alloc)
    GLint maxAttribs = 0;
    GLint sAttribEn[32];
    _glGetIntegerv(GL_MAX_VERTEX_ATTRIBS, &maxAttribs);
    if (maxAttribs > 32) maxAttribs = 32;
    for (int i = 0; i < maxAttribs; i++)
        _glGetVertexAttribiv(i, GL_VERTEX_ATTRIB_ARRAY_ENABLED, &sAttribEn[i]);

    GLint currentFBO = sFBO;  // Usually 0 (default) at eglSwapBuffers time

    // ---- Depth visualization using captured data ----
    // The depth was captured during frame rendering (glBlitFramebuffer hook),
    // before eglSwapBuffers. Data persists in gCapturedDepthFBO.
    bool depthOk = false;

    if (gDepthCapturedThisFrame && gCapturedDepthFBO != 0) {
        // Get the depth texture from captured FBO
        _glBindFramebuffer(GL_FRAMEBUFFER, gCapturedDepthFBO);
        GLint capType = GL_NONE, capId = 0;
        _glGetFramebufferAttachmentParameteriv(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT,
            GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE, &capType);
        _glGetFramebufferAttachmentParameteriv(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT,
            GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME, &capId);

        if (capType == GL_TEXTURE && capId > 0) {
            DOV_LOG("depth capTex=%d %dx%d", capId, gCapturedDepthWidth, gCapturedDepthHeight);

            // Set texture params
            _glBindTexture(GL_TEXTURE_2D, capId);
            _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_NONE);
            _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            while (_glGetError() != GL_NO_ERROR) {}

            float verts[] = { -1,-1,0, 1,-1,0, -1,1,0, 1,1,0 };
            float uvs[]   = { 0,0, 1,0, 0,1, 1,1 };
            int cw = gCapturedDepthWidth, ch = gCapturedDepthHeight;

            // ===== Plan A: Shader sampling =====
            // May work on some GPUs; fails on others (GLES 3.0 spec: undefined
            // for sampler2D on DEPTH24_STENCIL8).
            if (!depthOk && gDepthToColorProgram) {
                _glBindFramebuffer(GL_FRAMEBUFFER, gDepthBlitFBO);
                _glViewport(0, 0, cw, ch);
                GLenum db[] = {GL_COLOR_ATTACHMENT0};
                _glDrawBuffers(1, db);
                _glClearColor(0,0,0,1);
                _glClear(GL_COLOR_BUFFER_BIT);
                _glDisable(GL_DEPTH_TEST);
                while (_glGetError() != GL_NO_ERROR) {}

                _glUseProgram(gDepthToColorProgram);
                _glActiveTexture(GL_TEXTURE0);
                _glBindTexture(GL_TEXTURE_2D, capId);
                _glUniform1i(_glGetUniformLocation(gDepthToColorProgram, "u_depthTexture"), 0);

                GLint pLoc = _glGetAttribLocation(gDepthToColorProgram, "a_position");
                GLint tLoc = _glGetAttribLocation(gDepthToColorProgram, "a_texCoord");
                _glVertexAttribPointer(pLoc, 3, GL_FLOAT, GL_FALSE, 0, verts);
                _glVertexAttribPointer(tLoc, 2, GL_FLOAT, GL_FALSE, 0, uvs);
                _glEnableVertexAttribArray(pLoc);
                if (tLoc >= 0) _glEnableVertexAttribArray(tLoc);
                _glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

                DOV_DIAG("PlanA err=0x%x", _glGetError());

                GLubyte px[4] = {};
                _glReadPixels(cw/2, ch/2, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, px);
                DOV_DIAG("PlanA R=%d G=%d B=%d", px[0], px[1], px[2]);

                if (px[0] > 0 || px[1] > 0 || px[2] > 0) {
                    depthOk = true;
                    DOV_LOG("PlanA OK");
                } else {
                    DOV_LOG("PlanA=0, try PlanB");
                }
                while (_glGetError() != GL_NO_ERROR) {}
            }

            // ===== Plan B: N-pass depth test =====
            // Uses GPU hardware depth test (no texture sampling).
            // Guarantees correct output if depth data exists.
            if (!depthOk && gDepthTestVisProgram && EnsureTempResources(cw, ch)) {
                // Swap depth attachment: captured depth → temp FBO
                DepthAttachSwap swapGuard(gTempFBO, capId, gTempDepthTex);

                if (_glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE) {
                    _glViewport(0, 0, cw, ch);
                    GLenum db[] = {GL_COLOR_ATTACHMENT0};
                    _glDrawBuffers(1, db);
                    _glClearColor(0,0,0,1);
                    _glClear(GL_COLOR_BUFFER_BIT);  // clear color only, keep depth!

                    _glEnable(GL_DEPTH_TEST);
                    _glDepthFunc(GL_LESS);
                    _glEnable(GL_BLEND);
                    _glBlendFunc(GL_ONE, GL_ONE);  // additive
                    _glDepthMask(GL_FALSE);         // read depth only, don't write

                    _glUseProgram(gDepthTestVisProgram);
                    GLint bLoc = _glGetUniformLocation(gDepthTestVisProgram, "u_brightness");
                    GLint pLoc = _glGetAttribLocation(gDepthTestVisProgram, "a_position");
                    _glEnableVertexAttribArray(pLoc);

                    // N passes at evenly spaced Z thresholds.
                    // GL_LESS: fragment passes when frag_z < stored_depth.
                    // Brightness accumulates proportionally to depth value.
                    const int N = 16;
                    _glUniform1f(bLoc, 1.0f / N);
                    for (int i = 0; i < N; i++) {
                        float z = -1.0f + 2.0f * (i + 1) / (N + 1);
                        float zv[] = { -1,-1,z, 1,-1,z, -1,1,z, 1,1,z };
                        _glVertexAttribPointer(pLoc, 3, GL_FLOAT, GL_FALSE, 0, zv);
                        _glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
                    }

                    _glDisable(GL_DEPTH_TEST);
                    _glDisable(GL_BLEND);
                    _glDepthMask(GL_TRUE);

                    GLubyte px[4] = {};
                    _glReadPixels(cw/2, ch/2, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, px);
                    DOV_DIAG("PlanB R=%d G=%d B=%d", px[0], px[1], px[2]);

                    if (px[0] > 0 || px[1] > 0 || px[2] > 0) {
                        // Blit result to gDepthBlitFBO
                        _glBindFramebuffer(GL_READ_FRAMEBUFFER, gTempFBO);
                        _glBindFramebuffer(GL_DRAW_FRAMEBUFFER, gDepthBlitFBO);
                        _glBlitFramebuffer(0,0, cw,ch, 0,0, cw,ch,
                                           GL_COLOR_BUFFER_BIT, GL_NEAREST);
                        depthOk = true;
                        DOV_LOG("PlanB OK");
                    } else {
                        DOV_LOG("PlanB=0 (depth empty?)");
                    }
                }
                // swapGuard restores depth attachment here (RAII)
                while (_glGetError() != GL_NO_ERROR) {}
            }
        } else {
            DOV_LOG("capFBO no depth tex");
        }
    } else {
        DOV_LOG("no captured depth");
    }

    // ---- Overlay assembly ----
    // Left half: game color | Right half: depth grayscale (or black)
    _glBindFramebuffer(GL_FRAMEBUFFER, gOverlayFBO);
    _glViewport(0, 0, width, height);
    _glClearColor(0,0,0,1);
    _glClear(GL_COLOR_BUFFER_BIT);
    _glDisable(GL_SCISSOR_TEST);
    GLenum db[] = {GL_COLOR_ATTACHMENT0};
    _glDrawBuffers(1, db);

    // Left: game content (from default FBO at eglSwapBuffers time)
    _glBindFramebuffer(GL_READ_FRAMEBUFFER, currentFBO);
    if (currentFBO == 0) _glReadBuffer(GL_BACK);
    else _glReadBuffer(GL_COLOR_ATTACHMENT0);
    _glBindFramebuffer(GL_DRAW_FRAMEBUFFER, gOverlayFBO);
    _glBlitFramebuffer(0,0, width,height, 0,0, width/2,height,
                       GL_COLOR_BUFFER_BIT, GL_LINEAR);
    DOV_DIAG("left blit err=0x%x", _glGetError());

    // Right: depth visualization
    _glBindFramebuffer(GL_READ_FRAMEBUFFER, gDepthBlitFBO);
    _glBlitFramebuffer(0,0, width,height, width/2,0, width,height,
                       GL_COLOR_BUFFER_BIT, GL_LINEAR);
    DOV_DIAG("right blit err=0x%x", _glGetError());

    // Final: overlay → original FBO
    _glBindFramebuffer(GL_READ_FRAMEBUFFER, gOverlayFBO);
    _glBindFramebuffer(GL_DRAW_FRAMEBUFFER, sFBO);
    _glBlitFramebuffer(0,0, width,height, 0,0, width,height,
                       GL_COLOR_BUFFER_BIT, GL_LINEAR);
    DOV_DIAG("final blit err=0x%x", _glGetError());

    // ---- Restore GL state ----
    _glBindFramebuffer(GL_FRAMEBUFFER, sFBO);
    _glUseProgram(sProg);
    _glViewport(sVP[0], sVP[1], sVP[2], sVP[3]);
    _glBindBuffer(GL_ARRAY_BUFFER, sABuf);
    _glBindTexture(GL_TEXTURE_2D, sTex2D);
    _glActiveTexture(sActiveTex);
    if (sScissor) _glEnable(GL_SCISSOR_TEST); else _glDisable(GL_SCISSOR_TEST);
    if (sBlend)   _glEnable(GL_BLEND);        else _glDisable(GL_BLEND);
    if (sDepth)   _glEnable(GL_DEPTH_TEST);    else _glDisable(GL_DEPTH_TEST);
    if (sStencil) _glEnable(GL_STENCIL_TEST);  else _glDisable(GL_STENCIL_TEST);
    _glColorMask(sColorMask[0], sColorMask[1], sColorMask[2], sColorMask[3]);
    for (int i = 0; i < maxAttribs; i++) {
        if (sAttribEn[i]) _glEnableVertexAttribArray(i);
        else _glDisableVertexAttribArray(i);
    }

    // ---- Timing ----
    auto t1 = std::chrono::high_resolution_clock::now();
    gFrameTimeMs = std::chrono::duration<float, std::milli>(t1 - t0).count();
    gFrameCount++;
    if (gFrameCount % 60 == 0) {
        DOV_LOG("timing: %.2f ms (%.0f fps)", gFrameTimeMs, 1000.0f / gFrameTimeMs);
    }
}

// ====================================================================
// Update — called from eglSwapBuffers hook
// ====================================================================

void UpdateDepthOverlay(int width, int height) {
    std::lock_guard<std::mutex> lock(gOverlayMutex);

    // TODO: restore file trigger for production
    // bool triggered = (access(DEPTH_OVERLAY_TRIGGER_FILE, F_OK) == 0);
    bool triggered = true;

    if (triggered && !gDepthOverlayEnabled) {
        gDepthOverlayEnabled = true;
        DOV_LOG("ENABLED");
    } else if (!triggered && gDepthOverlayEnabled) {
        gDepthOverlayEnabled = false;
        DOV_LOG("DISABLED");
    }

    if (!gDepthOverlayEnabled) return;

    if (width == gLastStableWidth && height == gLastStableHeight) {
        gStableFrameCount++;
    } else {
        gStableFrameCount = 0;
        gLastStableWidth = width;
        gLastStableHeight = height;
    }

    if (gStableFrameCount >= DEPTH_OVERLAY_STABLE_FRAME_THRESHOLD) {
        if (!gOverlayInitDone || gOverlayWidth != width || gOverlayHeight != height)
            InitDepthOverlay(width, height);
        RenderDepthOverlay(width, height);
    }
}
