package com.codecs;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class EncoderInternal{
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
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private boolean isStarted = false;

    public EncoderInternal(){
        this.isStarted = false;
    }
    public boolean InitEncoder(String mime, int width, int height, int framerate, int bitrate, int i_frame_interval){
        this.mime = mime;
        this.width = width;
        this.height = height;
        this.bitRate = bitrate;
        this.frameRate = framerate;
        this.i_interval = i_frame_interval;
        Log.i(TAG, "Mime: "+this.mime+", width: "+this.width+", height:"+this.height+", framerate:"+this.frameRate+", bitrate:"+this.bitRate+", I-interval:"+this.i_interval);
        try {
            mediaCodec = MediaCodec.createEncoderByType(mime);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //mediaCodec = MediaCodec.createByCodecName("OMX.google.h264.encoder");
        if(null == mediaCodec){
            Log.d(TAG, "Unable codec create by mime="+mime);
            return false;
        }

        MediaFormat mediaFormat = MediaFormat.createVideoFormat(this.mime, this.width, this.height);
        mediaFormat.setInteger("profile", MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline);
        mediaFormat.setInteger("level", MediaCodecInfo.CodecProfileLevel.AVCLevel13);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, this.bitRate);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, this.frameRate);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, this.i_interval);

        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        return true;
    }
    public void Start(){
        mediaCodec.start();
        this.isStarted = true;
    }
    public void CloseEncoder(){
        if(mediaCodec != null){
            try {
                if(this.isStarted) {
                    this.isStarted = false;
                    mediaCodec.stop();
                }
                mediaCodec.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public byte[] EncodeFrame(byte[] input)
    {
        Log.i("ENCODE", "Begin");
        try {
            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
            Log.i("ENCODE", "Buffers inited");
            int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
            if (inputBufferIndex >= 0) {
                int beg = 0;
                while (beg<input.length) {
                    long pts = computePresentationTime(frameIndex, frameRate);
                    ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                    inputBuffer.clear();
                    inputBuffer.put(input, beg, inputBuffer.limit());
                    mediaCodec.queueInputBuffer(inputBufferIndex, 0, inputBuffer.limit(), pts, 0);
                    frameIndex++;
                    inputBufferIndex++;
                    beg += inputBuffer.limit();
                }
            }
            Log.i("ENCODE", "Input start data to buffers");
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, timeoutUSec);
            Log.i("ENCODE", "Output buffer index:"+outputBufferIndex);
            while (outputBufferIndex >= 0) {
                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                byte[] outData = new byte[bufferInfo.size];
                outputBuffer.get(outData);
                Log.i("ENCODE", "data to Output buffer");
                if (spsPpsInfo == null){
                    ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData);
                    if (spsPpsBuffer.getInt() == 0x00000001){
                        spsPpsInfo = new byte[outData.length];
                        System.arraycopy(outData, 0, spsPpsInfo, 0, outData.length);
                    }
                    else{
                        return null;
                    }
                }
                else {
                    outputStream.write(outData);
                }

                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, timeoutUSec);
            }
            byte[] ret = outputStream.toByteArray();
            if (ret.length > 5 && ret[4] == 0x65) {//key frame need to add sps pps
                outputStream.reset();
                outputStream.write(spsPpsInfo);
                outputStream.write(ret);
            }
            Log.i("ENCODE", "Output data to stream");
        } catch (Throwable t) {
            t.printStackTrace();
        }
        byte[] ret = outputStream.toByteArray();
        Log.i("ENCODE", "Output data to result array");
        outputStream.reset();
        Log.i("ENCODE", "Output stream reset");
        return ret;
    }
    public void SetBitRate(int bitRate){
        //Bundle bitrate = new Bundle();
        //bitrate.putInt(MediaCodec.PARAMETER_KEY_VIDEO_BITRATE, bitRate);
        //mediaCodec.setParameters(bitrate);
    }
    private long computePresentationTime(long frameIndex, int framerate){
        return 132 + frameIndex * 1000000 / framerate;
    }
    public String getMime(){ return mime;}
    public int getWidth(){return width;}
    public int getHeight(){return height;}
    public int getBitRate(){return bitRate;}
    public int getFrameRate(){return frameRate;}
    public int getIInterval(){return i_interval;}
    public MediaCodecInfo getCodecInfo(){return mediaCodec.getCodecInfo();}
}
