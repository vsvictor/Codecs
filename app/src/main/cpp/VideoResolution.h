/*****************************************************************************
 $Id: VideoResolution.h 82967 2017-07-14 11:56:20Z vdjurlyak $
 Copyright 2007 Avistar Communications Corporation. All rights reserved.
*****************************************************************************/

#ifndef __VOS_MEDIADESCRIPTION_VIDEORESOLUTION_H
#define __VOS_MEDIADESCRIPTION_VIDEORESOLUTION_H

#include <functional>
#include <iosfwd>

namespace vos
{
    namespace mediadescription
    {
        /** Yet another struct that contains a resolution -- this should replace some of the
            other ones out there.  */
        struct VideoResolution
        {
            unsigned width;
            unsigned height;

            VideoResolution()
                : width(0), height(0)
            {
            }

            VideoResolution(unsigned w, unsigned h)
                : width(w), height(h)
            {
            }

            bool operator ==(const VideoResolution &rhs) const
            {
                return width == rhs.width && height == rhs.height;
            }

            bool operator !=(const VideoResolution &rhs) const
            {
                return !(*this == rhs);
            }

            bool operator < (const VideoResolution &other) const
            {
                if (width == other.width)
                    return height < other.height;
                else
                    return width < other.width;
            }

            bool operator <= (const VideoResolution &other) const
            {
                return *this == other || *this < other;
            }

            bool operator > (const VideoResolution &other) const
            {
                return !(*this <= other);
            }

            bool operator >= (const VideoResolution &other) const
            {
                return !(*this < other);
            }

            unsigned long square() const
            {
                return width * height;
            }

            double Aspect() const
            {
                return height ? (double(width) / height) : 0;
            }

            friend std::ostream &operator <<(std::ostream &o, const VideoResolution &res);
        };

        /** Does a greater-than comparison on width.  If the left-hand side and the
            right-hand side have equal widths, it does a greater-than comparison on height. */
        struct VideoResolutionGreaterThan
            : std::binary_function<VideoResolution, VideoResolution, bool>
        {
            bool operator()(const VideoResolution &lhs, const VideoResolution &rhs) const;
        };


        // @name A few common resolutions.
        //@{
            extern const VideoResolution EMPTY_RESOLUTION;     ///< 0x0
            extern const VideoResolution CIF16_RESOLUTION;     ///< 1408x1152
            extern const VideoResolution HD1080_RESOLUTION;    ///< 1920x1080
            extern const VideoResolution HD720_RESOLUTION;     ///< 1280x720
            extern const VideoResolution HD360_RESOLUTION;     ///< 640x360
            extern const VideoResolution HD180_RESOLUTION;     ///< 640x360
            extern const VideoResolution CIF4_RESOLUTION;      ///< 704x576
            extern const VideoResolution VGA_RESOLUTION;       ///< 640x480
            extern const VideoResolution CIF_RESOLUTION;       ///< 352x288
            extern const VideoResolution PAL_SIF_RESOLUTION;   ///< 352x288
            extern const VideoResolution NTSC_SIF_RESOLUTION;  ///< 352x240
            extern const VideoResolution QVGA_RESOLUTION;      ///< 320x240
            extern const VideoResolution QCIF_RESOLUTION;      ///< 176x144
            extern const VideoResolution QQVGA_RESOLUTION;     ///< 160x120
            extern const VideoResolution SQCIF_RESOLUTION;     ///< 128x96
        //@}

        extern const uint32_t MAX_FPS;
    }
}
#endif // ifndef __VOS_MEDIADESCRIPTION_VIDEORESOLUTION_H
