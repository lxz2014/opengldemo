package com.lxz.opengl.demo3_egl;

import android.opengl.EGLContext;
import android.opengl.EGLSurface;

public class EGLSurfaceHolder {
    private String TAG = "EGLSurfaceHolder";

    private EGLCore mEGLCore;

    private EGLSurface mEGLSurface;

    public void init(EGLContext shareContext, int flags) {
        mEGLCore = new EGLCore();
        mEGLCore.init(shareContext, flags);
    }

    public void createEGLSurface(Object surface, int width, int height) {
        if (surface != null) {
            mEGLSurface = mEGLCore.createWindowSurface(surface);
        } else {
            mEGLSurface = mEGLCore.createOffscreenSurface(width, height);
        }
    }

    public void makeCurrent() {
        if (mEGLSurface != null) {
            mEGLCore.makeCurrent(mEGLSurface);
        }
    }

    public void swapBuffers() {
        if (mEGLSurface != null) {
            mEGLCore.swapBuffers(mEGLSurface);
        }
    }

    public void destroyEGLSurface() {
        if (mEGLSurface != null) {
            mEGLCore.destroySurface(mEGLSurface);
            mEGLSurface = null;
        }
    }

    public void release() {
        mEGLCore.release();
    }
}
