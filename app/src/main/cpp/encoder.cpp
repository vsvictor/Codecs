#include "encoder.h"
#include <media/NdkMediaCodec.h>
using namespace vos::medialib;

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

    AMediaCodec_configure(codec, format, NULL, NULL, CONFIGURE_FLAG_ENCODE);
}

void H264EncoderFilter_Internal::StartEncoder() {
    AMediaCodec_start(codec);
}

void H264EncoderFilter_Internal::StopEncoder() {
    AMediaCodec_stop(codec);
}
