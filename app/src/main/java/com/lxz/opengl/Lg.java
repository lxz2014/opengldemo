package com.lxz.opengl;

import android.util.Log;

public class Lg {
    private static String pre = "opengl-";
    public static void e(String msg) {
        Log.e(pre + "tag", msg);
    }

    public static void e(String tag, String msg) {
        Log.e(pre + tag, msg);
    }

    public static void e(String tag, String msg, Object ...args) {
        Log.e(pre + tag, String.format(msg, args));
    }

    public static void d(String msg) {
        Log.i(pre + "tag", msg);
    }

    public static void d(String tag, String msg) {
        Log.i(pre + tag, msg);
    }

    public static void d(String tag, String msg, Object ...args) {
        Log.i(pre + tag, String.format(msg, args));
    }

    public static void i(String tag, String msg, Object ...args) {
        Log.i(pre + tag, String.format(msg, args));
    }
}
