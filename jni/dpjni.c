#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <unistd.h>
#include <jni.h>
#include "android/log.h"

#include <errno.h>
#include <string.h>
#include <dirent.h>

#include "dp_driver.h"
#include "com_ntek_dpinterface_dpjni.h"

static const char *TAG="DPJNI";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

#define DEVICE_NAME                  "/dev/dp_driver"


int init(void)
{

	int fd = open(DEVICE_NAME, O_RDWR);
    if(fd <= 0)
    {
        LOGE("open() Failed\n");
        exit(-1);
    }
    return fd;
}


JNIEXPORT void JNICALL Java_com_ntek_dpinterface_dpjni_led1control(JNIEnv *env, jobject obj, jboolean onOff)
{
	int fd = init();
	DP_DATA dp_data;
	if(onOff)
	{
		dp_data.value=1;
		LOGD("led 1 switched high");
	}
	else
	{
		dp_data.value=0;
		LOGD("led 1 switched low");
	}
	ioctl(fd, LED1_IO, &dp_data);
	close(fd);
}

JNIEXPORT void JNICALL Java_com_ntek_dpinterface_dpjni_led2control(JNIEnv *env, jobject obj, jboolean onOff)
{
	int fd = init();
	DP_DATA dp_data;
	if(onOff)
	{
		dp_data.value=1;
		LOGD("led 2 switched high");
	}
	else
	{
		dp_data.value=0;
		LOGD("led 2 switched low");
	}
	ioctl(fd, LED2_IO, &dp_data);
	close(fd);
}

JNIEXPORT void JNICALL Java_com_ntek_dpinterface_dpjni_speakerMode(JNIEnv *env, jobject obj)
{
	int fd = init();
	DP_DATA dp_data;
	ioctl(fd, SPEAKER_IO, &dp_data);
	close(fd);
}

JNIEXPORT void JNICALL Java_com_ntek_dpinterface_dpjni_handsetMode(JNIEnv *env, jobject obj)
{
	int fd = init();
	DP_DATA dp_data;
	ioctl(fd, HANDSET_IO, &dp_data);
	close(fd);
}



