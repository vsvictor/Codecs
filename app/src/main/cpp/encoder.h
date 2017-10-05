#ifndef CODECS_ENCODER_H
#define CODECS_ENCODER_H

#include "VideoResolution.h"
#include <memory>

namespace vos {
    namespace log {
        class Category;
    }

    namespace medialib {

        const int Kb = 1024;

        class H264EncoderFilter_Internal {

        public:
            H264EncoderFilter_Internal();
            void InitSettings();

        private:
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

        };

    }
}

#endif //CODECS_ENCODER_H
