#include "depth_overlay.hpp"
#include <GLES3/gl3.h>
#include <dispatch/eglproc_auto.hpp>

#define DO_CHECK_GL_ERROR(msg) do { \
    GLenum err = _glGetError(); \
    if (err != GL_NO_ERROR) { \
        DBG_LOG("Depth overlay GL ERROR at %s: 0x%x", msg, err); \
    } \
} while(0)

#define CHECK_FBO_STATUS(fbo, name) do { \
    GLenum status = _glCheckFramebufferStatus(GL_FRAMEBUFFER); \
    if (status != GL_FRAMEBUFFER_COMPLETE) { \
        DBG_LOG("Depth overlay: FBO %s (%d) incomplete, status=0x%x", name, fbo, status); \
    } else { \
        DBG_LOG("Depth overlay: FBO %s (%d) complete", name, fbo); \
    } \
} while(0)

// bool gDepthOverlayEnabled = false;
bool gDepthOverlayEnabled = true;
std::atomic<bool> gOverlayTogglePending{false};

GLuint gOverlayFBO = 0;
GLuint gOverlayColorTex = 0;
GLuint gDepthBlitFBO = 0;
GLuint gDepthBlitColorTex = 0;
GLuint gSideBySideProgram = 0;
GLuint gDepthToColorProgram = 0;
GLboolean gOverlayInitDone = GL_FALSE;

// Cached temp resources for depth blit (avoid per-frame alloc/dealloc)
static GLuint gTempDepthFBO = 0;
static GLuint gTempDepthTex = 0;
static GLuint gTempDummyColorTex = 0;
static int gTempDepthWidth = 0;
static int gTempDepthHeight = 0;
int gOverlayWidth = 0;
int gOverlayHeight = 0;
std::mutex gOverlayMutex;
GLuint gLastDepthBlitTargetFBO = 0;

int gStableFrameCount = 0;
int gLastStableWidth = 0;
int gLastStableHeight = 0;
bool gDepthAvailableReported = false;

std::chrono::time_point<std::chrono::high_resolution_clock> gLastFrameTime;
float gFrameTimeMs = 0.0f;
int gFrameCount = 0;

static const char* DEPTH_TO_COLOR_VERTEX_SHADER = R"(
attribute vec4 a_position;
attribute vec2 a_texCoord;
varying vec2 v_texCoord;
void main() {
    gl_Position = a_position;
    v_texCoord = a_texCoord;
}
)";

static const char* DEPTH_TO_COLOR_FRAGMENT_SHADER = R"(
precision mediump float;
uniform sampler2D u_depthTexture;
varying vec2 v_texCoord;

void main() {
    float depth = texture2D(u_depthTexture, v_texCoord).r;
    gl_FragColor = vec4(depth, depth, depth, 1.0);
}
)";

static const char* SIDE_BY_SIDE_VERTEX_SHADER = R"(
attribute vec4 a_position;
attribute vec2 a_texCoord;
varying vec2 v_texCoord;
void main() {
    gl_Position = a_position;
    v_texCoord = a_texCoord;
}
)";

static const char* SIDE_BY_SIDE_FRAGMENT_SHADER = R"(
precision mediump float;
uniform sampler2D u_colorTexture;
uniform sampler2D u_depthPseudocolorTexture;
varying vec2 v_texCoord;

void main() {
    if (v_texCoord.x < 0.5) {
        gl_FragColor = texture2D(u_colorTexture, vec2(v_texCoord.x * 2.0, v_texCoord.y));
    } else {
        gl_FragColor = texture2D(u_depthPseudocolorTexture, 
                                 vec2((v_texCoord.x - 0.5) * 2.0, v_texCoord.y));
    }
}
)";

static bool isDepthOverlayTriggered() {
    // bool triggered = (access(DEPTH_OVERLAY_TRIGGER_FILE, F_OK) == 0);
    bool triggered = true;
    DBG_LOG("Depth overlay: trigger file check result=%d, path=%s", triggered ? 1 : 0, DEPTH_OVERLAY_TRIGGER_FILE);
    return triggered;
}

static GLuint compile_shader(GLenum type, const char* shaderSrc) {
    GLuint shader = _glCreateShader(type);
    _glShaderSource(shader, 1, &shaderSrc, NULL);
    _glCompileShader(shader);
    
    GLint compiled = 0;
    _glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
    if (!compiled) {
        GLint infoLen = 0;
        _glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
        if (infoLen > 1) {
            char* infoLog = new char[infoLen];
            _glGetShaderInfoLog(shader, infoLen, NULL, infoLog);
            DBG_LOG("Depth overlay: shader compile error: %s", infoLog);
            delete[] infoLog;
        }
    } else {
        DBG_LOG("Depth overlay: shader compiled successfully, type=0x%x", type);
    }
    return shader;
}

// Ensure cached temp depth blit resources exist and match dimensions.
// Returns true if resources are ready, false on error.
static bool EnsureTempDepthResources(int width, int height) {
    if (gTempDepthFBO != 0 && gTempDepthWidth == width && gTempDepthHeight == height) {
        return true; // Already cached with matching size
    }

    // Clean up old resources if dimensions changed
    if (gTempDepthFBO != 0) {
        _glDeleteFramebuffers(1, &gTempDepthFBO);
        gTempDepthFBO = 0;
    }
    if (gTempDepthTex != 0) {
        _glDeleteTextures(1, &gTempDepthTex);
        gTempDepthTex = 0;
    }
    if (gTempDummyColorTex != 0) {
        _glDeleteTextures(1, &gTempDummyColorTex);
        gTempDummyColorTex = 0;
    }

    _glGenFramebuffers(1, &gTempDepthFBO);
    _glGenTextures(1, &gTempDepthTex);
    _glGenTextures(1, &gTempDummyColorTex);

    // Setup depth texture
    _glBindTexture(GL_TEXTURE_2D, gTempDepthTex);
    _glTexStorage2D(GL_TEXTURE_2D, 1, GL_DEPTH24_STENCIL8, width, height);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_NONE);

    // Setup dummy color texture
    _glBindTexture(GL_TEXTURE_2D, gTempDummyColorTex);
    _glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, width, height);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    // Attach to FBO
    _glBindFramebuffer(GL_FRAMEBUFFER, gTempDepthFBO);
    _glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, gTempDepthTex, 0);
    _glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, gTempDummyColorTex, 0);

    GLenum status = _glCheckFramebufferStatus(GL_FRAMEBUFFER);
    if (status != GL_FRAMEBUFFER_COMPLETE) {
        DBG_LOG("Depth overlay: temp depth FBO incomplete, status=0x%x", status);
        return false;
    }

    _glBindFramebuffer(GL_FRAMEBUFFER, 0);

    gTempDepthWidth = width;
    gTempDepthHeight = height;
    DBG_LOG("Depth overlay: cached temp depth resources FBO=%d tex=%d dummyColor=%d size=%dx%d",
            gTempDepthFBO, gTempDepthTex, gTempDummyColorTex, width, height);
    return true;
}

static void CleanupTempDepthResources() {
    if (gTempDepthFBO) { _glDeleteFramebuffers(1, &gTempDepthFBO); gTempDepthFBO = 0; }
    if (gTempDepthTex) { _glDeleteTextures(1, &gTempDepthTex); gTempDepthTex = 0; }
    if (gTempDummyColorTex) { _glDeleteTextures(1, &gTempDummyColorTex); gTempDummyColorTex = 0; }
    gTempDepthWidth = 0;
    gTempDepthHeight = 0;
}

void InitDepthOverlay(int width, int height) {
    DBG_LOG("Depth overlay: InitDepthOverlay called, width=%d, height=%d, current=(%d,%d), initDone=%d", 
            width, height, gOverlayWidth, gOverlayHeight, gOverlayInitDone ? 1 : 0);
    
    if (gOverlayInitDone && gOverlayWidth == width && gOverlayHeight == height) {
        DBG_LOG("Depth overlay: skip init, same size");
        return;
    }
    
    DBG_LOG("Depth overlay: cleaning up old resources");
    CleanupDepthOverlay();
    
    DBG_LOG("Depth overlay: creating overlay FBO and color texture");
    _glGenFramebuffers(1, &gOverlayFBO);
    _glGenTextures(1, &gOverlayColorTex);
    
    _glBindTexture(GL_TEXTURE_2D, gOverlayColorTex);
    // _glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
    _glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, width, height);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    DBG_LOG("Depth overlay: overlayColorTex=%d created", gOverlayColorTex);
    
    _glBindFramebuffer(GL_FRAMEBUFFER, gOverlayFBO);
    _glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, gOverlayColorTex, 0);
    CHECK_FBO_STATUS(gOverlayFBO, "gOverlayFBO");
    DO_CHECK_GL_ERROR("after gOverlayFBO setup");
    
    DBG_LOG("Depth overlay: creating depth blit FBO and color texture");
    _glGenFramebuffers(1, &gDepthBlitFBO);
    _glGenTextures(1, &gDepthBlitColorTex);
    
    _glBindTexture(GL_TEXTURE_2D, gDepthBlitColorTex);
    _glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, width, height);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    DBG_LOG("Depth overlay: depthBlitColorTex=%d created", gDepthBlitColorTex);
    
    _glBindFramebuffer(GL_FRAMEBUFFER, gDepthBlitFBO);
    _glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, gDepthBlitColorTex, 0);
    CHECK_FBO_STATUS(gDepthBlitFBO, "gDepthBlitFBO");
    DO_CHECK_GL_ERROR("after gDepthBlitFBO setup");
    
    GLenum status = _glCheckFramebufferStatus(GL_FRAMEBUFFER);
    if (status != GL_FRAMEBUFFER_COMPLETE) {
        DBG_LOG("Depth overlay: FBO incomplete status=0x%x", status);
    } else {
        DBG_LOG("Depth overlay: FBO status=COMPLETE");
    }
    
    DBG_LOG("Depth overlay: compiling depth-to-color shader");
    GLuint vertShader1 = compile_shader(GL_VERTEX_SHADER, DEPTH_TO_COLOR_VERTEX_SHADER);
    GLuint fragShader1 = compile_shader(GL_FRAGMENT_SHADER, DEPTH_TO_COLOR_FRAGMENT_SHADER);
    gDepthToColorProgram = _glCreateProgram();
    _glAttachShader(gDepthToColorProgram, vertShader1);
    _glAttachShader(gDepthToColorProgram, fragShader1);
    _glLinkProgram(gDepthToColorProgram);
    
    GLint linkStatus = 0;
    _glGetProgramiv(gDepthToColorProgram, GL_LINK_STATUS, &linkStatus);
    DBG_LOG("Depth overlay: depth-to-color program linkStatus=%d, vertShader=%d, fragShader=%d",
            linkStatus, vertShader1, fragShader1);
    if (linkStatus == GL_FALSE) {
        GLint infoLen = 0;
        _glGetProgramiv(gDepthToColorProgram, GL_INFO_LOG_LENGTH, &infoLen);
        if (infoLen > 1) {
            char* infoLog = new char[infoLen];
            _glGetProgramInfoLog(gDepthToColorProgram, infoLen, NULL, infoLog);
            DBG_LOG("Depth overlay: depth-to-color program link error: %s", infoLog);
            delete[] infoLog;
        }
    } else {
        DBG_LOG("Depth overlay: depth-to-color program linked successfully");
    }
    
    _glDeleteShader(vertShader1);
    _glDeleteShader(fragShader1);
    DBG_LOG("Depth overlay: depthToColorProgram=%d", gDepthToColorProgram);
    
    DBG_LOG("Depth overlay: compiling side-by-side shader");
    GLuint vertShader2 = compile_shader(GL_VERTEX_SHADER, SIDE_BY_SIDE_VERTEX_SHADER);
    GLuint fragShader2 = compile_shader(GL_FRAGMENT_SHADER, SIDE_BY_SIDE_FRAGMENT_SHADER);
    gSideBySideProgram = _glCreateProgram();
    _glAttachShader(gSideBySideProgram, vertShader2);
    _glAttachShader(gSideBySideProgram, fragShader2);
    _glLinkProgram(gSideBySideProgram);
    
    GLint linkStatus2 = 0;
    _glGetProgramiv(gSideBySideProgram, GL_LINK_STATUS, &linkStatus2);
    if (linkStatus2 == GL_FALSE) {
        GLint infoLen2 = 0;
        _glGetProgramiv(gSideBySideProgram, GL_INFO_LOG_LENGTH, &infoLen2);
        if (infoLen2 > 1) {
            char* infoLog2 = new char[infoLen2];
            _glGetProgramInfoLog(gSideBySideProgram, infoLen2, NULL, infoLog2);
            DBG_LOG("Depth overlay: side-by-side program link error: %s", infoLog2);
            delete[] infoLog2;
        }
    } else {
        DBG_LOG("Depth overlay: side-by-side program linked successfully");
    }
    
    _glDeleteShader(vertShader2);
    _glDeleteShader(fragShader2);
    DBG_LOG("Depth overlay: sideBySideProgram=%d", gSideBySideProgram);
    
    _glBindFramebuffer(GL_FRAMEBUFFER, 0);
    
    gOverlayWidth = width;
    gOverlayHeight = height;
    gOverlayInitDone = GL_TRUE;
    gLastFrameTime = std::chrono::high_resolution_clock::now();
    gStableFrameCount = 0;
    
    DBG_LOG("Depth overlay: initialized successfully, FBO=(%d,%d), Tex=(%d,%d), programs=(%d,%d)",
            gOverlayFBO, gDepthBlitFBO, gOverlayColorTex, gDepthBlitColorTex, 
            gDepthToColorProgram, gSideBySideProgram);
}

static GLuint findFBOWithDepth() {
    GLuint bestFBO = 0;
    bool bestHasColor = false;

    // Scan FBOs and prefer ones that have both color AND depth attachments.
    // Note: we cannot query texture dimensions via glGetTexLevelParameteriv (desktop GL only).
    // Prefer FBOs with color+depth over FBOs with only depth (e.g. shadow maps).
    for (GLuint fbo = 1; fbo < 256; fbo++) {
        GLint depthType = GL_NONE;
        _glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        _glGetFramebufferAttachmentParameteriv(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT,
                                              GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE, &depthType);

        if (depthType != GL_NONE && depthType != GL_FRAMEBUFFER) {
            GLint colorType = GL_NONE;
            _glGetFramebufferAttachmentParameteriv(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                                                  GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE, &colorType);
            bool hasColor = (colorType != GL_NONE);

            // Prefer FBOs with both color and depth (likely scene FBOs, not shadow maps)
            if (hasColor && !bestHasColor) {
                bestFBO = fbo;
                bestHasColor = true;
                DBG_LOG("Depth overlay: found candidate FBO=%d with color+depth, depthType=0x%x",
                        fbo, depthType);
            } else if (!bestHasColor) {
                // Only depth, no color yet found — use as fallback
                bestFBO = fbo;
                DBG_LOG("Depth overlay: found candidate FBO=%d with depth only, depthType=0x%x",
                        fbo, depthType);
            }
        }
    }

    _glBindFramebuffer(GL_FRAMEBUFFER, 0);
    
    if (bestFBO > 0) {
        DBG_LOG("Depth overlay: selected FBO=%d as best depth source", bestFBO);
    } else {
        DBG_LOG("Depth overlay: no FBO with depth found after scanning 1-255");
    }
    
    return bestFBO;
}

void RenderDepthOverlay(int width, int height) {
    auto frameStart = std::chrono::high_resolution_clock::now();
    
    struct ResetGuard {
        ~ResetGuard() { gLastDepthBlitTargetFBO = 0; }
    } resetGuard;
    
    GLint savedFBO = 0, savedProgram = 0, savedViewport[4] = {0, 0, 0, 0};
    GLint savedArrayBuffer = 0, savedTexture2D = 0, savedActiveTexture = GL_TEXTURE0;
    GLint savedScissorTest = 0, savedBlend = 0, savedDepthTest = 0, savedStencilTest = 0;
    GLint savedColorWriteMask[4] = {0, 0, 0, 0};
    _glGetIntegerv(GL_FRAMEBUFFER_BINDING, &savedFBO);
    _glGetIntegerv(GL_CURRENT_PROGRAM, &savedProgram);
    _glGetIntegerv(GL_VIEWPORT, savedViewport);
    _glGetIntegerv(GL_ARRAY_BUFFER_BINDING, &savedArrayBuffer);
    _glGetIntegerv(GL_TEXTURE_BINDING_2D, &savedTexture2D);
    _glGetIntegerv(GL_ACTIVE_TEXTURE, &savedActiveTexture);
    _glGetIntegerv(GL_SCISSOR_TEST, &savedScissorTest);
    _glGetIntegerv(GL_BLEND, &savedBlend);
    _glGetIntegerv(GL_DEPTH_TEST, &savedDepthTest);
    _glGetIntegerv(GL_STENCIL_TEST, &savedStencilTest);
    _glGetIntegerv(GL_COLOR_WRITEMASK, savedColorWriteMask);

    // Save vertex attrib enable states
    GLint maxAttribs = 0;
    _glGetIntegerv(GL_MAX_VERTEX_ATTRIBS, &maxAttribs);
    GLint* savedAttribEnable = nullptr;
    if (maxAttribs > 0) {
        savedAttribEnable = new GLint[maxAttribs];
        for (int i = 0; i < maxAttribs; i++) {
            _glGetVertexAttribiv(i, GL_VERTEX_ATTRIB_ARRAY_ENABLED, &savedAttribEnable[i]);
        }
    }
    
    GLint currentFBO = 0;
    _glGetIntegerv(GL_FRAMEBUFFER_BINDING, &currentFBO);
    
    GLuint sourceFBO = 0;
    if (gLastDepthBlitTargetFBO != 0) {
        sourceFBO = gLastDepthBlitTargetFBO;
        DBG_LOG("Depth overlay: using recorded depth blit target FBO=%d", sourceFBO);
    } else {
        sourceFBO = currentFBO;
        DBG_LOG("Depth overlay: no depth blit recorded, using current FBO=%d", sourceFBO);
    }
    
    _glBindFramebuffer(GL_FRAMEBUFFER, sourceFBO);
    
    GLint colorAttachmentId = 0;
    GLint depthAttachmentId = 0;
    GLint depthAttachmentType = GL_NONE;
    
    _glGetFramebufferAttachmentParameteriv(GL_READ_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                                          GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME, &colorAttachmentId);
    _glGetFramebufferAttachmentParameteriv(GL_READ_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT,
                                          GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE, &depthAttachmentType);
    _glGetFramebufferAttachmentParameteriv(GL_READ_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT,
                                          GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME, &depthAttachmentId);
    
    {
        GLint readFBOBinding = 0;
        _glGetIntegerv(GL_READ_FRAMEBUFFER_BINDING, &readFBOBinding);
        DBG_LOG("Depth overlay: sourceFBO=%d, GL_READ_FRAMEBUFFER_BINDING=%d, GL_FRAMEBUFFER_BINDING=%d",
                sourceFBO, readFBOBinding, currentFBO);
        
        GLint srcDepthType = GL_NONE, srcDepthId = 0;
        _glGetFramebufferAttachmentParameteriv(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT,
                                              GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE, &srcDepthType);
        _glGetFramebufferAttachmentParameteriv(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT,
                                              GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME, &srcDepthId);
        GLint srcColorType = GL_NONE, srcColorId = 0;
        _glGetFramebufferAttachmentParameteriv(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                                              GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE, &srcColorType);
        _glGetFramebufferAttachmentParameteriv(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                                              GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME, &srcColorId);
        DBG_LOG("Depth overlay: sourceFBO=%d has depth(type=0x%x,id=%d) color(type=0x%x,id=%d)",
                sourceFBO, srcDepthType, srcDepthId, srcColorType, srcColorId);
        DO_CHECK_GL_ERROR("after query sourceFBO attachments");
        
        if (srcDepthType == GL_TEXTURE && srcDepthId > 0) {
            // GLES has no GL_FRAMEBUFFER_ATTACHMENT_TEXTURE_WIDTH/HEIGHT (desktop GL only)
            // Use the already-known width/height for debug logging
            GLint depthRedSize = 0;
            _glGetFramebufferAttachmentParameteriv(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT,
                GL_FRAMEBUFFER_ATTACHMENT_RED_SIZE, &depthRedSize);
            DBG_LOG("Depth overlay: sourceFBO depth texture id=%d size=%dx%d redSize=%d",
                    srcDepthId, width, height, depthRedSize);
            DO_CHECK_GL_ERROR("after query depth texture params");
        }
    }
    
    if (depthAttachmentId == 0) {
        GLuint fboWithDepth = findFBOWithDepth();
        if (fboWithDepth > 0) {
            sourceFBO = fboWithDepth;
            _glBindFramebuffer(GL_FRAMEBUFFER, sourceFBO);
            _glGetFramebufferAttachmentParameteriv(GL_READ_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                                                  GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME, &colorAttachmentId);
            _glGetFramebufferAttachmentParameteriv(GL_READ_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT,
                                                  GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE, &depthAttachmentType);
            _glGetFramebufferAttachmentParameteriv(GL_READ_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT,
                                                  GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME, &depthAttachmentId);
            DBG_LOG("Depth overlay: switched to FBO=%d for depth, color=%d, depth=(type=0x%x,id=%d)",
                    sourceFBO, colorAttachmentId, depthAttachmentType, depthAttachmentId);
        }
    }
    
    DBG_LOG("Depth overlay: render frameID=%d, size=%dx%d, sourceFBO=%d, color=%d, depth=(type=0x%x,id=%d), currentFBO=%d",
            gFrameCount, width, height, sourceFBO, colorAttachmentId, depthAttachmentType, depthAttachmentId, currentFBO);
    DBG_LOG("Depth overlay: shader programs - depthToColor=%d, sideBySide=%d, gDepthBlitFBO=%d, gOverlayFBO=%d",
            gDepthToColorProgram, gSideBySideProgram, gDepthBlitFBO, gOverlayFBO);

    if (colorAttachmentId == 0 || depthAttachmentId == 0) {
        if (!gDepthAvailableReported) {
            DBG_LOG("Depth overlay: no color or depth attachment, colorId=%d, depthId=%d", 
                    colorAttachmentId, depthAttachmentId);
            gDepthAvailableReported = true;
        }
        _glBindFramebuffer(GL_FRAMEBUFFER, savedFBO);
        _glUseProgram(savedProgram);
        _glViewport(savedViewport[0], savedViewport[1], savedViewport[2], savedViewport[3]);
        _glBindBuffer(GL_ARRAY_BUFFER, savedArrayBuffer);
        _glBindTexture(GL_TEXTURE_2D, savedTexture2D);
        _glActiveTexture(savedActiveTexture);
        delete[] savedAttribEnable;
        return;
    }

    gDepthAvailableReported = false;

    // CRITICAL: On tile-based mobile GPUs (Mali, Adreno, PowerVR), rendering commands
    // are deferred until a flush. Our code runs in eglSwapBuffers BEFORE the real swap,
    // so depth values may still be in on-chip tiles and not written to the texture yet.
    // _glFinish() forces the GPU to complete all pending work.
    DBG_LOG("Depth overlay: calling _glFinish() to flush tile-based GPU");
    _glFinish();

    // === 验证帧中捕获的深度数据 ===
    // 在 glBlitFramebuffer hook 中，游戏做 depth blit 时我们同步捕获了深度。
    // gCapturedDepthFBO 里有捕获的深度数据。验证它是否非空。
    {
        if (gDepthCapturedThisFrame && gCapturedDepthFBO != 0) {
            DBG_LOG("DIAG capture: depth was captured this frame, capFBO=%d %dx%d",
                    gCapturedDepthFBO, gCapturedDepthWidth, gCapturedDepthHeight);

            // 在捕获的 FBO 上做深度测试来验证数据
            // 需要一个 color attachment 来读回结果，所以先附加一个临时 color
            bool needColorAttach = false;
            _glBindFramebuffer(GL_FRAMEBUFFER, gCapturedDepthFBO);
            GLint colorType = GL_NONE;
            _glGetFramebufferAttachmentParameteriv(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE, &colorType);
            if (colorType != GL_TEXTURE && colorType != GL_RENDERBUFFER) {
                // 没有颜色附件，临时附加一个
                needColorAttach = true;
                if (!gTempDummyColorTex) {
                    _glGenTextures(1, &gTempDummyColorTex);
                }
                _glBindTexture(GL_TEXTURE_2D, gTempDummyColorTex);
                _glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8,
                                gCapturedDepthWidth, gCapturedDepthHeight);
                _glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                                        GL_TEXTURE_2D, gTempDummyColorTex, 0);
            }

            GLenum drawBuffers[] = {GL_COLOR_ATTACHMENT0};
            _glDrawBuffers(1, drawBuffers);

            _glViewport(0, 0, gCapturedDepthWidth, gCapturedDepthHeight);
            _glClearColor(1.0f, 0.0f, 0.0f, 1.0f);  // RED
            _glClear(GL_COLOR_BUFFER_BIT);  // 只清 color，不清 depth！

            _glUseProgram(gDepthToColorProgram);
            _glActiveTexture(GL_TEXTURE0);
            _glBindTexture(GL_TEXTURE_2D, 0);
            _glUniform1i(_glGetUniformLocation(gDepthToColorProgram, "u_depthTexture"), 0);
            GLint diagPosLoc = _glGetAttribLocation(gDepthToColorProgram, "a_position");
            GLint diagTexLoc = _glGetAttribLocation(gDepthToColorProgram, "a_texCoord");
            if (diagTexLoc >= 0) _glDisableVertexAttribArray(diagTexLoc);
            _glEnableVertexAttribArray(diagPosLoc);
            // Z=0.0 → depth threshold = 0.5
            float dv[] = { -1,-1,0.0f, 1,-1,0.0f, -1,1,0.0f, 1,1,0.0f };
            _glVertexAttribPointer(diagPosLoc, 3, GL_FLOAT, GL_FALSE, 0, dv);

            _glEnable(GL_DEPTH_TEST);
            _glDepthFunc(GL_LESS);
            _glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            _glDisable(GL_DEPTH_TEST);

            GLubyte dp[4] = {0};
            _glReadPixels(gCapturedDepthWidth/2, gCapturedDepthHeight/2, 1, 1,
                          GL_RGBA, GL_UNSIGNED_BYTE, dp);
            DBG_LOG("DIAG capture: depth test on capFBO R=%d G=%d B=%d (%s)",
                    dp[0], dp[1], dp[2],
                    dp[0] == 0 ? "BLACK=DATA_EXISTS!" : "RED=empty");

            if (dp[0] == 0) {
                DBG_LOG("DIAG capture: CONCLUSION=DEPTH_DATA_CAPTURED_SUCCESSFULLY");
            } else {
                DBG_LOG("DIAG capture: CONCLUSION=CAPTURED_BUT_EMPTY (capture failed?)");
            }
            while (_glGetError() != GL_NO_ERROR) {}
        } else {
            DBG_LOG("DIAG capture: NO depth captured this frame (gDepthCaptured=%d capFBO=%d)",
                    gDepthCapturedThisFrame ? 1 : 0, gCapturedDepthFBO);
        }
    }

    bool depthReadOk = false;
    float vertices[] = { -1, -1, 0,  1, -1, 0,  -1, 1, 0,  1, 1, 0 };
    float texCoords[] = { 0, 0,  1, 0,  0, 1,  1, 1 };

    // === Method 1: Shader sampling of depth texture (most GPU-friendly) ===
    // After _glFinish(), the depth texture should be populated.
    if (depthAttachmentType == GL_TEXTURE && depthAttachmentId != 0) {
        DBG_LOG("Depth overlay: trying shader sampling of depth texture id=%d", depthAttachmentId);

        // Override texture parameters to ensure sampling works
        _glBindTexture(GL_TEXTURE_2D, depthAttachmentId);
        _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_NONE);
        _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        while (_glGetError() != GL_NO_ERROR) {}

        _glBindFramebuffer(GL_FRAMEBUFFER, gDepthBlitFBO);
        _glViewport(0, 0, width, height);
        {
            GLenum drawBuffers[] = {GL_COLOR_ATTACHMENT0};
            _glDrawBuffers(1, drawBuffers);
        }
        _glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        _glClear(GL_COLOR_BUFFER_BIT);
        _glDisable(GL_DEPTH_TEST);
        while (_glGetError() != GL_NO_ERROR) {}

        _glUseProgram(gDepthToColorProgram);
        _glActiveTexture(GL_TEXTURE0);
        _glBindTexture(GL_TEXTURE_2D, depthAttachmentId);
        GLint depthTexLoc = _glGetUniformLocation(gDepthToColorProgram, "u_depthTexture");
        _glUniform1i(depthTexLoc, 0);
        GLint posLoc = _glGetAttribLocation(gDepthToColorProgram, "a_position");
        GLint texLoc = _glGetAttribLocation(gDepthToColorProgram, "a_texCoord");

        _glVertexAttribPointer(posLoc, 3, GL_FLOAT, GL_FALSE, 0, vertices);
        _glVertexAttribPointer(texLoc, 2, GL_FLOAT, GL_FALSE, 0, texCoords);
        _glEnableVertexAttribArray(posLoc);
        _glEnableVertexAttribArray(texLoc);
        _glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        GLenum drawErr = _glGetError();
        DBG_LOG("Depth overlay: shader draw err=0x%x, texLoc=%d, posLoc=%d, texLoc=%d",
                drawErr, depthTexLoc, posLoc, texLoc);

        // Verify output
        GLubyte pixel[4] = {0, 0, 0, 0};
        _glBindFramebuffer(GL_READ_FRAMEBUFFER, gDepthBlitFBO);
        _glReadPixels(width/2, height/2, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, pixel);
        DBG_LOG("DIAG shader after flush: gDepthBlitFBO center R=%d G=%d B=%d A=%d",
                pixel[0], pixel[1], pixel[2], pixel[3]);

        if (pixel[0] > 0 || pixel[1] > 0 || pixel[2] > 0) {
            depthReadOk = true;
            DBG_LOG("Depth overlay: shader sampling SUCCESS after _glFlush!");
        } else {
            DBG_LOG("Depth overlay: shader sampling still returns 0 after _glFinish");
        }
        while (_glGetError() != GL_NO_ERROR) {}
    }

    // === Method 2: glBlitFramebuffer (the game itself uses this successfully!) ===
    // On this device, glReadPixels for depth always fails with GL_INVALID_ENUM,
    // but glBlitFramebuffer(GL_DEPTH_BUFFER_BIT) succeeds (err=0x0).
    // So we blit depth to our temp texture, then use shader to convert depth→color.
    if (!depthReadOk && EnsureTempDepthResources(width, height)) {
        DBG_LOG("Depth overlay: trying glBlitFramebuffer depth blit");
        while (_glGetError() != GL_NO_ERROR) {}

        _glBindFramebuffer(GL_READ_FRAMEBUFFER, sourceFBO);
        _glBindFramebuffer(GL_DRAW_FRAMEBUFFER, gTempDepthFBO);
        _glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        GLenum blitErr = _glGetError();
        DBG_LOG("Depth overlay: depth blit err=0x%x", blitErr);

        if (blitErr == GL_NO_ERROR) {
            // Blit succeeded! Skip glReadPixels verification (not supported on this device).
            // Directly use shader to sample gTempDepthTex and convert to color.
            _glBindFramebuffer(GL_FRAMEBUFFER, gDepthBlitFBO);
            _glViewport(0, 0, width, height);
            {
                GLenum drawBuffers[] = {GL_COLOR_ATTACHMENT0};
                _glDrawBuffers(1, drawBuffers);
            }
            _glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            _glClear(GL_COLOR_BUFFER_BIT);
            _glDisable(GL_DEPTH_TEST);
            while (_glGetError() != GL_NO_ERROR) {}

            // Make sure gTempDepthTex has correct sampling params
            _glBindTexture(GL_TEXTURE_2D, gTempDepthTex);
            _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_NONE);
            _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            _glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

            _glUseProgram(gDepthToColorProgram);
            _glActiveTexture(GL_TEXTURE0);
            _glBindTexture(GL_TEXTURE_2D, gTempDepthTex);
            GLint dLoc = _glGetUniformLocation(gDepthToColorProgram, "u_depthTexture");
            _glUniform1i(dLoc, 0);
            GLint pLoc = _glGetAttribLocation(gDepthToColorProgram, "a_position");
            GLint tLoc = _glGetAttribLocation(gDepthToColorProgram, "a_texCoord");
            _glVertexAttribPointer(pLoc, 3, GL_FLOAT, GL_FALSE, 0, vertices);
            _glVertexAttribPointer(tLoc, 2, GL_FLOAT, GL_FALSE, 0, texCoords);
            _glEnableVertexAttribArray(pLoc);
            _glEnableVertexAttribArray(tLoc);
            _glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

            GLenum shaderErr = _glGetError();
            DBG_LOG("Depth overlay: blit+shader draw err=0x%x, dLoc=%d, pLoc=%d, tLoc=%d",
                    shaderErr, dLoc, pLoc, tLoc);

            // Verify shader output via COLOR glReadPixels (this works on this device!)
            GLubyte bpixel[4] = {0, 0, 0, 0};
            _glBindFramebuffer(GL_READ_FRAMEBUFFER, gDepthBlitFBO);
            _glReadPixels(width/2, height/2, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, bpixel);
            DBG_LOG("DIAG blit+shader: gDepthBlitFBO center R=%d G=%d B=%d A=%d",
                    bpixel[0], bpixel[1], bpixel[2], bpixel[3]);

            if (bpixel[0] > 0 || bpixel[1] > 0 || bpixel[2] > 0) {
                depthReadOk = true;
                DBG_LOG("Depth overlay: blit+shader SUCCESS!");
            } else {
                DBG_LOG("Depth overlay: blit succeeded but shader output is still 0");
            }
        }
        while (_glGetError() != GL_NO_ERROR) {}
    }

    if (!depthReadOk) {
        DBG_LOG("Depth overlay: all depth methods failed, showing game only");
    }

    while (_glGetError() != GL_NO_ERROR) {}

    // --- Overlay assembly ---
    _glBindFramebuffer(GL_FRAMEBUFFER, gOverlayFBO);
    CHECK_FBO_STATUS(gOverlayFBO, "gOverlayFBO");
    DO_CHECK_GL_ERROR("after bind gOverlayFBO");
    _glViewport(0, 0, width, height);
    _glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    _glClear(GL_COLOR_BUFFER_BIT);
    DO_CHECK_GL_ERROR("after clear gOverlayFBO");
    
    _glDisable(GL_SCISSOR_TEST);

    // DIAGNOSTIC: verify game content exists in currentFBO before blit
    {
        GLubyte pixel[4] = {0, 0, 0, 0};
        _glBindFramebuffer(GL_READ_FRAMEBUFFER, currentFBO);
        if (currentFBO == 0) _glReadBuffer(GL_BACK);
        else _glReadBuffer(GL_COLOR_ATTACHMENT0);
        _glReadPixels(width/2, height/2, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, pixel);
        GLenum diagErr = _glGetError();
        DBG_LOG("DIAG game content: currentFBO=%d center R=%d G=%d B=%d A=%d, err=0x%x",
                currentFBO, pixel[0], pixel[1], pixel[2], pixel[3], diagErr);
    }

    DBG_LOG("Depth overlay: first blit - read from currentFBO=%d to gOverlayFBO left half", currentFBO);
    _glBindFramebuffer(GL_READ_FRAMEBUFFER, currentFBO);
    _glBindFramebuffer(GL_DRAW_FRAMEBUFFER, gOverlayFBO);
    if (currentFBO == 0) {
        _glReadBuffer(GL_BACK);
    } else {
        _glReadBuffer(GL_COLOR_ATTACHMENT0);
    }
    {
        GLenum drawBuffers[] = {GL_COLOR_ATTACHMENT0};
        _glDrawBuffers(1, drawBuffers);
    }
    _glBlitFramebuffer(0, 0, width, height, 0, 0, width/2, height, GL_COLOR_BUFFER_BIT, GL_LINEAR);
    DO_CHECK_GL_ERROR("after first blit (original color to left)");
    
    DBG_LOG("Depth overlay: second blit - read from gDepthBlitFBO=%d to gOverlayFBO right half", gDepthBlitFBO);
    _glBindFramebuffer(GL_READ_FRAMEBUFFER, gDepthBlitFBO);
    _glBlitFramebuffer(0, 0, width, height, width/2, 0, width, height, GL_COLOR_BUFFER_BIT, GL_LINEAR);
    DO_CHECK_GL_ERROR("after second blit (depth to right)");

    // DIAGNOSTIC: verify overlay FBO has both halves
    {
        GLubyte pixelL[4] = {0, 0, 0, 0}, pixelR[4] = {0, 0, 0, 0};
        _glBindFramebuffer(GL_READ_FRAMEBUFFER, gOverlayFBO);
        _glReadBuffer(GL_COLOR_ATTACHMENT0);
        _glReadPixels(width/4, height/2, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, pixelL);
        _glReadPixels(width*3/4, height/2, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, pixelR);
        GLenum diagErr = _glGetError();
        DBG_LOG("DIAG overlay: left(%d,%d) R=%d G=%d B=%d  right(%d,%d) R=%d G=%d B=%d  err=0x%x",
                width/4, height/2, pixelL[0], pixelL[1], pixelL[2],
                width*3/4, height/2, pixelR[0], pixelR[1], pixelR[2], diagErr);
    }
    
    DBG_LOG("Depth overlay: final blit - from gOverlayFBO=%d to savedFBO=%d", gOverlayFBO, savedFBO);
    _glBindFramebuffer(GL_READ_FRAMEBUFFER, gOverlayFBO);
    _glBindFramebuffer(GL_DRAW_FRAMEBUFFER, savedFBO);
    _glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_LINEAR);
    DO_CHECK_GL_ERROR("after final blit to saved framebuffer");
    
    #ifdef DEPTH_OVERLAY_DEBUG
    {
        GLubyte pixel[4];
        _glBindFramebuffer(GL_READ_FRAMEBUFFER, savedFBO);
        _glReadPixels(width/4, height/2, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, pixel);
        DBG_LOG("Depth overlay: savedFBO pixel at (%d,%d) R=%d,G=%d,B=%d,A=%d", width/4, height/2, pixel[0], pixel[1], pixel[2], pixel[3]);
        DO_CHECK_GL_ERROR("after read savedFBO pixel");

        _glBindFramebuffer(GL_READ_FRAMEBUFFER, gDepthBlitFBO);
        _glReadPixels(width/4, height/2, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, pixel);
        DBG_LOG("Depth overlay: gDepthBlitFBO pixel at (%d,%d) R=%d,G=%d,B=%d,A=%d", width/4, height/2, pixel[0], pixel[1], pixel[2], pixel[3]);
        DO_CHECK_GL_ERROR("after read gDepthBlitFBO pixel");

        _glBindFramebuffer(GL_READ_FRAMEBUFFER, gOverlayFBO);
        _glReadPixels(width/4, height/2, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, pixel);
        DBG_LOG("Depth overlay: gOverlayFBO left pixel at (%d,%d) R=%d,G=%d,B=%d,A=%d", width/4, height/2, pixel[0], pixel[1], pixel[2], pixel[3]);
        DO_CHECK_GL_ERROR("after read gOverlayFBO left pixel");

        _glReadPixels(width*3/4, height/2, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, pixel);
        DBG_LOG("Depth overlay: gOverlayFBO right pixel at (%d,%d) R=%d,G=%d,B=%d,A=%d", width*3/4, height/2, pixel[0], pixel[1], pixel[2], pixel[3]);
        DO_CHECK_GL_ERROR("after read gOverlayFBO right pixel");
    }
    #endif

    // Temp depth resources are now cached, no per-frame cleanup needed
    
    auto frameEnd = std::chrono::high_resolution_clock::now();
    gFrameTimeMs = std::chrono::duration<float, std::milli>(frameEnd - frameStart).count();
    gFrameCount++;
    
    if (gFrameCount % 60 == 0) {
        DBG_LOG("Depth overlay FPS: %.1f, frame time: %.2f ms, frameID=%d", 
                1000.0f / gFrameTimeMs, gFrameTimeMs, gFrameCount);
    }

    _glBindFramebuffer(GL_FRAMEBUFFER, savedFBO);
    _glUseProgram(savedProgram);
    _glViewport(savedViewport[0], savedViewport[1], savedViewport[2], savedViewport[3]);
    _glBindBuffer(GL_ARRAY_BUFFER, savedArrayBuffer);
    _glBindTexture(GL_TEXTURE_2D, savedTexture2D);
    _glActiveTexture(savedActiveTexture);
    if (savedScissorTest) _glEnable(GL_SCISSOR_TEST); else _glDisable(GL_SCISSOR_TEST);
    if (savedBlend) _glEnable(GL_BLEND); else _glDisable(GL_BLEND);
    if (savedDepthTest) _glEnable(GL_DEPTH_TEST); else _glDisable(GL_DEPTH_TEST);
    if (savedStencilTest) _glEnable(GL_STENCIL_TEST); else _glDisable(GL_STENCIL_TEST);
    _glColorMask(savedColorWriteMask[0], savedColorWriteMask[1], savedColorWriteMask[2], savedColorWriteMask[3]);

    // Restore vertex attrib enable states
    if (savedAttribEnable) {
        for (int i = 0; i < maxAttribs; i++) {
            if (savedAttribEnable[i]) _glEnableVertexAttribArray(i);
            else _glDisableVertexAttribArray(i);
        }
        delete[] savedAttribEnable;
    }
}

void CleanupDepthOverlay() {
    DBG_LOG("Depth overlay: CleanupDepthOverlay, FBO=(%d,%d), Tex=(%d,%d), Progs=(%d,%d)",
            gOverlayFBO, gDepthBlitFBO, gOverlayColorTex, gDepthBlitColorTex,
            gSideBySideProgram, gDepthToColorProgram);
    if (gOverlayFBO) { _glDeleteFramebuffers(1, &gOverlayFBO); gOverlayFBO = 0; }
    if (gOverlayColorTex) { _glDeleteTextures(1, &gOverlayColorTex); gOverlayColorTex = 0; }
    if (gDepthBlitFBO) { _glDeleteFramebuffers(1, &gDepthBlitFBO); gDepthBlitFBO = 0; }
    if (gDepthBlitColorTex) { _glDeleteTextures(1, &gDepthBlitColorTex); gDepthBlitColorTex = 0; }
    if (gSideBySideProgram) { _glDeleteProgram(gSideBySideProgram); gSideBySideProgram = 0; }
    if (gDepthToColorProgram) { _glDeleteProgram(gDepthToColorProgram); gDepthToColorProgram = 0; }
    CleanupTempDepthResources();
    gOverlayInitDone = GL_FALSE;
    gOverlayWidth = 0;
    gOverlayHeight = 0;
    gStableFrameCount = 0;
    DBG_LOG("Depth overlay: cleanup done");
}

void UpdateDepthOverlay(int width, int height) {
    std::lock_guard<std::mutex> lock(gOverlayMutex);
    
    bool fileTriggered = isDepthOverlayTriggered();
    
    if (fileTriggered && !gDepthOverlayEnabled) {
        gDepthOverlayEnabled = true;
        DBG_LOG("Depth overlay: ENABLED by trigger file, width=%d, height=%d", width, height);
        InitDepthOverlay(width, height);
    } else if (!fileTriggered && gDepthOverlayEnabled) {
        gDepthOverlayEnabled = false;
        DBG_LOG("Depth overlay: DISABLED, trigger file removed");
    }
    
    if (gDepthOverlayEnabled) {
        if (width == gLastStableWidth && height == gLastStableHeight) {
            gStableFrameCount++;
        } else {
            if (gLastStableWidth != 0 || gLastStableHeight != 0) {
                DBG_LOG("Depth overlay: resolution changed %dx%d -> %dx%d, reset stable count", 
                        gLastStableWidth, gLastStableHeight, width, height);
            }
            gStableFrameCount = 0;
            gLastStableWidth = width;
            gLastStableHeight = height;
        }
        
        if (gStableFrameCount >= DEPTH_OVERLAY_STABLE_FRAME_THRESHOLD) {
            if (!gOverlayInitDone || gOverlayWidth != width || gOverlayHeight != height) {
                InitDepthOverlay(width, height);
            }
            RenderDepthOverlay(width, height);
        } else {
            if (gFrameCount % 60 == 0) {
                DBG_LOG("Depth overlay: waiting for stable frames %d/%d", 
                        gStableFrameCount, DEPTH_OVERLAY_STABLE_FRAME_THRESHOLD);
            }
        }
    }
}
