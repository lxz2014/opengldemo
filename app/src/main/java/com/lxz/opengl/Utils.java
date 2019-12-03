package com.lxz.opengl;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

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

    public static int[] intArrayOf(int id) {
        int []d = new int[1];
        d[0] = id;
        return d;
    }
}
