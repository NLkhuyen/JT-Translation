LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# OpenCV
OPENCV_CAMERA_MODULES:=on
OPENCV_INSTALL_MODULES:=on

include D:\Sdk_android\adt-bundle-windows-x86_64-20140702\work_space\OpenCV-2.4.10-android-sdk\OpenCV-2.4.10-android-sdk\sdk\native\jni\OpenCV.mk

LOCAL_MODULE    := NewDemoProject
LOCAL_SRC_FILES := NewDemoProject.cpp

include $(BUILD_SHARED_LIBRARY)
