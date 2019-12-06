package com.lxz.opengl.media.stream;

public interface IRecvFrameCallback {
    void onFrame(byte[] bytes);

    void onStart();

    void onEnd();
}
