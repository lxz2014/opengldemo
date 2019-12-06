package com.lxz.opengl.media.dencode;

import com.lxz.opengl.Lg;
import com.lxz.opengl.Utils;
import com.lxz.opengl.media.stream.IH264Stream;
import com.lxz.opengl.media.stream.IRecvFrameCallback;

public class VideoPlay implements IRecvFrameCallback{
    private static final String TAG = "PlayMainActivity";

    private IH264Stream h264Stream;
    private int fps = 15;//每秒帧率
    private long outputBufferCount = 0;
    private long frameCount = 0;
    private MediaDecode decode;
    private long startPlayTime = 0;
    private int sleep;

    public VideoPlay(IH264Stream h264Stream) {
        this.h264Stream = h264Stream;
    }

    public void setDecode(MediaDecode decode) {
        this.decode = decode;
    }

    public void startPlay() {
        sleep = 1000 / fps;
        h264Stream.startRecvFrame(this);
    }

    @Override
    public void onFrame(byte[] frame) {
        if (decode == null) {
            return;
        }

        long t1 = System.currentTimeMillis();
        if (frame != null ) {
            decode.offerDecoder(frame, frame.length);
        }
        long t2 = System.currentTimeMillis();

        long realTime = (int) (sleep - (t2 - t1));
        //Lg.d(TAG, "frame sleep time: %d, decodetime:%d", realTime, (t2 - t1));
        Utils.sleep((int) realTime);
    }

    @Override
    public void onStart() {
        outputBufferCount = 0;
        frameCount = 0;
        startPlayTime = System.currentTimeMillis();
    }

    @Override
    public void onEnd() {
        Lg.e(TAG, "play time %d outputBufferCount %d, frameCount %d"
                , (System.currentTimeMillis() - startPlayTime)
                , outputBufferCount
                , frameCount);
    }
}
