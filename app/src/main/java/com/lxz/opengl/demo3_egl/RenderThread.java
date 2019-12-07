package com.lxz.opengl.demo3_egl;

import android.opengl.GLES20;
import android.view.Surface;

import com.lxz.opengl.Lg;
import com.lxz.opengl.Utils;
import com.lxz.opengl.demo2_glsurface.drawer.IDrawer;

import static android.opengl.EGLExt.EGL_RECORDABLE_ANDROID;

public class RenderThread extends Thread {
    private static final String TAG = "RenderThread";
    // 渲染状态
    private RenderState mState = RenderState.NO_SURFACE;

    private EGLSurfaceHolder mEGLSurface;

    // 是否绑定了EGLSurface
    private boolean mHaveBindEGLContext = false;

    //是否已经新建过EGL上下文，用于判断是否需要生产新的纹理ID
    private boolean mNeverCreateEglContext = true;

    private int mWidth = 0;
    private int mHeight = 0;

    private Object mWaitLock = new Object();
    private IDrawer drawer;
    private Surface showSurface;

    public RenderThread(IDrawer drawer, Surface surface) {
        this.drawer = drawer;
        this.showSurface = surface;
    }

    //------------第1部分：线程等待与解锁-----------------

    private void holdOn() {
        synchronized(mWaitLock) {
            try {
                mWaitLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyGo() {
        synchronized(mWaitLock) {
            mWaitLock.notify();
        }
    }

    //------------第2部分：Surface声明周期转发函数------------
    public void onSurfaceCreate() {
        mState = RenderState.FRESH_SURFACE;
        notifyGo();
    }

    public void onSurfaceChange(int width, int height) {
        mWidth = width;
        mHeight = height;
        mState = RenderState.SURFACE_CHANGE;
        notifyGo();
    }

    public void onSurfaceDestroy() {
        mState = RenderState.SURFACE_DESTROY;
        notifyGo();
    }

    public void onSurfaceStop() {
        mState = RenderState.STOP;
        notifyGo();
    }

    //------------第3部分：OpenGL渲染循环------------
    @Override
    public void run() {
        // 【1】初始化EGL
        initEGL();
        while (true) {
            if (mState == RenderState.FRESH_SURFACE) {
                Lg.e(TAG, "1 fresh_surface");
                //【2】使用surface初始化EGLSurface，并绑定上下文
                createEGLSurfaceFirst();
                holdOn();
                Lg.e(TAG, "2 fresh_surface");
            }
            else if (mState == RenderState.SURFACE_CHANGE) {
                Lg.e(TAG, "surface_change");
                createEGLSurfaceFirst();
                //【3】初始化OpenGL世界坐标系宽高
                GLES20.glViewport(0, 0, mWidth, mHeight);
                configWordSize();
                mState = RenderState.RENDERING;
            }
            else if (mState == RenderState.RENDERING ) {
                //【4】进入循环渲染
                Lg.d(TAG, "surface_render");
                render();
            }
            else if (mState == RenderState.SURFACE_DESTROY) {
                //【5】销毁EGLSurface，并解绑上下文
                Lg.d(TAG, "surface_destroy");
                destroyEGLSurface();
                mState = RenderState.NO_SURFACE;
            }
            else if (mState == RenderState.STOP ) {
                //【6】释放所有资源
                Lg.d(TAG, "surface_stop");
                releaseEGL();
                return;
            }
            else {
                holdOn();
            }

            try {
                sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //------------第4部分：EGL相关操作------------
    private void initEGL() {
        mEGLSurface = new EGLSurfaceHolder();
        mEGLSurface.init(null, EGL_RECORDABLE_ANDROID);
    }

    private void createEGLSurfaceFirst() {
        if (!mHaveBindEGLContext) {
            mHaveBindEGLContext = true;
            createEGLSurface();
            if (mNeverCreateEglContext) {
                mNeverCreateEglContext = false;
                generateTextureID();
            }
        }
    }

    private void createEGLSurface() {
        mEGLSurface.createEGLSurface(showSurface, -1, -1);
        mEGLSurface.makeCurrent();
    }

    private void destroyEGLSurface() {
        mEGLSurface.destroyEGLSurface();
        mHaveBindEGLContext = false;
    }

    private void releaseEGL() {
        mEGLSurface.release();
    }

    //------------第5部分：OpenGL ES相关操作-------------

    private void generateTextureID() {
        int [] textureIds = Utils.createTextureIds(1);
        drawer.setTextureID(textureIds[0]);
    }

    private void configWordSize() {
        drawer.setWorldSize(mWidth, mHeight);
    }

    private void render() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT & GLES20.GL_DEPTH_BUFFER_BIT);
        drawer.draw();
        mEGLSurface.swapBuffers();
    }
}
