package com.lxz.opengl.media.stream;

import android.os.Environment;

import com.lxz.opengl.Lg;
import com.lxz.opengl.Utils;
import com.lxz.opengl.config.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class FileH264SizeStream extends BaseStream{
    private static final int SIZE = 1024;
    private File saveFile;
    private BufferedSource source;
    private BufferedSink sink;
    private IRecvFrameCallback callback;
    private AtomicBoolean isRecvEnd = new AtomicBoolean(true);

    public FileH264SizeStream() {
        saveFile = Config.getSaveFile();
    }

    public FileH264SizeStream(String name) {
        saveFile = Config.getSaveFile(name);
    }

    private byte[] readNextFrame() {
        try {
            byte [] size = new byte[4];
            source.readFully(size);
            int len = Utils.bytes2int(size);
            byte[] data = source.readByteArray(len);
            //Lg.i(TAG, "read len %d, data.len:%d" , len, data.length);
            //logFrame(data);
            return data;
        } catch (IOException e) {
            Lg.e(TAG, "read next frame error " + e);
        }
        return null;
    }

    @Override
    public void startRecvFrame(final IRecvFrameCallback callback1) {
        if (!isRecvEnd.get()) {
            Lg.e(TAG, "未结束");
            return;
        }

        try {
            source = Okio.buffer(Okio.source(saveFile));
        } catch (FileNotFoundException e) {
            Lg.e(TAG, "open file fail:" + e);
        }

        isRecvEnd.set(false);
        this.callback = callback1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onStart();
                }
                while (true) {
                    byte[] frame = readNextFrame();
                    if (frame == null) {
                        Lg.e(TAG, "read end..");
                        break;
                    }

                    if (callback != null) {
                        callback.onFrame(frame);
                    }
                }
                if (callback != null) {
                    callback.onEnd();
                }
                Utils.closeIo(source);
                isRecvEnd.set(true);
            }
        }).start();
    }

    @Override
    public void writeFrame(byte[] h264Data) {
        if (sink == null) {
            try {
                if (saveFile.exists()) {
                    saveFile.delete();
                    Lg.e(TAG, "delete old file ");
                }
                sink = Okio.buffer(Okio.sink(saveFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        //Lg.i(TAG, "frame len =" + h264Data.length);
        try {
            byte[] lenByte = Utils.int2bytes(h264Data.length);
            sink.write(lenByte);
            sink.write(h264Data);
            sink.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
