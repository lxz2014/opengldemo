package com.lxz.opengl.demo4_encode;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.lxz.opengl.Lg;
import com.lxz.opengl.R;
import com.lxz.opengl.Utils;
import com.lxz.opengl.comm.BaseActivity;
import com.lxz.opengl.demo2_glsurface.ISurfaceTextureCreate;
import com.lxz.opengl.demo2_glsurface.PlayVideoActivity;
import com.lxz.opengl.demo2_glsurface.drawer.IDrawer;
import com.lxz.opengl.demo2_glsurface.drawer.VideoSpiritLeftFilterDrawer;
import com.lxz.opengl.demo3_egl.RenderThread;
import com.lxz.opengl.media.decode.MediaDecode;
import com.lxz.opengl.media.decode.VideoPlay;
import com.lxz.opengl.media.encode.IEncoder;
import com.lxz.opengl.media.encode.SurfaceEncoder;
import com.lxz.opengl.media.stream.FileH264Stream;

public class EGLEncodeActivity2 extends BaseActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    private Button start;
    private int screenWidth = 1920;
    private int screenHeight = 1080;
    private int dpi = 1;
    private RenderThread renderThread;
    private SurfaceView surfaceview;

    private IEncoder encoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl_play);
        start = findViewById(R.id.start);
        start.setOnClickListener(this);
        surfaceview = findViewById(R.id.surfaceview);

        //
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        dpi = dm.densityDpi;
        Lg.d(TAG, "dpi %d, density:%f, w:%d, h:%d" , dpi, dm.density,screenWidth , screenHeight);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                startEGLPlay();
                break;
            case R.id.show:
                startPlay();
                break;
        }
    }


    private void startEGLPlay() {
        IDrawer drawer = new VideoSpiritLeftFilterDrawer();
        drawer.setISurfaceTextureCreate(new ISurfaceTextureCreate() {
            @Override
            public void onSurfaceTextureCreate(final SurfaceTexture surfaceTexture) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Surface inputSurface = new Surface(surfaceTexture);
                        Lg.e(TAG, "onSurfaceTextureCreate " + surfaceTexture);
                        MediaDecode decode = new MediaDecode(screenWidth, screenHeight);
                        decode.initDecode(inputSurface);
                        new VideoPlay(new FileH264Stream("save2"))
                                .setDecode(decode)
                                .startPlay();
                    }
                });
            }
        });

        encoder = new SurfaceEncoder(new FileH264Stream(), screenWidth, screenHeight);

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                int fpsTime = 1000 / 15;
                while (true) {
                    long t1 = System.currentTimeMillis();
                    encoder.outputEncodeData();
                    long t2 = System.currentTimeMillis();
                    long dt = t2 - t1;
                    Utils.sleep(fpsTime <= dt ? 0 : (int) (fpsTime - dt));
                }

            }
        }).start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (renderThread != null) {
            renderThread.onSurfaceDestroy();
        }
    }

    private void startPlay() {
        Intent it = new Intent(this, PlayVideoActivity.class);
        startActivity(it);
    }
}
