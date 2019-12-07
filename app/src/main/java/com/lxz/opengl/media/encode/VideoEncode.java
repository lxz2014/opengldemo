package com.lxz.opengl.media.encode;

import com.lxz.opengl.Lg;
import com.lxz.opengl.Utils;

public class VideoEncode implements Runnable{
    private static final String TAG = "VideoEncode";
    private IEncoder encoder;
    private boolean isStart = false;

    public VideoEncode(IEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public void run() {
        isStart = true;
        int fpsTime = 1000 / 15;
        while (isStart) {
            long t1 = System.currentTimeMillis();
            encoder.outputEncodeData();
            long t2 = System.currentTimeMillis();
            long dt = t2 - t1;
            Utils.sleep(fpsTime <= dt ? 0 : (int) (fpsTime - dt));
        }

        Lg.e(TAG, "停止录制");
        if (encoder != null) {
            encoder.release();
        }

    }

    public void stop() {
        isStart = false;
    }
}
