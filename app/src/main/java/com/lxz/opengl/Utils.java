package com.lxz.opengl;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLES20;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import okio.BufferedSource;

public class Utils {
    public static boolean supportGlEs20(Activity activity) {
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getDeviceConfigurationInfo().reqGlEsVersion >= 0x20000;
    }

    public static int[] createTextureIds(int count) {
        int[] texture = new int[count];
        GLES20.glGenTextures(count, texture, 0); //生成纹理
        return texture;
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public static FloatBuffer asFloatBuffer(float[] floatArray) {
        ByteBuffer bb = ByteBuffer.allocateDirect(floatArray.length * 4)
                .order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = bb.asFloatBuffer();
        floatBuffer.put(floatArray);
        floatBuffer.position(0);
        return floatBuffer;
    }

    public static int[] intArrayOf(int ...ids) {
        if (ids == null || ids.length == 0) {
            return null;
        }
        return ids;
    }

    public static void sleep(int realTime) {
        try {
            Thread.sleep(realTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ;
    }

    public static void closeIo(Closeable source) {
        if (source != null) {
            try {
                source.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 整型转换成字节数组
     */
    public static byte[] int2bytes(int i){
        byte[] bt = new byte[4];
        bt[0] = (byte) (0xff & i);
        bt[1] = (byte) ((0xff00 & i) >> 8);
        bt[2] = (byte) ((0xff0000 & i) >> 16);
        bt[3] = (byte) ((0xff000000 & i) >> 24);
        return bt;
    }

    /**
     * 字节数组转成int
     */
    public static int bytes2int(byte[] bytes){
        int value, offset = 0;
        value = (int) ((bytes[offset] & 0xFF)
                | ((bytes[offset + 1] << 8) & 0xFF00)
                | ((bytes[offset + 2] << 16) & 0xFF0000)
                | ((bytes[offset + 3] << 24) & 0xFF000000));
        return value;
    }
}
