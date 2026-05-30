#include "overlap.hpp"
#include <unordered_map>

// Draw call tracking for depth buffer selection heuristic
struct FBOStats {
    uint32_t draw_calls = 0;
    uint32_t vertices = 0;
};
static std::unordered_map<GLuint, FBOStats> g_fbo_draw_stats;

void GLES_CALLCONVENTION patrace_glBindFramebuffer(GLenum target, GLuint framebuffer){
    unsigned char tid = GetThreadId();
    UpdateTimesEGLConfigUsed(tid);
    // traceFunctionBody_pre
    // traceFunctionBody
    if (gTraceThread[tid].mCallDepth)
    {
        //DBG_LOG("WARNING: Recursive glCall. Depth: %d\n", gTraceThread[tid].mCallDepth);
        // invokeFunction
        // DBG_LOG("Invoking: _glBindFramebuffer(%x, %x)\n", target, framebuffer);

        ++gTraceThread[tid].mCallDepth;
        _glBindFramebuffer(target, framebuffer);
        --gTraceThread[tid].mCallDepth;

        return;
    }
    // invokeFunction
    DBG_LOG("Invoking: _glBindFramebuffer(%x, %x)\n", target, framebuffer);

    ++gTraceThread[tid].mCallDepth;

    DBG_LOG("glBindFramebuffer frameID:%d framebuffer:%d target：%x", g_frame_id, framebuffer, target);
    if (target == GL_FRAMEBUFFER || target == GL_DRAW_FRAMEBUFFER) {
            g_current_binding = framebuffer;
            // 初始化 FBO 记录
            if (g_fbo_registry.find(framebuffer) == g_fbo_registry.end()) {
                g_fbo_registry[framebuffer].id = framebuffer;
            }
    }

    _glBindFramebuffer(target, framebuffer);

    // Check if this FBO has a depth attachment for depth overlay
    if (framebuffer != 0 && (target == GL_FRAMEBUFFER || target == GL_DRAW_FRAMEBUFFER)) {
        GLint depthType = GL_NONE;
        _glGetFramebufferAttachmentParameteriv(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT,
            GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE, &depthType);
        if (depthType == GL_TEXTURE || depthType == GL_RENDERBUFFER) {
            gLastDepthBlitTargetFBO = framebuffer;
            DBG_LOG("Depth overlay: FBO=%d has depth attachment type=0x%x", framebuffer, depthType);
        }
    }

    --gTraceThread[tid].mCallDepth;

    // save parameters
    gTraceOut->callMutex.lock();
    char* dest = gTraceOut->writebuf;
    BCall *pCall = (BCall*)dest;
    pCall->funcId = glBindFramebuffer_id;
#ifdef DEBUG
    if (pCall->funcId > common::ApiInfo::MaxSigId)
    {
        DBG_LOG("Fatal error: Trying to write bad func ID for glBindFramebuffer!\n");
        abort();
    }
#endif
    pCall->tid = tid; pCall->reserved = 0;
    dest += sizeof(*pCall);

    dest = WriteFixed<int>(dest, target); // enum
    dest = WriteFixed<unsigned int>(dest, framebuffer); // literal
    pCall->errNo = GetCallErrorNo("glBindFramebuffer", tid);
    SendBufForNormalApi(pCall, dest, &gTraceOut->curparambuf, sizeof(*pCall));
    gTraceOut->callNo++;
    gTraceOut->callMutex.unlock();
    // traceFunctionBody_after
}



static bool isDrawMultipleImages = false;
EGLBoolean GLES_CALLCONVENTION patrace_eglSwapBuffers(EGLDisplay dpy, EGLSurface surface) {
    unsigned char tid = GetThreadId();
    UpdateTimesEGLConfigUsed(tid);
    EGLBoolean _result = EGL_TRUE;
    // traceFunctionBody_pre
    auto startAll = std::chrono::high_resolution_clock::now();
    FrameCounterClient::Instance()->SetSwapBuffersStartTimestamp(g_frame_id, startAll);
    pre_eglSwapBuffers();
    // traceFunctionBody
    gTraceOut->callMutex.lock();
    if (gTraceThread[tid].mCallDepth) {
        DBG_LOG("return for mCallDepth  frameID:%d\n", g_frame_id);
        return false;
    }

    static EGLSurface preEglSurface = nullptr;
    if (surface != preEglSurface) {
        // when multiple surface data are sent to server, server would not send the frame back
        // disable send control for a while, it would be enabled by DataTransManager later
        DataTransManager::Instance()->DisableSendControlTemporary(g_frame_id);
        preEglSurface = surface;
    }

    char *dest = gTraceOut->writebuf;
    BCall *pCall = (BCall *)dest;
    pCall->funcId = eglSwapBuffers_id;
#ifdef DEBUG
    if (pCall->funcId > common::ApiInfo::MaxSigId) {
        DBG_LOG("Fatal error: Trying to write bad func ID for eglSwapBuffers!\n");
        abort();
    }
#endif
    pCall->tid = tid;
    pCall->reserved = 0;
    dest += sizeof(*pCall);

    dest = WriteFixed<int>(dest, (intptr_t)dpy);      // int pointer
    dest = WriteFixed<int>(dest, (intptr_t)surface);  // int pointer
    dest = WriteFixed<int>(dest, 0);  // enum  写入swarpbuffer的返回值, 直接写0, 云侧不关注, 原来写的是patrace_eglSwapBuffers中的_result
    SendBufForNormalApi(pCall, dest, &gTraceOut->curparambuf, sizeof(*pCall)); 
    EGLint width;
    EGLint height;
    eglQuerySurface(dpy,surface,EGL_WIDTH,&width);
    eglQuerySurface(dpy,surface,EGL_HEIGHT,&height);


    /*OPTIMIZE external Buffer rendering TODO*/
    gTraceOut->callNo++;
    
    checkTextureTypeDepth();

    // Select best depth FBO based on draw stats (heuristic from ReShade approach)
    {
        GLuint bestDepthFBO = 0;
        uint32_t bestScore = 0;
        for (const auto& entry : g_fbo_draw_stats) {
            uint32_t score = entry.second.draw_calls + entry.second.vertices / 100;
            if (score > bestScore) {
                bestScore = score;
                bestDepthFBO = entry.first;
            }
        }
        if (bestDepthFBO != 0) {
            gLastDepthBlitTargetFBO = bestDepthFBO;
            DBG_LOG("Depth overlay: selected best depth FBO=%d (draws=%u, verts=%u)",
                    bestDepthFBO, g_fbo_draw_stats[bestDepthFBO].draw_calls,
                    g_fbo_draw_stats[bestDepthFBO].vertices);
        }
        g_fbo_draw_stats.clear();
    }

    UpdateDepthOverlay(width, height);

    // for debugging
    if (g_frame_id < client_draw_frame) {
        _result = _eglSwapBuffers(dpy, surface);
    }
    /*OPTIMIZE external Buffer rendering TODO*/
    // EGLint surface_width, surface_height;
    // _eglQuerySurface(dpy, surface, EGL_WIDTH, &surface_width);
    // _eglQuerySurface(dpy, surface, EGL_HEIGHT, &surface_height);
    // DBG_LOG("tlq: patrace_eglSwapBuffers dpy 0x%p, surface 0x%p, w %d, h %d \n", dpy, surface, surface_width, surface_height);
    bool hasDrawImage = false;
    // AImage* image = nullptr;
    int drawCnt = 0;
    Systrace::Instance()->SystraceBegin(SYSTRACE_CREATE_COMPRESS_JOB, g_frame_id);
    gTraceOut->CreateCompressJob(g_frame_id);
    Systrace::Instance()->SystraceEnd();

    // Backup context before drawing texture from server
    GLint curPrg, curActiveTexture, curTextureId;
    _glGetIntegerv(GL_CURRENT_PROGRAM, &curPrg);
    _glGetIntegerv(GL_ACTIVE_TEXTURE, &curActiveTexture);
    _glGetIntegerv(GL_TEXTURE_BINDING_2D, &curTextureId);

    // 在调用下面函数时候会产生新的API, 所以需要提前先创建压缩任务。
    // DrawAndSwapBuffersOH(dpy, surface);
    // _result = _eglSwapBuffers(dpy, surface);


    // Recovery context
    _glUseProgram(curPrg);
    _glActiveTexture(curActiveTexture);
    _glBindTexture(GL_TEXTURE_2D, curTextureId);

    isDrawMultipleImages = drawCnt > 1;

    gTraceOut->callNo++;
    gTraceOut->ClearApiData();
    gDepthCapturedThisFrame = false;  // Reset depth capture flag for next frame
    g_frame_id++;
    INFO_LOG("Frame start frameID:%d", g_frame_id);
    auto time = std::chrono::high_resolution_clock::now();
    FrameCounterClient::Instance()->SetStartTimestamp(g_frame_id, time);
    gTraceOut->callMutex.unlock();

    // traceFunctionBody_after
    after_eglSwapBuffers();
    return _result;
}



void GLES_CALLCONVENTION patrace_glDrawArrays(GLenum mode, GLint first, GLsizei count){
    unsigned char tid = GetThreadId();
    UpdateTimesEGLConfigUsed(tid);
    // traceFunctionBody_pre
    if (count && _need_user_arrays()) {
        GLuint _count = _glDrawArrays_count(first, count);
        _trace_user_arrays(_count, 0);
    }
    if (unlikely(stateLoggingEnabled)) {
        gTraceOut->getStateLogger().logFunction(tid, "glDrawArrays", gTraceOut->callNo, 0);
        gTraceOut->getStateLogger().logState(tid, first, count, 0);
    }
    // traceFunctionBody
    if (gTraceThread[tid].mCallDepth)
    {
        //DBG_LOG("WARNING: Recursive glCall. Depth: %d\n", gTraceThread[tid].mCallDepth);
        // invokeFunction
        // DBG_LOG("Invoking: _glDrawArrays(%x, %x, %x)\n", mode, first, count);

        ++gTraceThread[tid].mCallDepth;

        if (g_frame_id < client_draw_frame) {
            _glDrawArrays(mode, first, count);
        }

        --gTraceThread[tid].mCallDepth;

        return;
    }
    // invokeFunction
    DUMP_PER_API("Invoking: _glDrawArrays(%x, %x, %x)\n", mode, first, count);

    ++gTraceThread[tid].mCallDepth;
    if (g_frame_id < client_draw_frame) {
        _glDrawArrays(mode, first, count);
    }

    // Track draw calls for depth buffer selection heuristic
    if (g_current_binding != 0) {
        g_fbo_draw_stats[g_current_binding].draw_calls++;
        g_fbo_draw_stats[g_current_binding].vertices += count;
    }

    --gTraceThread[tid].mCallDepth;

    // save parameters
    gTraceOut->callMutex.lock();
    char* dest = gTraceOut->writebuf;
    BCall *pCall = (BCall*)dest;
    pCall->funcId = glDrawArrays_id;
#ifdef DEBUG
    if (pCall->funcId > common::ApiInfo::MaxSigId)
    {
        DBG_LOG("Fatal error: Trying to write bad func ID for glDrawArrays!\n");
        abort();
    }
#endif
    pCall->tid = tid; pCall->reserved = 0;
    dest += sizeof(*pCall);

    dest = WriteFixed<int>(dest, mode); // enum
    dest = WriteFixed<int>(dest, first); // literal
    dest = WriteFixed<int>(dest, count); // literal
    pCall->errNo = GetCallErrorNo("glDrawArrays", tid);
    SendBufForNormalApi(pCall, dest, &gTraceOut->curparambuf, sizeof(*pCall));
    gTraceOut->callNo++;
    gTraceOut->callMutex.unlock();
    // traceFunctionBody_after
    after_glDraw();
}

void GLES_CALLCONVENTION patrace_glDrawElements(GLenum mode, GLsizei count, GLenum type, const GLvoid * indices){
    unsigned char tid = GetThreadId();
    UpdateTimesEGLConfigUsed(tid);
    // traceFunctionBody_pre
    if (count && _need_user_arrays()) {
        GLuint _count = _glDrawElements_count(count, type, indices);
        _trace_user_arrays(_count, 0);
    }
    if (unlikely(stateLoggingEnabled)) {
        gTraceOut->getStateLogger().logFunction(tid, "glDrawElements", gTraceOut->callNo, 0);
        gTraceOut->getStateLogger().logState(tid, count, type, indices, 0);
    }
    GLint _element_array_buffer = 0;
    _glGetIntegerv(GL_ELEMENT_ARRAY_BUFFER_BINDING, &_element_array_buffer);
    GLuint clientSideBufferObjName = 0;
#if ENABLE_CLIENT_SIDE_BUFFER
    if (!_element_array_buffer) {
        clientSideBufferObjName = _glClientSideBufferData(indices, count*_gl_type_size(type));
    }
#endif
    // traceFunctionBody
    if (gTraceThread[tid].mCallDepth)
    {
        //DBG_LOG("WARNING: Recursive glCall. Depth: %d\n", gTraceThread[tid].mCallDepth);
        // invokeFunction
        // DBG_LOG("Invoking: _glDrawElements(%x, %x, %x, %x)\n", mode, count, type, indices);

        ++gTraceThread[tid].mCallDepth;
        if (g_frame_id < client_draw_frame) {
            _glDrawElements(mode, count, type, indices);
        }
        --gTraceThread[tid].mCallDepth;

        return;
    }
    // invokeFunction
    DUMP_PER_API("Invoking: _glDrawElements(%x, %x, %x, %x)\n", mode, count, type, indices);

    ++gTraceThread[tid].mCallDepth;
    if (g_frame_id < client_draw_frame) {
        _glDrawElements(mode, count, type, indices);
    }

    // Track draw calls for depth buffer selection heuristic
    if (g_current_binding != 0) {
        g_fbo_draw_stats[g_current_binding].draw_calls++;
        g_fbo_draw_stats[g_current_binding].vertices += count;
    }

    --gTraceThread[tid].mCallDepth;

    // save parameters
    gTraceOut->callMutex.lock();
    char* dest = gTraceOut->writebuf;
    BCall_vlen *pCall = (BCall_vlen*)dest;
    pCall->funcId = glDrawElements_id;
#ifdef DEBUG
    if (pCall->funcId > common::ApiInfo::MaxSigId)
    {
        DBG_LOG("Fatal error: Trying to write bad func ID for glDrawElements!\n");
        abort();
    }
#endif
    pCall->tid = tid; pCall->reserved = 0;
    dest += sizeof(*pCall);

    dest = WriteFixed<int>(dest, mode); // enum
    dest = WriteFixed<int>(dest, count); // literal
    dest = WriteFixed<int>(dest, type); // enum
    if (!_element_array_buffer) {
#if ENABLE_CLIENT_SIDE_BUFFER
        dest = WriteFixed<unsigned int>(dest, ClientSideBufferObjectReferenceType); // IS *MEMORY REFERENCE*
        dest = WriteFixed<unsigned int>(dest, clientSideBufferObjName);
        dest = WriteFixed<unsigned int>(dest, 0);
#else
        dest = Write1DArray<char>(dest, (unsigned int)(count*_gl_type_size(type)), (const char*)indices);
#endif
    } else {
        dest = WriteFixed<unsigned int>(dest, BufferObjectReferenceType); // ISN'T *BLOB*
        dest = WriteFixed<unsigned int>(dest, (uintptr_t)indices); // opaque -> ptr 
    }
    pCall->errNo = GetCallErrorNo("glDrawElements", tid);
    pCall->toNext = dest-gTraceOut->writebuf;
#ifdef DEBUG
    if (pCall->toNext == 0)
    {
        DBG_LOG("Zero-length variable call detected for glDrawElements in call %d\n", (int)gTraceOut->callNo);
        abort();
    }
#endif
    SendBufForNormalApi(pCall, dest, &gTraceOut->curparambuf, sizeof(*pCall));
    gTraceOut->callNo++;
    gTraceOut->callMutex.unlock();
    // traceFunctionBody_after
    after_glDraw();
}
