package com.codecs;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CODEC";
    private RecyclerView listCodecs;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MediaCodecInfo[] info = getListCodec();
        Log.i(TAG,"Codec count: "+info.length+", first: "+MediaCodecList.getCodecInfoAt(0).getName());

    }
    public native MediaCodecInfo[] getListCodec();
}
