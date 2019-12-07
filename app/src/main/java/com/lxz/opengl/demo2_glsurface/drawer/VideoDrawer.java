package com.lxz.opengl.demo2_glsurface.drawer;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.lxz.opengl.Lg;
import com.lxz.opengl.Utils;
import com.lxz.opengl.demo2_glsurface.ISurfaceTextureCreate;

import java.nio.FloatBuffer;

public class VideoDrawer implements IDrawer {
    private static final String TAG = "VideoDrawer";
    // 顶点坐标
    private float[] mVertexCoors = new float[]{
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
    };

    // 纹理坐标
//    private float[] mTextureCoors = new float[]{
//                0f, 0.5f,
//                0.5f, 0.5f,
//                0f, 0f,
//                0.5f, 0f
//    };

    private float[] mTextureCoors = new float[]{
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };

    private int mTextureId = -1;

    //OpenGL程序ID
    private int mProgram = -1;
    // 顶点坐标接收者
    private int mVertexPosHandler = -1;
    // 纹理坐标接收者
    private int mTexturePosHandler = -1;
    // 纹理接收者
    private int mTextureHandler = -1;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    private SurfaceTexture mSurfaceTexture;
    private ISurfaceTextureCreate listener;

    public VideoDrawer() {
        // 【步骤1: 初始化顶点坐标】
        mVertexBuffer = Utils.asFloatBuffer(mVertexCoors);
        mTextureBuffer = Utils.asFloatBuffer(mTextureCoors);
    }

    @Override
    public void setVideoSize(int videoW, int videoH) {

    }

    @Override
    public void setWorldSize(int worldW, int worldH) {

    }

    @Override
    public void setTextureID(int textureId) {
        Lg.d(TAG, "setTextureID " + textureId);
        this.mTextureId = textureId;
        mSurfaceTexture = new SurfaceTexture(textureId);
        if (listener != null) {
            listener.onSurfaceTextureCreate(mSurfaceTexture);
        }
    }

    @Override
    public void draw() {
        if (mTextureId != -1) {
            //【步骤2: 创建、编译并启动OpenGL着色器】
            createGLPrg();
            //【步骤3: 激活并绑定纹理单元】
            activateTexture();
            //【步骤4: 绑定图片到纹理单元】
            updateTexture();
            //【步骤5: 开始渲染绘制】
            doDraw();
        }
    }
    private void createGLPrg() {
        if (mProgram == -1) {
            int vertexShader = Utils.loadShader(GLES20.GL_VERTEX_SHADER, getVertexShader());
            int fragmentShader = Utils.loadShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShader());

            //创建OpenGL ES程序，注意：需要在OpenGL渲染线程中创建，否则无法渲染
            mProgram = GLES20.glCreateProgram();
            //将顶点着色器加入到程序
            GLES20.glAttachShader(mProgram, vertexShader);
            //将片元着色器加入到程序中
            GLES20.glAttachShader(mProgram, fragmentShader);
            //连接到着色器程序
            GLES20.glLinkProgram(mProgram);

            mVertexPosHandler = GLES20.glGetAttribLocation(mProgram, "aPosition");
            mTexturePosHandler = GLES20.glGetAttribLocation(mProgram, "aCoordinate");

            mTextureHandler = GLES20.glGetUniformLocation(mProgram, "uTexture");
        }
        //使用OpenGL程序
        GLES20.glUseProgram(mProgram);

    }

    private void activateTexture() {
        //激活指定纹理单元
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        //绑定纹理ID到纹理单元
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureId);
        //将激活的纹理单元传递到着色器里面
        GLES20.glUniform1i(mTextureHandler, 1);
        //配置边缘过渡参数
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
    }

    private void updateTexture() {
        mSurfaceTexture.updateTexImage();
    }

    private void doDraw() {
        //启用顶点的句柄
        GLES20.glEnableVertexAttribArray(mVertexPosHandler);
        GLES20.glEnableVertexAttribArray(mTexturePosHandler);
        //设置着色器参数， 第二个参数表示一个顶点包含的数据数量，这里为xy，所以为2
        GLES20.glVertexAttribPointer(mVertexPosHandler, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glVertexAttribPointer(mTexturePosHandler, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        //开始绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    @Override
    public void release() {
        GLES20.glDisableVertexAttribArray(mVertexPosHandler);
        GLES20.glDisableVertexAttribArray(mTexturePosHandler);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glDeleteTextures(1, Utils.intArrayOf(mTextureId), 0);
        GLES20.glDeleteProgram(mProgram);
    }

    @Override
    public void setISurfaceTextureCreate(ISurfaceTextureCreate listener) {
        this.listener = listener;
    }

    private String getVertexShader() {
        return "attribute vec4 aPosition;" +
                "attribute vec2 aCoordinate;" +
                "varying vec2 vCoordinate;" +
                "void main() {" +
                "    gl_Position = aPosition;" +
                "    vCoordinate = aCoordinate;" +
                "}";
    }

    private String getFragmentShader() {
        //一定要加换行"\n"，否则会和下一行的precision混在一起，导致编译出错
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;" +
                "varying vec2 vCoordinate;" +
                "uniform samplerExternalOES uTexture;" +
                "void main() {" +
                "  gl_FragColor=texture2D(uTexture, vCoordinate);" +
                "}";
    }
}
