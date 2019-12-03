package com.lxz.opengl.demo2;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;

import com.lxz.opengl.R;
import com.lxz.opengl.Utils;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private GLSurfaceView gl_surface;
    private IDrawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gl_surface = findViewById(R.id.gl_surface);

        if (!Utils.supportGlEs20(this)) {
            Toast.makeText(this, "不支持opengl 2.0", Toast.LENGTH_SHORT).show();
            return;
        }

        drawer = new TriangleDrawer();
        initRender(new SimpleRender(drawer));
    }

    private void initRender(SimpleRender render) {
        gl_surface.setEGLContextClientVersion(2);
        gl_surface.setRenderer(render);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        drawer.release();
    }

    //    @Override
//    protected void onPause() {
//        super.onPause();
//        gl_surface.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        gl_surface.onResume();
//    }
}
