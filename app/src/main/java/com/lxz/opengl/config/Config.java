package com.lxz.opengl.config;

import android.os.Environment;

import java.io.File;

public class Config {
    public static final int DATA_LEN = 1480;
    public static final int KEY_I_FRAME_INTERVAL = 1;
    private static final String EXTRA_IP = "Config";
    public static int encodeFps = 15;
    public static int decodeFps = 25;

    public static File getSaveFile() {
        return new File(Environment.getExternalStorageDirectory().getPath(), "save.h264");
    }
}
