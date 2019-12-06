package com.lxz.opengl;

import android.app.Application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import okio.BufferedSource;
import okio.Okio;

public class ShaderUtils {
    private static final String TAG = "ShaderUtils";
    private static Application application;
    public static void init(Application app) {
        application = app;
    }


    public static String getFragmentShader(String fileName) {
        String resFile = "fragmentshader/" + fileName;
        try {
            String[] assets = application.getAssets().list("");
            Lg.d(TAG, "list " + Arrays.toString(assets));
            InputStream is = application.getAssets().open(resFile);
            BufferedSource source = Okio.buffer(Okio.source(is));
            String content = source.readUtf8();
            Lg.d(TAG, ">> getFragmentShader: " + content);
            return content;
        } catch (IOException e) {
            Lg.e(TAG, "read getFragmentShader error " + e);
        }
        return null;
    }

    public static String getVertexshader(String fileName) {
        String resFile = "vertexshader/" + fileName;
        try {
            String[] assets = application.getAssets().list("");
            Lg.d(TAG, "list " + Arrays.toString(assets));
            InputStream is = application.getAssets().open(resFile);
            BufferedSource source = Okio.buffer(Okio.source(is));
            String content = source.readUtf8();
            Lg.d(TAG, ">> getVertexshader: " + content);
            return content;
        } catch (IOException e) {
            Lg.e(TAG, "read getFragmentShader error " + e);
        }
        return null;
    }
}
