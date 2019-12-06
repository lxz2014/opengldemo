package com.lxz.opengl.demo2_glsurface.drawer;

import android.opengl.GLES20;

import com.lxz.opengl.Lg;
import com.lxz.opengl.Utils;
import com.lxz.opengl.demo2_glsurface.ISurfaceTextureCreate;

import java.nio.FloatBuffer;

public class TriangleDrawer implements IDrawer {
    private static final String TAG = "TriangleDrawer";
    //顶点坐标
    private float[] mVertexCoors = new float[]{
            -1f, -1f,
            1f, -1f,
            0f, 1f
    };

    //纹理坐标
    private float[] mTextureCoors = new float[]{
            0f, 1f,
            1f, 1f,
            0.5f, 0f
    };

    //纹理ID
    private int mTextureId = -1;
    //OpenGL程序ID
    private int mProgram = -1;
    // 顶点坐标接收者
    private int mVertexPosHandler = -1;
    // 纹理坐标接收者
    private int mTexturePosHandler = -1;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;

    private String getVertexShader() {
        return "attribute vec4 aPosition;" +
                "void main() {" +
                "  gl_Position = aPosition;" +
                "}";
    }

    private String getFragmentShader() {
        return "precision mediump float;" +
                "void main() {" +
                "  gl_FragColor = vec4(1.0, 1.0, 0.0, 1.0);" +
                "}";
    }

    public TriangleDrawer() {
        //【步骤1: 初始化顶点坐标】
        initPos();
    }

    private void initPos() {
        //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
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
        this.mTextureId = textureId;
        Lg.e("textureId " + textureId);
    }

    @Override
    public void draw() {
        if (mTextureId != -1) {
            //【步骤2: 创建、编译并启动OpenGL着色器】
            createGLPrg();
            //【步骤3: 开始渲染绘制】
            doDraw();
        }
    }

    private void createGLPrg() {
        if (mProgram == -1) {
            int vertexShader = Utils.loadShader(GLES20.GL_VERTEX_SHADER, getVertexShader());
            int fragmentShader = Utils.loadShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShader());

            //创建OpenGL ES程序，注意：需要在OpenGL渲染线程中创建，否则无法渲染
            mProgram = GLES20.glCreateProgram();
            Lg.e(TAG, "create program %d", mProgram);
            //将顶点着色器加入到程序
            GLES20.glAttachShader(mProgram, vertexShader);
            //将片元着色器加入到程序中
            GLES20.glAttachShader(mProgram, fragmentShader);
            //连接到着色器程序
            GLES20.glLinkProgram(mProgram);

            mVertexPosHandler = GLES20.glGetAttribLocation(mProgram, "aPosition");
            mTexturePosHandler = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
        }
        //使用OpenGL程序
        GLES20.glUseProgram(mProgram);
    }

    private void doDraw() {
        Lg.e(TAG, "doDraw");
        //启用顶点的句柄
        GLES20.glEnableVertexAttribArray(mVertexPosHandler);
        GLES20.glEnableVertexAttribArray(mTexturePosHandler);
        //设置着色器参数
        GLES20.glVertexAttribPointer(mVertexPosHandler, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glVertexAttribPointer(mTexturePosHandler, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        //开始绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    @Override
    public void release() {
        Lg.e("release....");
        GLES20.glDisableVertexAttribArray(mVertexPosHandler);
        GLES20.glDisableVertexAttribArray(mTexturePosHandler);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glDeleteTextures(1, Utils.intArrayOf(mTextureId), 0);
        GLES20.glDeleteProgram(mProgram);
    }

    @Override
    public void setISurfaceTextureCreate(ISurfaceTextureCreate listener) {

    }
}
