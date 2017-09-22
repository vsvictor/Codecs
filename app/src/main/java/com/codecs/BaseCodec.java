package com.codecs;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;

/**
 * Created by User on 9/22/2017.
 */

public abstract class BaseCodec {
    MediaCodec codec;
    public BaseCodec(){
    }

    public abstract void encodeFrame(byte[] data);
    public abstract void encodeFrame(byte[] data, EncodedFrameListener encodeListener);
    public abstract void encodeFrame(byte[] data, EncodedFrameListener encodeListener, ParameterSetsListener parameterListener);
    public void close(){
        codec.stop();
        codec.release();
    }

    public interface EncodedFrameListener {
        public void frameReceived(byte[] data, int pos, int length);
    }
    public interface ParameterSetsListener{
        public void avcParametersSetsEstablished(byte[] sps, byte[] pps);
    }
}
