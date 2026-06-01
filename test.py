#if CURRENT_LOG_LEVEL <= LOG_LEVEL_DEBUG
    #define DBG_LOG(format, ...)                                            \
        do {                                                                \
            os::log("%s,%d: %s" format, SHORT_FILE, __LINE__, LOG_DEBUG_STAMP, ##__VA_ARGS__); \
        } while (0)
#else
    #define DBG_LOG(format, ...)
#endif
