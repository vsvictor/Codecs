package com.codecs;

/**
 * Created by User on 9/22/2017.
 */

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class EncoderInternal
{
    private final static String TAG = EncoderInternal.class.getSimpleName();

    private MediaCodec mediaCodec;
    private String mime;
    private int width;
    private int height;
    private int timeoutUSec = 10000;
    private long frameIndex = 0;
    private byte[] spsPpsInfo = null;
    private int bitRate;
    private int frameRate;
    private int i_interval;

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public EncoderInternal()
    {
    }

    public boolean InitEncoder(String mime, int width, int height, int framerate, int bitrate, int i_frame_interval)
    {
        this.mime = mime;
        this.width = width;
        this.height = height;
        this.bitRate = bitrate;
        this.frameRate = framerate;
        this.i_interval = i_frame_interval;

        try
        {
            mediaCodec = MediaCodec.createEncoderByType(mime);
        }
        catch (IOException e)
        {
            return false;
        }

        MediaFormat mediaFormat = MediaFormat.createVideoFormat(this.mime, this.width, this.height);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, this.bitRate);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, this.frameRate);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, this.i_interval);

        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mediaCodec.start();
        return true;
    }

    public void CloseEncoder(){
        try
        {
            mediaCodec.stop();
            mediaCodec.release();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public byte[] EncodeFrame(byte[] input)
    {
        try {
            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
            int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
            if (inputBufferIndex >= 0)
            {
                long pts = computePresentationTime(frameIndex, frameRate);
                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                inputBuffer.clear();
                inputBuffer.put(input, 0, input.length);
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, pts, 0);
                frameIndex++;
            }

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, timeoutUSec);

            while (outputBufferIndex >= 0)
            {
                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                byte[] outData = new byte[bufferInfo.size];
                outputBuffer.get(outData);

                if (spsPpsInfo == null)
                {
                    ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData);
                    if (spsPpsBuffer.getInt() == 0x00000001)
                    {
                        spsPpsInfo = new byte[outData.length];
                        System.arraycopy(outData, 0, spsPpsInfo, 0, outData.length);
                    }
                    else
                    {
                        return null;
                    }
                }
                else
                {
                    outputStream.write(outData);
                }

                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, timeoutUSec);
            }
            byte[] ret = outputStream.toByteArray();
            if (ret.length > 5 && ret[4] == 0x65) //key frame need to add sps pps
            {
                outputStream.reset();
                outputStream.write(spsPpsInfo);
                outputStream.write(ret);
            }

        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        byte[] ret = outputStream.toByteArray();
        outputStream.reset();
        return ret;
    }

    private static MediaCodecInfo selectCodec(String mimeType)
    {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++)
        {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder())
                continue;

            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++)
            {
                if (types[j].equalsIgnoreCase(mimeType))
                    return codecInfo;
            }
        }
        return null;
    }

    private long computePresentationTime(long frameIndex, int framerate)
    {
        return 132 + frameIndex * 1000000 / framerate;
    }

    public String getMime(){ return mime;}
    public int getWidth(){return width;}
    public int getHeight(){return height;}
    public int getBitRate(){return bitRate;}
    public int getFrameRate(){return frameRate;}
    public int getIInterval(){return i_interval;}
    public MediaCodecInfo getCodecInfo(){
        return mediaCodec.getCodecInfo();
    }
}