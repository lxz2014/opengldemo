package com.lxz.opengl.demo1;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;

import com.lxz.opengl.R;
import com.lxz.opengl.Utils;

public class MainActivity extends AppCompatActivity {
    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Utils.supportGlEs20(this)) {
            Toast.makeText(this, "不支持opengl 2.0", Toast.LENGTH_SHORT).show();
            return;
        }
        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surface);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 8, 8);
        mGLSurfaceView.setRenderer(new MyRenderer());
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }
}
