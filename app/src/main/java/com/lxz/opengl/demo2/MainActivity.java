package com.lxz.opengl.demo2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Toast;

import com.lxz.opengl.Lg;
import com.lxz.opengl.R;
import com.lxz.opengl.Utils;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
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

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.teacher_img_head);
        //drawer = new TriangleDrawer();
        Lg.d(TAG, "BMP %d %d", bmp.getWidth(), bmp.getHeight());
        drawer = new ImageDrawer(bmp);
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

//        @Override
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
