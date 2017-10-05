package com.codecs;

import android.media.MediaCodecInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CODEC";
    private RecyclerView listCodecs;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("encoder");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MediaCodecInfo[] info = getListCodec();
        for(int i = 0; i<info.length;i++) {
            Log.i(TAG, "Codec number: " + i + ", name: " + info[i].getName() +" is Encoder? "+ info[i].isEncoder()+"    "+isEncoder(info[i]));
        }
        EncoderInternal enc = new EncoderInternal();
        enc.InitEncoder("video/avc",320,200,30,100000, 5);
        //InitParms();
    }
    public native MediaCodecInfo[] getListCodec();
    public native boolean isEncoder(MediaCodecInfo info);
    //public native void InitParms();
}
