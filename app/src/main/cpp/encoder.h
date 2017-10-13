#ifndef CODECS_ENCODER_H
#define CODECS_ENCODER_H

#include "VideoResolution.h"
#include <memory>
#include <media/NdkMediaCodec.h>
namespace vos {
    namespace log {
        class Category;
    }

    namespace medialib {


        class H264EncoderFilter_Internal {
        public:
            const int Kb = 1024;
            const char* VIDEO_AVC_MIME = "video/avc";

            const char* KEY_WIDTH = "width";
            const char* KEY_HEIGHT = "height";
            const char* KEY_PROFILE = "profile";
            const int AVC_PROFILE_BASE_LINE = 1;
            const char* KEY_LEVEL = "level";
            const int AVC_LEVEL_13 = 16;
            const char* KEY_BIT_RATE = "bitrate";
            const char* KEY_FRAME_RATE = "frame-rate";
            const char* KEY_COLOR_FORMAT = "color-format";
            const int COLOR_FORMAT_SURFACE= 2130708361;
            const char* KEY_I_FRAME_INTERVAL = "i-frame-interval";

            const int CONFIGURE_FLAG_ENCODE = 1;

        public:
            H264EncoderFilter_Internal();
            void InitSettings();
            void InitEncoder();
            void SetBitRate(int bitrate);
            void StartEncoder();
            void StopEncoder();
            void CloseEncoder();
            void OnNewFrameRate();
            void EncodeFrame(uint8_t *input, size_t ilen, uint8_t *output, size_t olen, bool isIFrame);
            //vos::mediadescription::VideoResolution GetMaxSupportedResolution();
            bool isStarted(){ return started;}
        private:
            long computePresentationTime(long frameIndex, int framerate);
            static long currentTimeInNanos();
            typedef struct
            {
                unsigned payloadSize;
            } RTPSettings;
            typedef struct
            {
                vos::mediadescription::VideoResolution resolution;
                int bitrate;
                int framerate;
                int iframe_interval;
                RTPSettings rtpSettings;
            } h264setting_internal;

            std::unique_ptr<h264setting_internal> m_encSettings;
            //AMediaCodec* codec;
            int frameIndex = 0;
            bool started = false;

        };

    }
}

#endif //CODECS_ENCODER_H
