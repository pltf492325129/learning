#include <thread>
#include <unistd.h>
#include <vector>
#include <sys/stat.h>
#include <GLES3/gl3.h>
#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <cmath>
#include <native_window/external_window.h>
#include "common/logger.h"
#include "load_texture.hpp"
#include "tools.hpp"

#define USE_NATIVE_WINDOW true;
OHNativeWindow *g_native_window;
EGLDisplay display;
EGLSurface surface = EGL_NO_SURFACE;
EGLContext context;

// 左右眼离屏渲染 FBO (1920x874)
const int PARALLAX_WIDTH = 1920;
const int PARALLAX_HEIGHT = 874;
GLuint leftEyeFBO = 0, rightEyeFBO = 0;
GLuint leftEyeTex = 0, rightEyeTex = 0;
const char* PARALLAX_SAVE_PATH_FMT = "/data/storage/el2/base/haps/parallaxResults/parallax_%05d_%s.bmp";

int debugValue = 0;
//int TOTAL_FRAMES = 965;
int TOTAL_FRAMES = 965;
int g_frameID = 0;
const char* COLOR_PATH_FMT = "/data/storage/el2/base/haps/inputs/results/color_%d.ppm";
const char* DEPTH_PATH_FMT = "/data/storage/el2/base/haps/inputs/results/depth_%d.bin";
//const char* COLOR_PATH_FMT = "/data/storage/el2/base/haps/color_%d.ppm";
//const char* DEPTH_PATH_FMT = "/data/storage/el2/base/haps/depth_%d.bin";

const double PI = 3.14159265358979323846;
GLuint program = 0;
GLuint vao = 0, vbo = 0;
GLuint colorTex = 0, depthTex = 0;

GLint loc_ColorTex, loc_DepthTex;
GLint loc_AI, loc_DLR, loc_Resolution, loc_PatternType, loc_FocusPlane;
GLint loc_DebugMode;
GLint loc_NearPlane, loc_FarPlane;

void initParallaxFBOs() {
    LOGW("xr initParallaxFBOs START");
    
    // 创建左眼 FBO
    glGenFramebuffers(1, &leftEyeFBO);
    glGenTextures(1, &leftEyeTex);
    glBindTexture(GL_TEXTURE_2D, leftEyeTex);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, PARALLAX_WIDTH, PARALLAX_HEIGHT, 0, GL_RGB, GL_UNSIGNED_BYTE, nullptr);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    
    glBindFramebuffer(GL_FRAMEBUFFER, leftEyeFBO);
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, leftEyeTex, 0);
    GLenum status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
    if (status != GL_FRAMEBUFFER_COMPLETE) {
        LOGW("leftEyeFBO incomplete: 0x%x", status);
    }
    
    // 创建右眼 FBO
    glGenFramebuffers(1, &rightEyeFBO);
    glGenTextures(1, &rightEyeTex);
    glBindTexture(GL_TEXTURE_2D, rightEyeTex);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, PARALLAX_WIDTH, PARALLAX_HEIGHT, 0, GL_RGB, GL_UNSIGNED_BYTE, nullptr);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    
    glBindFramebuffer(GL_FRAMEBUFFER, rightEyeFBO);
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, rightEyeTex, 0);
    status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
    if (status != GL_FRAMEBUFFER_COMPLETE) {
        LOGW("rightEyeFBO incomplete: 0x%x", status);
    }
    
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    getErrInfo("initParallaxFBOs");
    LOGW("xr initParallaxFBOs END: leftFBO=%{public}d rightFBO=%{public}d", leftEyeFBO, rightEyeFBO);
}

void initTextures() {
    LOGW("xr zpp initTextures start");
    glGenTextures(1, &colorTex);
    glBindTexture(GL_TEXTURE_2D, colorTex);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, WIDTH, HEIGHT, 0, GL_RGB, GL_UNSIGNED_BYTE, nullptr);
    getErrInfo("createTextures");
    
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    glGenTextures(1, &depthTex);
    glBindTexture(GL_TEXTURE_2D, depthTex);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_R32F, WIDTH, HEIGHT, 0, GL_RED, GL_FLOAT, nullptr);
    getErrInfo("initTextures");
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    getErrInfo("depthTex init");

    LOGW("xr zpp initTextures END colorTex:%{public}d depthTex:%{public}d", colorTex, depthTex);
    
    // 初始化左右眼离屏渲染 FBO
    initParallaxFBOs();
}

void updateTextures(int frameID){
    Timer t("updateTextures", g_frameID);
    char colorPath[256], depthPath[256];
    snprintf(colorPath, sizeof(colorPath), COLOR_PATH_FMT, frameID);
    snprintf(depthPath, sizeof(depthPath), DEPTH_PATH_FMT, frameID);

    LOGW("Loading frame %{public}d: color=%{public}s, depth=%{public}s", frameID, colorPath, depthPath);
    
    auto colorImg = loadPPM(colorPath);
    auto depthImg = loadDepthD24S8(depthPath, WIDTH, HEIGHT);
    
    // === 调试：打印深度值 ===
    LOGW("=== Depth Debug ===");
    LOGW("depthImg.depth.size() = %{public}zu, expected = %{public}d", depthImg.depth.size(), WIDTH * HEIGHT);
    for (int i = 0; i < 10; i++) {
        LOGW("depth[%{public}d] = %{public}f", i, depthImg.depth[i]);
    }
    // === 调试结束 ===
    
     // Update color texture
    LOGW("xr updateTextures colorTex:%{public}d depthTex:%{public}d", colorTex, depthTex);
    glBindTexture(GL_TEXTURE_2D, colorTex);
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, WIDTH, HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, colorImg.data.data());
    getErrInfo("updateTextures0");   
    // Update depth texture (使用GL_RED格式，与GL_R32F匹配)
    glBindTexture(GL_TEXTURE_2D, depthTex);
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, WIDTH, HEIGHT, GL_RED, GL_FLOAT, depthImg.depth.data());
    getErrInfo("updateTextures depthTex");   
    
}

GLuint compileShader(GLenum type, const char *source) {
    LOGW("xr zpp compileShader start");
    GLuint shader = glCreateShader(type);
    glShaderSource(shader, 1, &source, nullptr);
    glCompileShader(shader);

    GLint success;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &success);

    if (!success) {
        GLchar infoLog[512];
        glGetShaderInfoLog(shader, 512, nullptr, infoLog);
        LOGE("Shader compile error: %{public}s", infoLog);
        getErrInfo("compileShader");
    }
    return shader;
}

void initShaders() {
    LOGW("xr zpp initShaders start");
    const char *vertexShaderSource = R"(#version 300 es
precision highp float;
in vec2 a_position;
out vec2 v_uv;
void main() {
    v_uv = a_position * 0.5 + 0.5; //将坐标从[-1,1]归一化到[0,1]
    gl_Position = vec4(a_position, 0.0, 1.0);
}
    )";
    const char *fragmentShaderSource = R"(#version 300 es
    precision highp float;
    in vec2 v_uv;
    out vec4 out_color;
    
    uniform sampler2D ColorTex;
    uniform sampler2D DepthTex;
    uniform float AI;              // = f * IPD/2 (像素 × 米)
    uniform vec2 DLR;
    uniform vec2 Resolution;
    uniform int PatternType;
    uniform float FocusPlane;      // 归一化深度值 [0, 1]
    uniform int DebugMode;
    uniform float NearPlane;       // 近裁剪面 (米)
    uniform float FarPlane;        // 远裁剪面 (米)
    // 深度归一化值转真实距离 (米)
    // depthNorm: 0 = 最远, 最大值 = 最近
    float depthToDistance(float depthNorm) {
        // 线性插值: depth=0 → FarPlane, depth=1 → NearPlane
        return (1.0 - depthNorm) * FarPlane + depthNorm * NearPlane;
    }
    float ndcToLinearZ(float d) {
        // 防止除零：d 不应为 1.0（无穷远）
        return (NearPlane * FarPlane) /
               (NearPlane + d * (FarPlane - NearPlane));
    }    
    float reversedZToLinear(float d) {
        // 防止除零（天空区域 d 极小）
        if (d < 0.001) return FarPlane;  // 视为无限远
        return NearPlane / d;            // Reversed-Z infinite far
    }    
    float depthToZ(float d) {
        if (d < 0.001) return FarPlane;
        return 0.25001 / (0.000042 + d);
    }
    
    // --- 核心：DIBR 视差位移与空洞修复函数 ---
    // 返回值：修复空洞后的源纹理 UV 坐标
    vec2 getWarpedUV(vec2 target_uv, bool isRightEye, float MAX_PIXEL_DISPARITY) {
        // 左眼通常作为基准帧，不位移（或者位移量为 -disparity/2，取决于你的相机模型）
        float eyeSign = isRightEye ? 1.0 : -1.0;
    
        float invZ_focus = 1.0 / reversedZToLinear(FocusPlane);
//        float max_disp_uv = (MAX_PIXEL_DISPARITY * 2.0) / Resolution.x;
        float max_disp_uv = MAX_PIXEL_DISPARITY / Resolution.x;
    
        vec2 best_src_uv = target_uv;
        float min_diff = 100.0; 
        float furthest_Z = 0.0; 
        vec2 background_uv = target_uv; 
    
        // 极线搜索：寻找哪个源像素位移后能落在当前 target_uv 上
        const int SEARCH_STEPS = 12*2; 
        for (int i = 0; i < SEARCH_STEPS; i++) {
            float t = (float(i) / float(SEARCH_STEPS - 1)) * 2.0 - 1.0; 
            vec2 sample_uv = target_uv + vec2(t * max_disp_uv, 0.0);
            sample_uv = clamp(sample_uv, vec2(0.0), vec2(1.0));
    
            float d_norm = texture(DepthTex, sample_uv).r;
            float sample_Z = reversedZToLinear(d_norm);
    
            // 策略：记录搜索范围内最远的背景点，用于空洞填充
            if (sample_Z > furthest_Z) {
                furthest_Z = sample_Z;
                background_uv = sample_uv;
            }
    
            // 计算该采样点的视差位移量
            // $disparity = \frac{f \cdot IPD}{2} \cdot (\frac{1}{Z} - \frac{1}{Z_{focus}})$
            float sample_disp_px = eyeSign * AI * (1.0 / sample_Z - invZ_focus);
            sample_disp_px = clamp(sample_disp_px, -MAX_PIXEL_DISPARITY, MAX_PIXEL_DISPARITY);
            float sample_disp_uv = sample_disp_px / Resolution.x;
    
            // 核心逻辑：重投影匹配
            float projected_x = sample_uv.x - sample_disp_uv;
            float diff = abs(projected_x - target_uv.x);
    
            if (diff < min_diff) {
                min_diff = diff;
                best_src_uv = sample_uv;
            }
        }
    
        // 空洞检测：如果搜索一圈发现重投影误差依然很大，说明该区域是遮挡露出的空洞
        float hole_threshold = 1.5 / Resolution.x;
        if (min_diff > hole_threshold) {
            return background_uv; // 使用最远背景填充
        }
    
        return best_src_uv;
    }    

vec2 steepParallaxDIBR(vec2 target_uv, bool isRightEye, float MAX_PIXEL_DISPARITY) {
    float eyeSign = isRightEye ? 1.0 : -1.0;
    float Z_focus = reversedZToLinear(FocusPlane);
    float invZ_focus = 1.0 / Z_focus;

    const int SEARCH_STEPS = 32;
    // 修复: 去掉错误的 * 2.0
    float max_disp_uv = MAX_PIXEL_DISPARITY / Resolution.x;
    float deltaUV = (2.0 * max_disp_uv) / float(SEARCH_STEPS);

    // Ray march 状态
    vec2 prevUV = target_uv;
    float prevReprojDiff = 0.0;

    // 最佳匹配状态
    vec2 bestUV = target_uv;
    float minAbsDiff = 100.0;

    // 背景跟踪
    float furthestZ = 0.0;
    vec2 backgroundUV = target_uv;

    bool zeroCrossingFound = false;

    // ========================================
    // Step 2: Steep Parallax Ray March
    // ========================================
    // 从搜索范围的一端向另一端逐层 march
    // 方向: right eye → 从 target 右侧向左侧 march
    //       left eye  → 从 target 左侧向右侧 march
    for (int i = 0; i < SEARCH_STEPS; i++) {
        // 当前层对应的 UV 偏移
        float offset = max_disp_uv - float(i) * deltaUV;
        vec2 currentUV = target_uv + vec2(eyeSign * offset, 0.0);
        currentUV = clamp(currentUV, vec2(0.0), vec2(1.0));

        // 采样深度
        float d = texture(DepthTex, currentUV).r;
        float Z = reversedZToLinear(d);

        // 背景跟踪
        if (Z > furthestZ) {
            furthestZ = Z;
            backgroundUV = currentUV;
        }

        // 重投影误差: 这个源像素映射到哪个 target?
        float sample_disp_px = eyeSign * AI * (1.0 / Z - invZ_focus);
        sample_disp_px = clamp(sample_disp_px, -MAX_PIXEL_DISPARITY, MAX_PIXEL_DISPARITY);
        float sample_disp_uv = sample_disp_px / Resolution.x;

        float projected_x = currentUV.x + sample_disp_uv;
        float reprojDiff = projected_x - target_uv.x;

        // ========================================
        // Step 3: 零交叉检测 + POM 插值
        // ========================================
        // 等价于 SuperDepth3D 中 while 循环终止后的 POM 精炼
        if (i > 0 && prevReprojDiff * reprojDiff < 0.0) {
            // POM 线性插值权重
            // 对应: weight = afterDepthValue / (afterDepthValue - beforeDepthValue)
            float weight = abs(prevReprojDiff) /
                           (abs(prevReprojDiff) + abs(reprojDiff) + 0.0001);
            weight = clamp(weight, 0.0, 1.0);

            // 对应: ParallaxCoord = Prev * weight + Current * (1 - weight)
            return prevUV * weight + currentUV * (1.0 - weight);
        }

        // 更新最佳匹配
        float absDiff = abs(reprojDiff);
        if (absDiff < minAbsDiff) {
            minAbsDiff = absDiff;
            bestUV = currentUV;
        }

        prevReprojDiff = reprojDiff;
        prevUV = currentUV;
    }

    // ========================================
    // 空洞检测与背景填充
    // ========================================
    float holeThreshold = 1.5 / Resolution.x;
    if (minAbsDiff > holeThreshold) {
        // 空洞区域: 使用最远的背景像素
        return backgroundUV;
    }

    return bestUV;
}
    
    
    void main() {
        vec2 uv = v_uv;
        float depthNorm = texture(DepthTex, uv).r;
        vec3 color;
    
    // 调试深度图1=========================================
        if (DebugMode == 1) {
            out_color = vec4(vec3(depthNorm), 1.0);
            return;
        }
    
        // 将归一化深度转换为真实距离
        float Z = reversedZToLinear(depthNorm);
        float Z_focus = reversedZToLinear(FocusPlane);
    
        // 正确的视差公式: disparity = IPD/2 * f * (1/Z - 1/Z_focus) * Scale
        // AI = f * IPD/2，所以 disparity_px = AI * (1/Z - 1/Z_focus)
        float invZ = 1.0 / Z;
        float invZ_focus = 1.0 / Z_focus;
        float disparity_px = AI * (invZ - invZ_focus);

        // 限制最大视差 (像素)
        const float MAX_PIXEL_DISPARITY = 30.0;
        disparity_px = clamp(disparity_px, -MAX_PIXEL_DISPARITY, MAX_PIXEL_DISPARITY);
    
        // 转为 UV 偏移
        float disparity = disparity_px / Resolution.x;
        vec2 offset = vec2(disparity, 0.0);

// 显示左右眼视差图2=========================================
    if (DebugMode == 2) {
        float d = disparity; // 归一化到 [-1, 1]
        vec3 color;
        if (d < 0.0) {
            color = vec3(-d, 0.0, 0.0); // 负视差（左眼偏移）→ 红色
        } else {
            color = vec3(0.0, d, 0.0); // 正视差（右眼偏移）→ 绿色
        }
        out_color = vec4(color, 1.0);
        return;
    }

// 调试：显示 disparity 值5=========================================
    if (DebugMode == 5) {
        // 显示 disparity_px（像素值），归一化到 [0,1]
        float dispNorm = (disparity_px + MAX_PIXEL_DISPARITY) / (MAX_PIXEL_DISPARITY*2.0);  // 映射到 [0,1]
        out_color = vec4(vec3(dispNorm), 1.0);
        return;
    }

// 调试：显示转换后的距离值6=========================================
    if (DebugMode == 6) {
        float Z_norm = (Z - NearPlane) / (FarPlane - NearPlane);
        out_color = vec4(vec3(Z_norm), 1.0);
        return;
    }

    bool isRightEye = (PatternType == 1);
    vec2 warpedUV = steepParallaxDIBR(v_uv, isRightEye, MAX_PIXEL_DISPARITY);
    color = texture(ColorTex, warpedUV).rgb;
    
//    if (PatternType == 0) {
//        vec2 leftUV = uv - offset;
//        leftUV = clamp(leftUV, vec2(0.0), vec2(1.0));
//        color = texture(ColorTex, leftUV).rgb;        
////        color = texture(ColorTex, leftUV).rgb;
////        vec2 warpedUV = getWarpedUV(v_uv, false, MAX_PIXEL_DISPARITY);
//
//    } else {
////        // 调用提取的函数
////        vec2 warpedUV = getWarpedUV(v_uv, true, MAX_PIXEL_DISPARITY);
////        // 采样最终颜色
////        color = texture(ColorTex, warpedUV).rgb;
//        vec2 rightUV = uv + offset;
//        rightUV = clamp(rightUV, vec2(0.0), vec2(1.0));
//        color = texture(ColorTex, rightUV).rgb;  
//    }        

// 调试深度为0的区域3=========================================
    if (DebugMode == 3) {
        if (depthNorm < 0.01) {
            out_color = vec4(1.0, 0.0, 0.0, 1.0);  // 深度接近0显示红色
        } else {
            out_color = vec4(vec3(depthNorm), 1.0);
        }
        return;
    }
//    4可视化深度depth与color匹配，depth异常 color不匹配====================
    if (DebugMode == 4) {
        // 重新采样 color 以便调试
        vec3 debugColor = texture(ColorTex, uv).rgb;
        float colorIntensity = length(debugColor);
        if (depthNorm < 0.01 && colorIntensity > 0.1) {
             out_color = vec4(colorIntensity, 0.0, 0.0, 1.0); //红色
        } else {
            out_color = vec4(debugColor, 1.0);
        }
        return;
    }
    
    out_color = vec4(color, 1.0);
}
    )";
    
    auto timestamp = std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now().time_since_epoch()).count();
    GLuint vs = compileShader(GL_VERTEX_SHADER, vertexShaderSource);
    GLuint fs = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource);
    auto timestamp1 = std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now().time_since_epoch()).count();
    LOGW("xr compileShader count=%{public}d time:%{public}d", 0, timestamp1 - timestamp);

    program = glCreateProgram();
    glAttachShader(program, vs);
    glAttachShader(program, fs);
    glLinkProgram(program);
    
    loc_DebugMode = glGetUniformLocation(program, "DebugMode");
    loc_ColorTex = glGetUniformLocation(program, "ColorTex");
    loc_DepthTex = glGetUniformLocation(program, "DepthTex");
    loc_AI = glGetUniformLocation(program, "AI");
    loc_DLR = glGetUniformLocation(program, "DLR");
    loc_Resolution = glGetUniformLocation(program, "Resolution");
    loc_PatternType = glGetUniformLocation(program, "PatternType");
    loc_FocusPlane = glGetUniformLocation(program, "FocusPlane");
    loc_NearPlane = glGetUniformLocation(program, "NearPlane");
    loc_FarPlane = glGetUniformLocation(program, "FarPlane");

    // 打印所有 uniform 位置用于调试
    LOGW("xr Uniforms: DebugMode=%d, AI=%d, FocusPlane=%d, NearPlane=%d, FarPlane=%d",
         loc_DebugMode, loc_AI, loc_FocusPlane, loc_NearPlane, loc_FarPlane);
}

void initQuad() {
    const GLfloat quad[] = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};

    glGenVertexArrays(1, &vao);
    glGenBuffers(1, &vbo);
    glBindVertexArray(vao);
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferData(GL_ARRAY_BUFFER, sizeof(quad), quad, GL_STATIC_DRAW);
    glVertexAttribPointer(0, 2, GL_FLOAT, GL_FALSE, 0, 0);
    glEnableVertexAttribArray(0);
}

void render() {
    Timer t("render", g_frameID);
    LOGW("xr render start");
    GLint currentFBO;
    glGetIntegerv(GL_DRAW_FRAMEBUFFER_BINDING, &currentFBO);
    LOGW("Rendering to FBO: %{public}d", currentFBO);
    
    GLint depthBits;
    glGetIntegerv(GL_DEPTH_BITS, &depthBits);
    if (depthBits == 0) {
        LOGW("FATAL: Depth buffer not created! (GL_DEPTH_BITS=0)");
    }
    LOGW("zpp Depth depthBits created! GL_DEPTH_BITS=%{public}d", depthBits);
    
    
    glUseProgram(program);
    LOGW("xr zpp render start1");

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, colorTex);
    glUniform1i(loc_ColorTex, 0);

    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, depthTex);
    
    LOGW("xr zpp render start2");
    glUniform1i(loc_DepthTex, 1);
    glUniform1i(loc_DebugMode, debugValue);  //调试模式

    // 深度到距离转换参数
    const float NearPlane = 0.1f;   // 近裁剪面 0.1m    
//    const float NearPlane = 0.25f;   // 近裁剪面 Unity 出入的 _ProjectionParams 参数    
    const float FarPlane = 6000.0f;  // 远裁剪面 300m
    const float FocusPlane = 0.503f;  // 聚焦平面 (归一化深度值，对应约 50m)
//    const float FocusPlane = 0.2f;  // 聚焦平面 (归一化深度值，对应约 50m)
//    const float FocusPlane = 0.002f;  // 聚焦平面 (归一化深度值，对应约 50m)
    
//Projection = (1.00, 0.25, 6000.00, 0.00017)
    
    // 计算焦距和 AI (f * IPD/2)
    float angle = 50.0;   //FOV=50度
    float radian = angle * (PI / 180.0);
    float focalLength = WIDTH / (2.0f * std::tan(radian/2.0f));  // 焦距(像素)
    float IPD = 0.064f;  // 瞳孔间距 64mm
    float AI = focalLength * (IPD / 2.0f);  // f * IPD/2

    // 计算聚焦平面对应的真实距离用于日志
    float Z_focus = (1.0f - FocusPlane) * FarPlane + FocusPlane * NearPlane;
    
    float Z_target = 4.0f;  // 
//    float FocusPlane = FarPlane * (Z_target - NearPlane / (Z_target * (FarPlane - NearPlane)));  // ≈ 0.9757
//    LOGW("xr Z_to_dist: FocusPlane2=%{public}f", FocusPlane2);
//    float Z_focus = FarPlane * (Z_target - NearPlane / (Z_target * (FarPlane - NearPlane)));  // ≈ 0.9757
    float reverzed_z0 = NearPlane/FocusPlane;
    
    LOGW("xr Focus: focalLength=%{public}f, IPD=%{public}f, AI=%{public}f", focalLength, IPD, AI);
    LOGW("xr Z_to_dist: NearPlane=%{public}f, FarPlane=%{public}f, FocusPlane_norm=%{public}f -> Z=%{public}f rev_z0=%{public}f ",
         NearPlane, FarPlane, FocusPlane, Z_focus, reverzed_z0);

    glUniform1f(loc_AI, AI);
    glUniform2f(loc_DLR, 0.0f, 0.02f);
    glUniform1f(loc_FocusPlane, FocusPlane);
    glUniform1f(loc_NearPlane, NearPlane);
    glUniform1f(loc_FarPlane, FarPlane);
    glUniform2f(loc_Resolution, WIDTH, HEIGHT);  //这里是纹理分辨率
   

    glBindVertexArray(vao);
    LOGW("xr zpp render start3");
    // Left eye
    glUniform1i(loc_PatternType, 0);
    glViewport(0, 0, SCREEN_WIDTH / 2 - testWidth, SCREEN_HEIGHT);
    LOGW("xr zpp render start3.1");
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    LOGW("xr zpp render start3.2");
    // Right eye
    glUniform1i(loc_PatternType, 1);
    glViewport(SCREEN_WIDTH / 2 - testWidth, 0, SCREEN_WIDTH / 2, SCREEN_HEIGHT);
    glGetError(); // 清除之前的错误
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    getErrInfo("glDrawArrays info");

    // 保存左右眼输出图
//    saveLREyeImages(SCREEN_WIDTH, SCREEN_HEIGHT, "/data/storage/el2/base/haps/eye", testWidth);

    LOGW("xr zpp render end...");
}

int initEgl() {
    LOGW("xr initEgl xrmain start");
    display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (display == EGL_NO_DISPLAY) {
        LOGW("xr initEgl eglGetDisplay failed");
        return -1;
    }
    LOGW("xr initEgl xrmain start1");
    if (!eglInitialize(display, nullptr, nullptr)) {
        LOGW("xr eglInitialize failed");
        return -1;
    }
    LOGW("xr zpp xrmain start2");
    EGLint configAttribs[] = {EGL_SURFACE_TYPE,
#ifdef USE_NATIVE_WINDOW
                              EGL_WINDOW_BIT,
#else
                               EGL_PBUFFER_BIT,
#endif
                              EGL_BLUE_SIZE,    8, EGL_GREEN_SIZE, 8,  EGL_RED_SIZE,        8,
                              EGL_ALPHA_SIZE,   8, EGL_DEPTH_SIZE, 24, EGL_RENDERABLE_TYPE, EGL_OPENGL_ES3_BIT,
                              EGL_NONE};
    EGLConfig config;
    EGLint numConfigs;
    if (!eglChooseConfig(display, configAttribs, &config, 1, &numConfigs) || numConfigs == 0) {
        getErrInfo("xrmain config test ***********");
        return -1;
    }
    LOGW("xr zpp xrmain start4");
#ifdef USE_NATIVE_WINDOW
    LOGW("xr zpp eglGetDisplay USE_NATIVE_WINDOW");
    if (!g_native_window) {
        LOGW("xr g_native_window is null!");
        return -1;
    }
    LOGW("xr zpp xrmain start5");
    surface = eglCreateWindowSurface(display, config, (EGLNativeWindowType)g_native_window, nullptr);
    LOGW("xr zpp xrmain start6");
    EGLenum error = eglGetError();
    if (surface == EGL_NO_SURFACE || error != EGL_SUCCESS) {
        LOGE("xr eglCreateWindowSurface failed 111");
        return 0;
    }    
#else
    EGLint pbufferAttribs[] = {EGL_WIDTH, WIDTH, EGL_HEIGHT, HEIGHT, EGL_NONE};
    surface = eglCreatePbufferSurface(display, config, pbufferAttribs);
    if (surface == EGL_NO_SURFACE) {
        LOGW("xr eglCreatePbufferSurface failed");
        return -1;
    }
#endif
    EGLint contextAttribs[] = {EGL_CONTEXT_CLIENT_VERSION, 3, EGL_NONE};
    context = eglCreateContext(display, config, EGL_NO_CONTEXT, contextAttribs);
    if (context == EGL_NO_CONTEXT) {
        LOGW("xr eglCreateContext failed");
        return -1;
    }
    LOGW("xr zpp xrmain start7");
    if (!eglMakeCurrent(display, surface, surface, context)) {
        LOGW("xr eglMakeCurrent failed");
        return -1;
    }
    LOGW("xr zpp xrmain start8");

    // ========== 初始化 OpenGL 资源 ==========
    std::vector<unsigned char> colorData;
    std::vector<float> depthData;
    LOGW("xr zpp xrmain start9");
    initTextures();
    getErrInfo("createTextures");
    initShaders();
    LOGW("xr zpp xrmain start11");
    getErrInfo("initShaders");
    initQuad();
    LOGW("xr initEgl end...");
    return 1;
}
void destoryEgl(){
    // ========== Cleanup ==========
    // 清理离屏渲染资源
    glDeleteFramebuffers(1, &leftEyeFBO);
    glDeleteFramebuffers(1, &rightEyeFBO);
    glDeleteTextures(1, &leftEyeTex);
    glDeleteTextures(1, &rightEyeTex);
    
    glDeleteTextures(1, &colorTex);
    glDeleteTextures(1, &depthTex);
    glDeleteVertexArrays(1, &vao);
    glDeleteBuffers(1, &vbo);
    glDeleteProgram(program);

    eglMakeCurrent(display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    eglDestroySurface(display, surface);
    eglDestroyContext(display, context);
    eglTerminate(display);
    LOGW("xr zpp xrmain end");
}

// 使用 mkdir 确保目录存在
static bool ensureDirExists(const char* filePath) {
    char dirPath[256];
    strncpy(dirPath, filePath, sizeof(dirPath) - 1);
    dirPath[sizeof(dirPath) - 1] = 0;
    
    char* p = strrchr(dirPath, '/');
    if (p) {
        *p = '\0';
        // 使用 stat 检查目录是否存在
        struct stat st;
        if (stat(dirPath, &st) != 0) {
            // 尝试递归创建目录
            for (char* pp = dirPath; *pp; pp++) {
                if (*pp == '/') {
                    *pp = '\0';
                    if (stat(dirPath, &st) != 0) {
                        mkdir(dirPath, 0755);
                    }
                    *pp = '/';
                }
            }
            mkdir(dirPath, 0755);
        }
    }
    return true;
}

// 保存为 BMP 格式
void saveParallaxImageBMP(const char* filePath, int width, int height, const uint8_t* pixels) {
    Timer t("saveParallaxImageBMP", -1);
    
    ensureDirExists(filePath);
    
    FILE* fp = fopen(filePath, "wb");
    if (!fp) {
        LOGW("Failed to open %s for writing", filePath);
        return;
    }
    
    // BMP 文件头
    uint8_t header[54] = {0};
    // BM 标识
    header[0] = 'B';
    header[1] = 'M';
    // 文件大小
    uint32_t rowSize = (width * 3 + 3) & ~3;  // 每行字节数（4字节对齐）
    uint32_t dataSize = rowSize * height;
    uint32_t fileSize = 54 + dataSize;
    header[2] = fileSize & 0xFF;
    header[3] = (fileSize >> 8) & 0xFF;
    header[4] = (fileSize >> 16) & 0xFF;
    header[5] = (fileSize >> 24) & 0xFF;
    // 保留字段
    header[6] = header[7] = header[8] = header[9] = 0;
    // 数据偏移
    header[10] = 54;
    // DIB 头大小
    header[14] = 40;
    // 宽度
    header[18] = width & 0xFF;
    header[19] = (width >> 8) & 0xFF;
    header[20] = (width >> 16) & 0xFF;
    header[21] = (width >> 24) & 0xFF;
    // 高度（负值表示自上而下）
    int16_t negHeight = -height;
    header[22] = negHeight & 0xFF;
    header[23] = (negHeight >> 8) & 0xFF;
    header[24] = 0;
    header[25] = 0;
    // 颜色平面数
    header[26] = 1;
    // 每像素位数
    header[28] = 24;
    // 压缩方式（无）
    header[30] = header[31] = header[32] = header[33] = 0;
    // 图像大小
    header[34] = dataSize & 0xFF;
    header[35] = (dataSize >> 8) & 0xFF;
    header[36] = (dataSize >> 16) & 0xFF;
    header[37] = (dataSize >> 24) & 0xFF;
    // 水平分辨率（像素/米）
    header[38] = header[42] = 0x13;
    header[39] = header[43] = 0x0B;
    header[40] = header[44] = 0;
    header[41] = header[45] = 0;
    // 调色板颜色数
    header[46] = header[47] = header[48] = header[49] = 0;
    // 重要颜色数
    header[50] = header[51] = header[52] = header[53] = 0;
    
    fwrite(header, 1, 54, fp);
    
    // 写入像素数据（每行需要 4 字节对齐）
    std::vector<uint8_t> row(rowSize);
    for (int y = 0; y < height; y++) {
        memcpy(row.data(), pixels + y * width * 3, width * 3);
        fwrite(row.data(), 1, rowSize, fp);
    }
    
    fclose(fp);
    LOGW("Saved parallax image: %s (%dx%d)", filePath, width, height);
}

// 渲染并保存左右眼视差图
void renderAndSaveParallaxImages(int frameID) {
    Timer t("renderAndSaveParallax", frameID);
    LOGW("xr renderAndSaveParallax start: frameID=%{public}d, size=%dx%{public}d", frameID, PARALLAX_WIDTH, PARALLAX_HEIGHT);
    
    glUseProgram(program);
    
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, colorTex);
    glUniform1i(loc_ColorTex, 0);

    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, depthTex);
    glUniform1i(loc_DepthTex, 1);
    glUniform1i(loc_DebugMode, 0);
    
    const float NearPlane = 0.1f;
    const float FarPlane = 6000.0f;
    const float FocusPlane = 0.503f;
    
    float angle = 50.0;
    float radian = angle * (PI / 180.0);
    float focalLength = PARALLAX_WIDTH / (2.0f * std::tan(radian/2.0f));
    float IPD = 0.064f;
    float AI = focalLength * (IPD / 2.0f);

    glUniform1f(loc_AI, AI);
    glUniform1f(loc_FocusPlane, FocusPlane);
    glUniform1f(loc_NearPlane, NearPlane);
    glUniform1f(loc_FarPlane, FarPlane);
    glUniform2f(loc_Resolution, PARALLAX_WIDTH, PARALLAX_HEIGHT);
    glBindVertexArray(vao);
    
    // 渲染左眼到左眼 FBO
    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, leftEyeFBO);
    glViewport(0, 0, PARALLAX_WIDTH, PARALLAX_HEIGHT);
    glClear(GL_COLOR_BUFFER_BIT);
    glUniform1i(loc_PatternType, 0);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    glFinish();
    getErrInfo("leftEye draw");
    
    // 读取左眼图像并保存
    std::vector<uint8_t> leftPixels(PARALLAX_WIDTH * PARALLAX_HEIGHT * 3);
    glBindFramebuffer(GL_READ_FRAMEBUFFER, leftEyeFBO);
    glReadPixels(0, 0, PARALLAX_WIDTH, PARALLAX_HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, leftPixels.data());
    getErrInfo("leftEye readPixels");
    LOGW("Left eye: first[0]=%{public}d,[1]=%{public}d,[2]=%{public}d, size=%{public}u, expected=%{public}d", 
         leftPixels[0], leftPixels[1], leftPixels[2], leftPixels.size(), PARALLAX_WIDTH * PARALLAX_HEIGHT * 3);
    
    char leftPath[256];
    snprintf(leftPath, sizeof(leftPath), PARALLAX_SAVE_PATH_FMT, frameID, "left");
    std::thread([=]() {
        saveParallaxImageBMP(leftPath, PARALLAX_WIDTH, PARALLAX_HEIGHT, leftPixels.data());
    }).detach();
    
    // 渲染右眼到右眼 FBO
    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, rightEyeFBO);
    glViewport(0, 0, PARALLAX_WIDTH, PARALLAX_HEIGHT);
    glClear(GL_COLOR_BUFFER_BIT);
    glUniform1i(loc_PatternType, 1);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    glFinish();
    getErrInfo("rightEye draw");
    
    // 读取右眼图像并保存
    std::vector<uint8_t> rightPixels(PARALLAX_WIDTH * PARALLAX_HEIGHT * 3);
    glBindFramebuffer(GL_READ_FRAMEBUFFER, rightEyeFBO);
    glReadPixels(0, 0, PARALLAX_WIDTH, PARALLAX_HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, rightPixels.data());
    getErrInfo("rightEye readPixels");
    LOGW("Right eye: first[0]=%d,[1]=%d,[2]=%d, size=%{public}zu, expected=%d", 
         rightPixels[0], rightPixels[1], rightPixels[2], rightPixels.size(), PARALLAX_WIDTH * PARALLAX_HEIGHT * 3);
    
    char rightPath[256];
    snprintf(rightPath, sizeof(rightPath), PARALLAX_SAVE_PATH_FMT, frameID, "right");
    std::thread([=]() {
        saveParallaxImageBMP(rightPath, PARALLAX_WIDTH, PARALLAX_HEIGHT, rightPixels.data());
    }).detach();
    
    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
    LOGW("xr renderAndSaveParallax done");
}

int xrmain() {
    initEgl();
//    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//    glClearDepthf(1.0f);
//
//    glEnable(GL_DEPTH_TEST);
//    // 创建深度缓冲区（32位浮点，支持glReadPixels读取）
//    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);        
//
//    GLuint fbo = createFBO();
//    if (fbo == 0) {
//        LOGE("Failed to create FBO!");
//    }
    while (1) {
        Timer t0("xrmain loop", g_frameID);
// ========== 渲染一帧 ==========     
// 渲染到 FBO
//        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//        glEnable(GL_DEPTH_TEST);
//        
//        getErrInfo("xrmain0");
//        debugFBO("Before read");
//                  render();
//        debugFBO("After render");
//

//        getErrInfo("xrmain1");
//        queryFBOAttachments(fbo);
//        check_depth_info();
//        
//
//        if (frameID >= 0 && frameID <= 10) {
//            LOGW("zpp Saved frame id:%{public}d/", frameID);
//                // 读取颜色和深度（直接从主FBO读取）
//                std::vector<unsigned char> colorData;
//                std::vector<float> depthData;
//
//                readColorData(WIDTH, HEIGHT, colorData);
//                readDepthData(WIDTH, HEIGHT, depthData);
//
//                // 保存到后台线程
//                 std::thread([=]() {
//                    saveColorMap("/data/storage/el2/base/haps/color1_" + std::to_string(frameID) + ".ppm", colorData, WIDTH, HEIGHT);
//                    saveDepthMapRaw("/data/storage/el2/base/haps/depth1_" + std::to_string(frameID) + ".bin", depthData, WIDTH, HEIGHT);
//                    LOGW("Saved frame %{public}d/", frameID);
//                 }).detach();
//
//        }          
//         // ========== 切换到默认FBO进行显示 ==========
//        glBindFramebuffer(GL_FRAMEBUFFER, 0);
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//
//         将自己FBO的内容绘制到屏幕上
//        glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo);
//        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
//        glBlitFramebuffer(0, 0, WIDTH, HEIGHT, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, GL_COLOR_BUFFER_BIT, GL_NEAREST);
//        
//        g_frameID = 1;
        updateTextures(g_frameID);
        render();
        renderAndSaveParallaxImages(g_frameID);  // 渲染并保存左右眼视差图
        LOGW("xr zpp xrmain start13");
        #ifndef USE_NATIVE_WINDOW
            // 只有离屏时才读取像素保存
            updateTextures(g_frameID);
            render();
            renderAndSaveParallaxImages(g_frameID);  // 渲染并保存左右眼视差图
            std::vector<uint8_t> pixels(WIDTH * HEIGHT * 3);
            glReadPixels(0, 0, WIDTH, HEIGHT, GL_RGB, GL_UNSIGNED_BYTE, pixels.data());
            saveBMP("/data/storage/el2/base/haps/output_stereo.bmp", WIDTH, HEIGHT, pixels);
            LOGW("Saved output_stereo.bmp (left half: original, right half: stereo view)");
        #else
            LOGW("xr eglSwapBuffers start1");
            Timer t("eglSwapBuffers", g_frameID);
            eglSwapBuffers(display, surface); // 显示到屏幕
            g_frameID = (g_frameID + 1) % TOTAL_FRAMES;
//            if (g_frameID == TOTAL_FRAMES - 2) {
//                break;
//            }
                #endif
            }
            destoryEgl();
            return 0;
        }
