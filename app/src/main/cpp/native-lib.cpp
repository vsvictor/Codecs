#include <jni.h>
#include <string>
#include <android/looper.h>
#include "encoder.h"
#include <media/NdkMediaCodec.h>



extern "C"

JNIEXPORT jboolean JNICALL Java_com_codecs_MainActivity_isEncoder(JNIEnv *env, jobject instance, jobject info) {
    jclass codecInfo = env->GetObjectClass(info);
    jmethodID id = env->GetMethodID(codecInfo,"isEncoder","()Z");
    jboolean result = JNI_FALSE;
    if(id != NULL){
        result = env->CallBooleanMethod(codecInfo,id);
    }
    return result;
}



JNIEXPORT jobjectArray JNICALL Java_com_codecs_MainActivity_getListCodec(JNIEnv *env, jobject obj) {
    jclass list = env->FindClass("android/media/MediaCodecList");
    jclass info = env->FindClass("android/media/MediaCodecInfo");
    jobjectArray arr;
    jint count = -1;
    jmethodID mid = env->GetStaticMethodID(list,"getCodecCount", "()I");
    if(mid != 0) {
        count = env->CallStaticIntMethod(list, mid);
        arr = env->NewObjectArray(count,info, NULL);

        for(int i = 0; i<count;i++){
            jmethodID id = env->GetStaticMethodID(list,"getCodecInfoAt","(I)Landroid/media/MediaCodecInfo;");
            jobject curr = env->CallStaticObjectMethod(list, id, i);
            env->SetObjectArrayElement(arr, i, curr);
        }

    }
    return arr;
}
JNIEXPORT void JNICALL Java_com_codecs_MainActivity_InitParms(JNIEnv *env, jobject obj) {
    vos::medialib::H264EncoderFilter_Internal* enc = new vos::medialib::H264EncoderFilter_Internal();
}
