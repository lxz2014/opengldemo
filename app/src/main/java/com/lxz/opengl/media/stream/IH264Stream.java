package com.lxz.opengl.media.stream;

public interface IH264Stream {

    void startRecvFrame(IRecvFrameCallback callback);

    void writeFrame(byte[] h264Data);
}
