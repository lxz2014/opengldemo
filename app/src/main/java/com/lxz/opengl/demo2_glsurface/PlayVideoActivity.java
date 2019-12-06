package com.lxz.opengl.demo2_glsurface;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;

import com.lxz.opengl.Lg;
import com.lxz.opengl.R;
import com.lxz.opengl.comm.BaseActivity;
import com.lxz.opengl.demo2_glsurface.drawer.IDrawer;
import com.lxz.opengl.demo2_glsurface.drawer.VideoSpiritLeftFilterDrawer;
import com.lxz.opengl.media.dencode.MediaDecode;
import com.lxz.opengl.media.dencode.VideoPlay;
import com.lxz.opengl.media.stream.FileH264Stream;

import androidx.annotation.Nullable;

public class PlayVideoActivity extends BaseActivity {
    private GLSurfaceView gl_surface;
    private static final String TAG = "PlayMainActivity";
    private IDrawer drawer;
    private MediaDecode decode;
    private VideoPlay videoPlay;
    private int width = 720;
    private int height = 1280;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gl_surface = findViewById(R.id.gl_surface);
        log();

        //drawer = new VideoMatrixDrawer();
        //drawer = new VideoScaleFilterDrawer();
        drawer = new VideoSpiritLeftFilterDrawer();
        drawer.setVideoSize(width, height);
        drawer.setISurfaceTextureCreate(new ISurfaceTextureCreate() {
            @Override
            public void onSurfaceTextureCreate(SurfaceTexture surfaceTexture) {
                Lg.e(TAG, "onSurfaceTextureCreate");
                decode = new MediaDecode(width, height);
                decode.initDecode(new Surface(surfaceTexture));
                videoPlay = new VideoPlay(new FileH264Stream());
                videoPlay.setDecode(decode);
            }
        });
        gl_surface.setEGLContextClientVersion(2);
        gl_surface.setRenderer(new SimpleRender(drawer));
        gl_surface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPlay.startPlay();
            }
        });
    }

    private void log() {
        SurfaceHolder holder = gl_surface.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Lg.e("surfaceCreated");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Lg.e("surfaceDestroyed");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Lg.d(TAG, "surfaceChanged w:%d, h:%d" , width, height);
            }
        });
    }
}
