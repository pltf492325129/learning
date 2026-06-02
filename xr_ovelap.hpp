#ifndef _DEPTH_OVERLAY_HPP_
#define _DEPTH_OVERLAY_HPP_

#include <mutex>
#include <atomic>
#include <chrono>
#include <GLES3/gl3.h>

// DBG_LOG is defined by the build system (see build config).
// Format: DBG_LOG(format, ...) → os::log("%s,%d: %s" format, SHORT_FILE, __LINE__, ...)

#define DEPTH_OVERLAY_TRIGGER_FILE "/data/storage/el2/base/haps/inputs/results/depth_overlay"
#define DEPTH_OVERLAY_STABLE_FRAME_THRESHOLD 30

// Enabled state
extern bool gDepthOverlayEnabled;
extern std::atomic<bool> gOverlayTogglePending;
extern std::mutex gOverlayMutex;

// Frame ID — set by hook.cpp or tcp_egltrace_auto.cpp before each overlay render
extern int gOverlayFrameId;

// Overlay resources
extern GLuint gOverlayFBO;
extern GLuint gOverlayColorTex;
extern GLuint gDepthBlitFBO;
extern GLuint gDepthBlitColorTex;
extern GLuint gDepthToColorProgram;
extern GLboolean gOverlayInitDone;
extern int gOverlayWidth;
extern int gOverlayHeight;

// Depth capture globals (set by glBlitFramebuffer hook during frame rendering)
extern GLuint gCapturedDepthFBO;
extern int gCapturedDepthWidth;
extern int gCapturedDepthHeight;
extern bool gDepthCapturedThisFrame;

// FBO tracking (set by glBindFramebuffer hook)
extern GLuint gLastDepthBlitTargetFBO;

void InitDepthOverlay(int width, int height);
void UpdateDepthOverlay(int width, int height);
void RenderDepthOverlay(int width, int height);
void CleanupDepthOverlay();

#endif
