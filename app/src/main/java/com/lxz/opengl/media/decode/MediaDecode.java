package com.lxz.opengl.media.decode;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.lxz.opengl.Lg;

import java.nio.ByteBuffer;

public class MediaDecode {
    private static final String MIME_TYPE = "video/avc";
    private static final String TAG = "PlayMainActivity";
    private MediaCodec decode;
    private int width = 720;
    private int height = 1280;
    private int bitrate = width * height * 3;//编码比特率，
    private long timeoutUs = 100;

    public MediaDecode(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void initDecode(Surface surface) {
        try {
            decode = MediaCodec.createDecoderByType(MIME_TYPE);
        } catch (Exception e) {
            Lg.e(TAG, "init decode error " + e);
        }
        Lg.d(TAG, "");
        final MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, width, height);
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitrate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        decode.configure(format, surface, null, 0);
        decode.start();
    }

    public void offerDecoder(byte[] input, int length) {
        try {
            int inputBufferIndex = decode.dequeueInputBuffer(0);
            if (inputBufferIndex >= 0) {
                ByteBuffer inputBuffer = decode.getInputBuffer(inputBufferIndex);
                inputBuffer.clear();
                try {
                    inputBuffer.put(input, 0, length);
                } catch (Exception e) {
                    Lg.e(TAG, "offerDecoder input buffer error " + e);
                }
                decode.queueInputBuffer(inputBufferIndex, 0, length, System.nanoTime(), 0);
            }
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = decode.dequeueOutputBuffer(bufferInfo, timeoutUs);
            while (outputBufferIndex >= 0) {
                decode.releaseOutputBuffer(outputBufferIndex, true);

                outputBufferIndex = decode.dequeueOutputBuffer(bufferInfo, timeoutUs);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void destroyDecode(SurfaceHolder holder) {
        if (decode != null) {
            decode.release();
        }
        decode = null;
    }
}
