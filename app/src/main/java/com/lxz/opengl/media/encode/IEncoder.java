package com.lxz.opengl.media.encode;

import android.view.Surface;

public interface IEncoder {
    Surface getSurface();

    void outputEncodeData();

    void release();

    boolean isStopEncode();
}
