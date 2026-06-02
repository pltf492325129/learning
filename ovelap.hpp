#ifndef _DEPTH_OVERLAY_HPP_
#define _DEPTH_OVERLAY_HPP_

#include <mutex>
#include <atomic>
#include <chrono>
#include <GLES3/gl3.h>
#include <GLES3/gl3ext.h>

#define DEPTH_OVERLAY_TRIGGER_FILE "/data/storage/el2/base/haps/inputs/results/depth_overlay"
#define DEPTH_OVERLAY_STABLE_FRAME_THRESHOLD 30

// Debug logging macro — override in your build if needed
#ifndef DBG_LOG
#include <android/log.h>
#define DBG_LOG(fmt, ...) \
    __android_log_print(ANDROID_LOG_DEBUG, "DepthOverlay", fmt, ##__VA_ARGS__)
#endif

extern bool gDepthOverlayEnabled;
extern std::atomic<bool> gOverlayTogglePending;
extern std::mutex gOverlayMutex;
extern int gOverlayFrameId;

// Depth capture globals (set by glBlitFramebuffer hook during frame rendering)
extern GLuint gCapturedDepthFBO;
extern int gCapturedDepthWidth;
extern int gCapturedDepthHeight;
extern bool gDepthCapturedThisFrame;

// FBO tracking (set by glBindFramebuffer hook)
extern GLuint gLastDepthBlitTargetFBO;

extern GLuint gOverlayFBO;
extern GLuint gOverlayColorTex;
extern GLuint gDepthBlitFBO;
extern GLuint gDepthBlitColorTex;
extern GLuint gDepthToColorProgram;
extern GLboolean gOverlayInitDone;
extern int gOverlayWidth;
extern int gOverlayHeight;

void InitDepthOverlay(int width, int height);
void UpdateDepthOverlay(int width, int height);
void RenderDepthOverlay(int width, int height);
void CleanupDepthOverlay();

#endif
