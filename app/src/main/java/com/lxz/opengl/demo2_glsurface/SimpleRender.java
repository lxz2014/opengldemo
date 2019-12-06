package com.lxz.opengl.demo2_glsurface;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.lxz.opengl.Lg;
import com.lxz.opengl.Utils;
import com.lxz.opengl.demo2_glsurface.drawer.IDrawer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SimpleRender implements GLSurfaceView.Renderer {
    private static final String TAG = "SimpleRender";
    private IDrawer drawer;

    public SimpleRender(IDrawer drawer) {
        this.drawer = drawer;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Lg.e(TAG, "onSurfaceCreated");
        GLES20.glClearColor(0f, 0f, 0f, 0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        drawer.setTextureID(Utils.createTextureIds(1)[0]);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        //设置OpenGL窗口坐标
        drawer.setWorldSize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
//        GLES20.glClearColor(0f, 0f, 0f, 0f);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        drawer.draw();
    }
}
