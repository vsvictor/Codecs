#include <jni.h>
#include <string>

extern "C"

JNIEXPORT jobjectArray JNICALL Java_com_codecs_MainActivity_getListCodec(JNIEnv *env, jobject obj) {
    jclass list = env->FindClass("android/media/MediaCodecList");
    jclass info = env->FindClass("android/media/MediaCodecInfo");
    jobjectArray arr;
    jint count = -1;
    jmethodID mid = env->GetStaticMethodID(list,"getCodecCount", "()I");
    if(mid != 0) {
        count = env->CallStaticIntMethod(list, mid);
        arr = env->NewObjectArray(count,info, NULL);
        jmethodID id = env->GetStaticMethodID(list,"getCodecInfoAt","(I)Landroid/media/MediaCodecInfo");
        if(id != NULL) {
            for(int i = 0; i<count;i++){
                jobject curr = env->CallStaticObjectMethod(list, id, i);
                env->SetObjectArrayElement(arr, i, curr);
            }
        }
    }
    return arr;
}

