#include "encoder.h"
#include <media/NdkMediaCodec.h>
using namespace vos::medialib;

H264EncoderFilter_Internal::H264EncoderFilter_Internal():m_encSettings(new h264setting_internal)
{
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

}

