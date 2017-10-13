#include "encoder.h"
#include <media/NdkMediaCodec.h>
#include <malloc.h>

#define NANOS_IN_SECOND 1000000000

using namespace vos::medialib;

static long currentTimeInNanos() {

    struct timespec res;
    clock_gettime(CLOCK_MONOTONIC, &res);
    return (res.tv_sec * NANOS_IN_SECOND) + res.tv_nsec;
}


H264EncoderFilter_Internal::H264EncoderFilter_Internal():m_encSettings(new h264setting_internal)
{
    started = false;
    InitSettings();
}

void H264EncoderFilter_Internal::InitSettings() {
    memset(m_encSettings.get(), 0, sizeof(h264setting_internal));
    m_encSettings->resolution.width = 320;
    m_encSettings->resolution.height = 240;
    m_encSettings->bitrate = 1024*Kb;
    m_encSettings->framerate = 30;
    m_encSettings->iframe_interval = 5;
}
void H264EncoderFilter_Internal::InitEncoder() {
/*
    AMediaFormat* format = AMediaFormat_new();
    AMediaFormat_setInt32(format,KEY_WIDTH, m_encSettings->resolution.width);
    AMediaFormat_setInt32(format,KEY_HEIGHT, m_encSettings->resolution.height);
    AMediaFormat_setInt32(format,KEY_PROFILE, AVC_PROFILE_BASE_LINE);
    AMediaFormat_setInt32(format,KEY_LEVEL, AVC_LEVEL_13);
    AMediaFormat_setInt32(format,KEY_BIT_RATE, m_encSettings->bitrate);
    AMediaFormat_setInt32(format,KEY_FRAME_RATE, m_encSettings->framerate);
    AMediaFormat_setInt32(format,KEY_COLOR_FORMAT, COLOR_FORMAT_SURFACE);
    AMediaFormat_setInt32(format,KEY_I_FRAME_INTERVAL, m_encSettings->iframe_interval);

    codec = AMediaCodec_createEncoderByType(VIDEO_AVC_MIME);

    media_status_t status = AMediaCodec_configure(codec, format, NULL, NULL, CONFIGURE_FLAG_ENCODE);
*/
}

void H264EncoderFilter_Internal::StartEncoder() {
    //AMediaCodec_start(codec);
}

void H264EncoderFilter_Internal::StopEncoder() {
    //AMediaCodec_stop(codec);
}

void H264EncoderFilter_Internal::EncodeFrame(uint8_t *input, size_t ilen, uint8_t *output, size_t olen, bool isIFrame) {
/*
    int64_t startWhenUsec = currentTimeInNanos();
    ssize_t index = AMediaCodec_dequeueInputBuffer(codec, -1);
    size_t out_size;
    if(index >= 0 ){
        uint8_t *buffer = AMediaCodec_getInputBuffer(codec, index, &out_size);
        if(out_size > 0){
            memcpy(buffer, input, out_size);
            AMediaCodec_queueInputBuffer(codec, index, 0, out_size, startWhenUsec, AMEDIACODEC_BUFFER_FLAG_END_OF_STREAM);
        }
    }
    AMediaCodecBufferInfo *info = (AMediaCodecBufferInfo *) malloc(sizeof(AMediaCodecBufferInfo));
    ssize_t outIndex;
    do{
        outIndex = AMediaCodec_dequeueOutputBuffer(codec, info, 0);
        size_t out_size;
        if (outIndex >= 0) {
            uint8_t *outputBuffer = AMediaCodec_getOutputBuffer(codec, outIndex, &out_size);
            if(output == NULL) output = (uint8_t*)malloc(out_size);
            AMediaCodec_releaseOutputBuffer(codec, outIndex, false);
            memcpy(output, outputBuffer, out_size);
        } else if (outIndex == AMEDIACODEC_INFO_OUTPUT_FORMAT_CHANGED) {
            AMediaFormat *outFormat = AMediaCodec_getOutputFormat(codec);
        }
    }while(outIndex >= 0);
*/
}

long H264EncoderFilter_Internal::computePresentationTime(long frameIndex, int framerate){
    return 132 + frameIndex * 1000000 / framerate;
}

