package com.lxz.opengl.demo3_egl;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.lxz.opengl.demo2_glsurface.drawer.IDrawer;

import java.lang.ref.WeakReference;

public class CustomerSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    //OpenGL渲染线程
    private RenderThread mThread ;

    //所有的绘制器
    private IDrawer mDrawer;

    public CustomerSurfaceView(Context context) {
        super(context);
        init();
    }

    public CustomerSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomerSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CustomerSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
        addOnAttachStateChangeListener(new View.OnAttachStateChangeListener(){

            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                mThread.onSurfaceStop();
                removeOnAttachStateChangeListener(this);
            }
        });
    }

    public void setDrawer(IDrawer draw) {
        this.mDrawer = draw;
        mThread = new RenderThread(draw, getHolder().getSurface());
        mThread.start();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread.onSurfaceCreate();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mThread.onSurfaceChange(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mThread.onSurfaceDestroy();
    }
}
