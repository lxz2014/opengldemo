package com.lxz.opengl.demo3_egl;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;

import com.lxz.opengl.Lg;
import com.lxz.opengl.R;
import com.lxz.opengl.comm.BaseActivity;
import com.lxz.opengl.demo2_glsurface.ISurfaceTextureCreate;
import com.lxz.opengl.demo2_glsurface.SimpleRender;
import com.lxz.opengl.demo2_glsurface.drawer.IDrawer;
import com.lxz.opengl.demo2_glsurface.drawer.VideoSpiritLeftFilterDrawer;
import com.lxz.opengl.media.dencode.MediaDecode;
import com.lxz.opengl.media.dencode.VideoPlay;
import com.lxz.opengl.media.stream.FileH264Stream;

import androidx.annotation.Nullable;

public class PlayEGLVideoActivity extends BaseActivity {
    private CustomerRender gl_surface;
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
                videoPlay = new VideoPlay(new FileH264Stream());
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
