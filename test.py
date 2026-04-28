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
//    float LinearEyeDepth(float z) {
//        float zparam = (1 - FarPlane/NearPlane) / FarPlane;
//        float wparam = (FarPlane/NearPlane) / FarPlane;
//        return 1.0 / ( zbuffer * zparam + wparam);
//    }
    float LinearEyeDepth(float d) {
//        return (FarPlane*NearPlane) / (d * (FarPlane - NearPlane) + NearPlane);
        return 1.0 / (d * 3.99983 + 0.000167);
    }
    
    // --- 核心：DIBR 视差位移与空洞修复函数 ---
    // 返回值：修复空洞后的源纹理 UV 坐标
    vec2 getWarpedUV(vec2 target_uv, bool isRightEye, float MAX_PIXEL_DISPARITY) {
        // 左眼通常作为基准帧，不位移（或者位移量为 -disparity/2，取决于你的相机模型）
        float eyeSign = isRightEye ? 1.0 : -1.0;
    
        float invZ_focus = 1.0 / reversedZToLinear(FocusPlane);
//        float invZ_focus = 1.0 / FocusPlane;
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
            float sample_Z = LinearEyeDepth(d_norm);
    
            // 策略：记录搜索范围内最远的背景点，用于空洞填充
            if (sample_Z > furthest_Z) {
                furthest_Z = sample_Z;
                background_uv = sample_uv;
            }
    
            // 计算该采样点的视差位移量
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
//        float Z_norm = (Z - NearPlane) / (FarPlane - NearPlane);
        float Z_norm = LinearEyeDepth(depthNorm);
        out_color = vec4(vec3(Z_norm), 1.0);
        return;
    }

    bool isRightEye = (PatternType == 1);
    
    vec2 warpedUV = getWarpedUV(v_uv, isRightEye, MAX_PIXEL_DISPARITY);
    color = texture(ColorTex, warpedUV).rgb;
    
//    float eyeSign = isRightEye ? 1.0 : -1.0;
//    vec2 leftUV = uv + eyeSign*offset;
//    leftUV = clamp(leftUV, vec2(0.0), vec2(1.0));
//    color = texture(ColorTex, leftUV).rgb;   
       

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
//    const float NearPlane = 0.1f;   // 近裁剪面 0.1m    
    const float NearPlane = 0.25f;   // 近裁剪面 Unity 出入的 _ProjectionParams 参数    
    const float FarPlane = 6000.0f;  // 远裁剪面 300m
//    const float FocusPlane = 0.503f;  // 聚焦平面 (归一化深度值，对应约 50m)
    const float FocusPlane = 0.9375f;  
    
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
