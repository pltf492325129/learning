#pragma once

// ======================== PNG 编码器（无外部依赖）========================
// 使用 DEFLATE store 块（无压缩），生成合法 PNG 文件
//
// 依赖: <cstdio>, <cstring>, <vector>, <cstdint>, <sys/stat.h>
// 可选: "common/logger.h" (有 LOGW 则输出日志，无则静默)

#include <cstdio>
#include <cstring>
#include <cstdint>
#include <vector>
#include <sys/stat.h>

// ---- 目录工具 ----

inline static bool ensureDirExists(const char* filePath) {
    char dirPath[256];
    strncpy(dirPath, filePath, sizeof(dirPath) - 1);
    dirPath[sizeof(dirPath) - 1] = 0;

    char* p = strrchr(dirPath, '/');
    if (p) {
        *p = '\0';
        struct stat st;
        if (stat(dirPath, &st) != 0) {
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

// ---- CRC32 ----

static uint32_t png_crc_table[256];
static bool png_crc_table_ready = false;

static void png_init_crc_table() {
    for (uint32_t n = 0; n < 256; n++) {
        uint32_t c = n;
        for (int k = 0; k < 8; k++) {
            c = (c & 1) ? (0xEDB88320u ^ (c >> 1)) : (c >> 1);
        }
        png_crc_table[n] = c;
    }
    png_crc_table_ready = true;
}

// ---- PNG chunk 写入 ----

static void png_write_chunk(FILE* fp, const char type[4],
                            const uint8_t* data, uint32_t len) {
    if (!png_crc_table_ready) png_init_crc_table();

    // Length (big-endian)
    uint8_t buf[4];
    buf[0] = (len >> 24) & 0xFF; buf[1] = (len >> 16) & 0xFF;
    buf[2] = (len >> 8) & 0xFF;  buf[3] = len & 0xFF;
    fwrite(buf, 1, 4, fp);

    // Type
    fwrite(type, 1, 4, fp);

    // Data
    if (data && len > 0) fwrite(data, 1, len, fp);

    // CRC32 over type + data (增量计算，无需额外分配)
    uint32_t crc = 0xFFFFFFFFu;
    for (int i = 0; i < 4; i++)
        crc = png_crc_table[(crc ^ (uint8_t)type[i]) & 0xFF] ^ (crc >> 8);
    for (uint32_t i = 0; data && i < len; i++)
        crc = png_crc_table[(crc ^ data[i]) & 0xFF] ^ (crc >> 8);
    crc ^= 0xFFFFFFFFu;

    buf[0] = (crc >> 24) & 0xFF; buf[1] = (crc >> 16) & 0xFF;
    buf[2] = (crc >> 8) & 0xFF;  buf[3] = crc & 0xFF;
    fwrite(buf, 1, 4, fp);
}

// ---- 主保存函数 ----

inline void savePNG(const char* filePath, int width, int height,
                    const uint8_t* pixels) {
    ensureDirExists(filePath);

    FILE* fp = fopen(filePath, "wb");
    if (!fp) {
#ifdef LOGW
        LOGW("Failed to open %s for writing", filePath);
#endif
        return;
    }

    // PNG 签名
    uint8_t sig[8] = {137, 80, 78, 71, 13, 10, 26, 10};
    fwrite(sig, 1, 8, fp);

    // IHDR chunk
    uint8_t ihdr[13] = {0};
    ihdr[0] = (width >> 24) & 0xFF;  ihdr[1] = (width >> 16) & 0xFF;
    ihdr[2] = (width >> 8) & 0xFF;   ihdr[3] = width & 0xFF;
    ihdr[4] = (height >> 24) & 0xFF; ihdr[5] = (height >> 16) & 0xFF;
    ihdr[6] = (height >> 8) & 0xFF;  ihdr[7] = height & 0xFF;
    ihdr[8] = 8;    // bit depth = 8
    ihdr[9] = 2;    // color type = RGB (no alpha)
    ihdr[10] = 0;   // compression = deflate
    ihdr[11] = 0;   // filter = adaptive
    ihdr[12] = 0;   // interlace = none
    png_write_chunk(fp, "IHDR", ihdr, 13);

    // 构建带 filter byte 的原始图像数据
    // PNG 规范: 每行前需要 1 字节 filter 类型 (0 = None)
    // glReadPixels 读取方向: 行0=底部, 左→右
    // PNG 期望方向: 行0=顶部, 左→右
    // 需要垂直翻转 + 水平翻转以匹配正常图像坐标
    size_t rowBytes = (size_t)width * 3;
    size_t rawSize = (rowBytes + 1) * (size_t)height;
    std::vector<uint8_t> rawData(rawSize);
    for (int y = 0; y < height; y++) {
        size_t rowStart = y * (rowBytes + 1);
        rawData[rowStart] = 0;  // filter byte = None
        // 垂直翻转: 从像素缓冲区底部行开始读
        const uint8_t* srcRow = pixels + (height - 1 - y) * rowBytes;
        // 水平翻转: 每行像素顺序反转
        for (int x = 0; x < width; x++) {
            rawData[rowStart + 1 + x * 3 + 0] = srcRow[(width - 1 - x) * 3 + 0];
            rawData[rowStart + 1 + x * 3 + 1] = srcRow[(width - 1 - x) * 3 + 1];
            rawData[rowStart + 1 + x * 3 + 2] = srcRow[(width - 1 - x) * 3 + 2];
        }
    }

    // DEFLATE store 编码 (zlib 格式, 无压缩)
    // 结构: CMF(1) + FLG(1) + [store blocks] + Adler32(4)
    std::vector<uint8_t> zlibData;
    zlibData.push_back(0x78);  // CMF: deflate, window=32K
    zlibData.push_back(0x01);  // FLG: no dict, level 0

    const size_t maxBlock = 65535;
    size_t offset = 0;
    while (offset < rawSize) {
        size_t blockLen = (rawSize - offset < maxBlock)
                          ? (rawSize - offset) : maxBlock;
        bool isFinal = (offset + blockLen >= rawSize);

        // BFINAL(1 bit) + BTYPE(2 bits=00 stored)
        zlibData.push_back(isFinal ? 0x01 : 0x00);

        // LEN + NLEN (little-endian, 16-bit each)
        uint16_t len16 = (uint16_t)blockLen;
        zlibData.push_back(len16 & 0xFF);
        zlibData.push_back((len16 >> 8) & 0xFF);
        zlibData.push_back(~len16 & 0xFF);
        zlibData.push_back((~len16 >> 8) & 0xFF);

        // 原始数据
        zlibData.insert(zlibData.end(),
                        rawData.begin() + offset,
                        rawData.begin() + offset + blockLen);
        offset += blockLen;
    }

    // Adler32 校验 (big-endian)
    {
        uint32_t a = 1, b = 0;
        for (size_t i = 0; i < rawSize; i++) {
            a = (a + rawData[i]) % 65521;
            b = (b + a) % 65521;
        }
        uint32_t adler = (b << 16) | a;
        zlibData.push_back((adler >> 24) & 0xFF);
        zlibData.push_back((adler >> 16) & 0xFF);
        zlibData.push_back((adler >> 8) & 0xFF);
        zlibData.push_back(adler & 0xFF);
    }

    // IDAT chunk
    png_write_chunk(fp, "IDAT", zlibData.data(), (uint32_t)zlibData.size());

    // IEND chunk
    png_write_chunk(fp, "IEND", nullptr, 0);

    fclose(fp);
#ifdef LOGW
    LOGW("Saved PNG: %s (%dx%d)", filePath, width, height);
#endif
}
