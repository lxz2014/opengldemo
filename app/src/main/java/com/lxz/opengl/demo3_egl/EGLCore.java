package com.lxz.opengl.demo3_egl;

import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.view.Surface;

import com.lxz.opengl.Lg;
import com.lxz.opengl.Utils;

public class EGLCore {
    private final String TAG = "EGLCore";

    public final int FLAG_RECORDABLE = 0x01;

    public final int EGL_RECORDABLE_ANDROID = 0x3142;

    // EGL相关变量
    private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
    private EGLConfig mEGLConfig = null;

    /**
     * 初始化EGLDisplay
     * @param eglContext 共享上下文
     */
    public void init(EGLContext eglContext, int flags) {
        if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("EGL already set up");
        }

        EGLContext sharedContext = eglContext == null ? EGL14.EGL_NO_CONTEXT : eglContext;
        // 1，创建 EGLDisplay
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("Unable to get EGL14 display");
        }

        // 2，初始化 EGLDisplay
        int[] version = new int[2];
        if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
            mEGLDisplay = EGL14.EGL_NO_DISPLAY;
            throw new RuntimeException("unable to initialize EGL14");
        }

        // 3，初始化EGLConfig，EGLContext上下文
        if (mEGLContext == EGL14.EGL_NO_CONTEXT) {
            EGLConfig eglConfig = getConfig(flags, 2);
            if (eglConfig == null) {
                throw new RuntimeException("Unable to find a suitable EGLConfig");
            }

            int[] attr2List = Utils.intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE);
            EGLContext context = EGL14.eglCreateContext(mEGLDisplay
                                            , eglConfig
                                            , sharedContext
                                            , attr2List
                                            , 0);
            mEGLConfig = eglConfig;
            mEGLContext = context;
        }
    }

    /**
     * 获取EGL配置信息
     * @param flags 初始化标记
     * @param version EGL版本
     */
    private EGLConfig getConfig(int flags, int version){
        int renderableType = EGL14.EGL_OPENGL_ES2_BIT;
        if (version >= 3) {
            // 配置EGL 3
            renderableType = renderableType | EGLExt.EGL_OPENGL_ES3_BIT_KHR;
        }

        // 配置数组，主要是配置RAGA位数和深度位数
        // 两个为一对，前面是key，后面是value
        // 数组必须以EGL14.EGL_NONE结尾
        int[] attrList = Utils.intArrayOf(
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                //EGL14.EGL_DEPTH_SIZE, 16,
                //EGL14.EGL_STENCIL_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, renderableType,
                EGL14.EGL_NONE, 0, // 用于替换 占位用的，当flags 是 FLAG_RECORDABLE时候替换
                EGL14.EGL_NONE
        );

        //配置Android指定的标记
        if ((flags & FLAG_RECORDABLE) != 0) {
            attrList[attrList.length - 3] = EGL_RECORDABLE_ANDROID;
            attrList[attrList.length - 2] = 1;
        }
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];

        //获取可用的EGL配置列表
        if (!EGL14.eglChooseConfig(mEGLDisplay
                , attrList
                , 0
                , configs
                , 0
                , configs.length
                , numConfigs
                , 0)) {
            Lg.e(TAG, "Unable to find RGB8888 / $version EGLConfig");
            return null;
        }

        //使用系统推荐的第一个配置
        return configs[0];
    }

    /**
     * 创建可显示的渲染缓存
     * @param surface 渲染窗口的surface
     */
    public EGLSurface createWindowSurface(Object surface) {
        if (!(surface instanceof Surface) && !(surface instanceof SurfaceTexture)) {
            throw new RuntimeException("Invalid surface: $surface");
        }

        int[] surfaceAttr = Utils.intArrayOf(EGL14.EGL_NONE);
        Lg.d(TAG, "createWindowSurface " + surface);
        EGLSurface eglSurface = EGL14.eglCreateWindowSurface(
                                      mEGLDisplay
                                    , mEGLConfig
                                    , surface
                                    , surfaceAttr
                                    , 0);

        if (eglSurface == null) {
            throw new RuntimeException("Surface was null");
        }

        return eglSurface;
    }

    /**
     * 创建离屏渲染缓存
     * @param width 缓存窗口宽
     * @param height 缓存窗口高
     */
    public EGLSurface createOffscreenSurface(int width, int height) {
        int[] surfaceAttr = Utils.intArrayOf(
                                      EGL14.EGL_WIDTH , width
                                    , EGL14.EGL_HEIGHT, height
                                    , EGL14.EGL_NONE);

        EGLSurface eglSurface = EGL14.eglCreatePbufferSurface(
                  mEGLDisplay
                , mEGLConfig
                , surfaceAttr
                , 0);

        if (eglSurface == null) {
            throw new RuntimeException("Surface was null");
        }

        return eglSurface;
    }

    /**
     * 将当前线程与上下文进行绑定
     */
    public void makeCurrent(EGLSurface eglSurface) {
        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("EGLDisplay is null, call init first");
        }

        if (!EGL14.eglMakeCurrent(mEGLDisplay, eglSurface, eglSurface, mEGLContext)) {
            throw new RuntimeException("makeCurrent(eglSurface) failed");
        }
    }

    /**
     * 将当前线程与上下文进行绑定
     */
    public void makeCurrent(EGLSurface drawSurface, EGLSurface readSurface) {
        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("EGLDisplay is null, call init first");
        }
        if (!EGL14.eglMakeCurrent(mEGLDisplay, drawSurface, readSurface, mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent(draw,read) failed");
        }
    }

    /**
     * 将缓存图像数据发送到设备进行显示
     */
    public boolean swapBuffers(EGLSurface eglSurface) {
        return EGL14.eglSwapBuffers(mEGLDisplay, eglSurface);
    }

    /**
     * 设置当前帧的时间，单位：纳秒
     */
    public void setPresentationTime(EGLSurface eglSurface, long nsecs) {
        EGLExt.eglPresentationTimeANDROID(mEGLDisplay, eglSurface, nsecs);
    }

    /**
     * 销毁EGLSurface，并解除上下文绑定
     */
    public void destroySurface(EGLSurface elg_surface) {
        EGL14.eglMakeCurrent(
                mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_CONTEXT
        );
        EGL14.eglDestroySurface(mEGLDisplay, elg_surface);
    }

    /**
     * 释放资源
     */
        public void release() {
        if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            // Android is unusual in that it uses a reference-counted EGLDisplay.  So for
            // every eglInitialize() we need an eglTerminate().
            EGL14.eglMakeCurrent(
                    mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_CONTEXT
            );
            EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
            EGL14.eglReleaseThread();
            EGL14.eglTerminate(mEGLDisplay);
        }

        mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        mEGLContext = EGL14.EGL_NO_CONTEXT;
        mEGLConfig = null;
    }
}
