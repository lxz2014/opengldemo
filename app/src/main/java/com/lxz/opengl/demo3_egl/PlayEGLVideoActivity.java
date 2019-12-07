package com.lxz.opengl.demo3_egl;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;

import com.lxz.opengl.Lg;
import com.lxz.opengl.R;
import com.lxz.opengl.comm.BaseActivity;
import com.lxz.opengl.demo2_glsurface.ISurfaceTextureCreate;
import com.lxz.opengl.demo2_glsurface.drawer.IDrawer;
import com.lxz.opengl.demo2_glsurface.drawer.VideoSpiritLeftFilterDrawer;
import com.lxz.opengl.media.decode.MediaDecode;
import com.lxz.opengl.media.decode.VideoPlay;
import com.lxz.opengl.media.stream.FileH264SizeStream;

import androidx.annotation.Nullable;

public class PlayEGLVideoActivity extends BaseActivity {
    private CustomerSurfaceView gl_surface;
    private static final String TAG = "PlayMainActivity";
    private IDrawer drawer;
    private MediaDecode decode;
    private VideoPlay videoPlay;
    private int width = 720;
    private int height = 1280;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl);
        gl_surface = findViewById(R.id.gl_surface);
        drawer = new VideoSpiritLeftFilterDrawer();
        drawer.setVideoSize(width, height);
        drawer.setISurfaceTextureCreate(new ISurfaceTextureCreate() {
            @Override
            public void onSurfaceTextureCreate(SurfaceTexture surfaceTexture) {
                Lg.e(TAG, "onSurfaceTextureCreate");
                decode = new MediaDecode(width, height);
                decode.initDecode(new Surface(surfaceTexture));
                videoPlay = new VideoPlay(new FileH264SizeStream());
                videoPlay.setDecode(decode);
            }
        });
        gl_surface.setDrawer(drawer);
//        gl_surface.setEGLContextClientVersion(2);
//        gl_surface.setRenderer(new SimpleRender(drawer));
        gl_surface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPlay.startPlay();
            }
        });
    }
}
