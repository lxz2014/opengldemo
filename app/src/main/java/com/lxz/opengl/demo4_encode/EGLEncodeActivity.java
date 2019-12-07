package com.lxz.opengl.demo4_encode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.View;
import android.widget.Button;

import com.lxz.opengl.Lg;
import com.lxz.opengl.R;
import com.lxz.opengl.Utils;
import com.lxz.opengl.comm.BaseActivity;
import com.lxz.opengl.demo2_glsurface.ISurfaceTextureCreate;
import com.lxz.opengl.demo2_glsurface.PlayVideoActivity;
import com.lxz.opengl.demo2_glsurface.drawer.IDrawer;
import com.lxz.opengl.demo2_glsurface.drawer.VideoDrawer;
import com.lxz.opengl.demo2_glsurface.drawer.VideoSpiritLeftFilterDrawer;
import com.lxz.opengl.demo3_egl.RenderThread;
import com.lxz.opengl.media.decode.MediaDecode;
import com.lxz.opengl.media.decode.VideoPlay;
import com.lxz.opengl.media.encode.IEncoder;
import com.lxz.opengl.media.encode.SurfaceEncoder;
import com.lxz.opengl.media.stream.FileH264Stream;
import com.lxz.opengl.media.stream.FileH264Stream2;
import com.lxz.opengl.media.stream.IH264Stream;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import androidx.annotation.Nullable;

public class EGLEncodeActivity extends BaseActivity implements View.OnClickListener {
    private static final int STATE_STOP = 2;
    private static final int REQUEST_CODE = 11;
    private final String TAG = getClass().getSimpleName();

    private Button start;
    private Button stop;

    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;

    private int state = STATE_STOP;
    private VirtualDisplay virtualDisplay;
    private int screenWidth = 1920;
    private int screenHeight = 1080;
    private int dpi = 1;
    private RenderThread renderThread;
    private IEncoder encoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtimescreen);
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        findViewById(R.id.show).setOnClickListener(this);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        findViewById(R.id.show).setOnClickListener(this);

        //
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        dpi = dm.densityDpi;
        Lg.d(TAG, "dpi %d, density:%f, w:%d, h:%d" , dpi, dm.density,screenWidth , screenHeight);

        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                startCapture();
                break;
            case R.id.stop:
                stopCapture();
                break;
            case R.id.show:
                startPlay();
                break;
        }
    }

    private void stopCapture() {
        if (virtualDisplay != null) {
            virtualDisplay.release();
        }
        virtualDisplay = null;
    }

    private void startCapture() {
        // 申请相关权限成功后，要向用户申请录屏对话框
        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            showToast("不支持录屏");
        }
    }

    private void startEGLRecode() {
        IDrawer drawer = new VideoSpiritLeftFilterDrawer();
        drawer.setISurfaceTextureCreate(new ISurfaceTextureCreate() {
            @Override
            public void onSurfaceTextureCreate(final SurfaceTexture surfaceTexture) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Lg.e(TAG, "onSurfaceTextureCreate " + surfaceTexture);
                        surfaceTexture.setDefaultBufferSize(screenWidth, screenHeight);
                        exeRecord(new Surface(surfaceTexture));
                    }
                });
            }
        });

        encoder = new SurfaceEncoder(new FileH264Stream2(), screenWidth, screenHeight);

        renderThread = new RenderThread(drawer, encoder.getSurface());
        renderThread.start();
        renderThread.onSurfaceCreate();
        start.postDelayed(new Runnable() {
            @Override
            public void run() {
                Lg.e(TAG, "onSurfaceChange");
                renderThread.onSurfaceChange(screenWidth, screenHeight);
            }
        }, 500);

    }


    private void exeRecord(Surface inputSurface) {
        this.virtualDisplay = this.mediaProjection.createVirtualDisplay(
                "Recording Display"
                , screenWidth
                , screenHeight
                , 1
                , DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
                , inputSurface
                , new VirtualDisplay.Callback() {
                    @Override
                    public void onPaused() {
                        Lg.d(TAG, "onPause");
                    }

                    @Override
                    public void onResumed() {
                        Lg.d(TAG, "onResumed");
                    }

                    @Override
                    public void onStopped() {
                        Lg.d(TAG, "onStopped");
                    }
                },
                new Handler());

        new Thread(new Runnable() {
            @Override
            public void run() {
                int fpsTime = 1000 / 15;
                while (virtualDisplay != null) {
                    long t1 = System.currentTimeMillis();
                    encoder.outputEncodeData();
                    long t2 = System.currentTimeMillis();
                    long dt = t2 - t1;
                    Utils.sleep(fpsTime <= dt ? 0 : (int) (fpsTime - dt));
                }

                showToast("停止录制");
                Lg.e(TAG, "停止录制");
                if (encoder != null) {
                    encoder.release();
                }
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
                if (state == STATE_STOP) {
                    startEGLRecode();
                }
            } else {
                showToast("录制权限竟然被拒绝了！！");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (virtualDisplay != null) {
            virtualDisplay.release();
        }
        if (renderThread != null) {
            renderThread.onSurfaceDestroy();
        }
    }

    private void startPlay() {
        Intent it = new Intent(this, PlayVideoActivity.class);
        startActivity(it);
    }
}
