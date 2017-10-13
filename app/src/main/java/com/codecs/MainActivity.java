package com.codecs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaCodecInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CODEC";
    private RecyclerView listCodecs;
    private ImageView ivBitmap;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("encoder");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MediaCodecInfo[] infs = getListCodec();
        for(int i = 0; i<infs.length;i++){
            Log.i(TAG, "Codec: "+infs[i].getName());
        }


        ivBitmap = (ImageView) findViewById(R.id.ivBitmap);
        EncoderInternal enc = new EncoderInternal();
        boolean res = enc.InitEncoder("video/avc",320,240,30,100000, 5);
        if(res) Log.i(TAG, "Initialized");
        else Log.i(TAG, "Init fail!!!");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile("/sdcard/DCIM/Camera/pict.jpg", options);
        ivBitmap.setImageBitmap(bitmap);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        enc.Start();
        byte[] pict = enc.EncodeFrame(byteArray);
        Log.i(TAG, "Encoded? result size:" + pict.length);
    }
    public native MediaCodecInfo[] getListCodec();
    //public native boolean isEncoder(MediaCodecInfo info);
    //public native void InitParms();
}
