package com.lxz.opengl.media.encode;


import com.lxz.opengl.media.stream.IH264Stream;

public abstract class BaseEncoder implements IEncoder {
    private static final String TAG = "BaseEncoder";
    protected MediaEncoder avcEncoder;
    protected IH264Stream outStream;
    protected int screenWidth;
    protected int screenHeight;
    protected boolean isStopEncode;

    public BaseEncoder(IH264Stream outStream, int screenWidth, int screenHeight) {
        this.outStream = outStream;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        isStopEncode = false;
    }

    protected int frameHead(byte[] h264Data) {
        return h264Data[4] & 0x1f;
    }

    @Override
    public boolean isStopEncode() {
        return isStopEncode;
    }

    @Override
    public void release() {
        isStopEncode = true;
        if (avcEncoder != null) {
            avcEncoder.close();
        }
    }
}
