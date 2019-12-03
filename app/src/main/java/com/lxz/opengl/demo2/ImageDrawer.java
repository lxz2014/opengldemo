package com.lxz.opengl.demo2;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.lxz.opengl.Lg;
import com.lxz.opengl.Utils;

import java.nio.FloatBuffer;

public class ImageDrawer implements IDrawer {
    private static final String TAG = "ImageDrawer";
    //-------【注1：坐标变更了，由四个点组成一个四边形】-------
    // 顶点坐标
    private float[] mVertexCoors = new float[]{
                    -1f, -1f,
                    1f, -1f,
                    -1f, 1f,
                    1f, 1f};

    // 纹理坐标
    private float[] mTextureCoors = new float[]{
                    0f, 1f,
                    1f, 1f,
                    0f, 0f,
                    1f, 0f};

    //-------【注2：新增纹理接收者】-------
    // 纹理接收者
    private int mTextureId = -1;
    private int mProgram;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    private int mVertexPosHandler;
    private int mTexturePosHandler;
    private int mTextureHandler;
    private Bitmap mBitmap;

    public ImageDrawer(Bitmap bmp) {
        this.mBitmap = bmp;
        //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
        mVertexBuffer = Utils.asFloatBuffer(mVertexCoors);
        mTextureBuffer = Utils.asFloatBuffer(mTextureCoors);
    }

    @Override
    public void setTextureID(int textureId) {
        this.mTextureId = textureId;
    }

    @Override
    public void draw() {
        if (mTextureId != -1) {
            //【步骤2: 创建、编译并启动OpenGL着色器】
            createGLPrg();
            //-------【注4：新增两个步骤】-------
            //【步骤3: 激活并绑定纹理单元】
            activateTexture();
            //【步骤4: 绑定图片到纹理单元】
            bindBitmapToTexture();
            //----------------------------------
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
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //绑定纹理ID到纹理单元
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        //将激活的纹理单元传递到着色器里面
        GLES20.glUniform1i(mTextureHandler, 0);
        //配置边缘过渡参数
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    private void bindBitmapToTexture() {
        if (!mBitmap.isRecycled()) {
            //绑定图片到被激活的纹理单元
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
        }
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

    private String getVertexShader() {
        return "attribute vec4 aPosition;" +
                "attribute vec2 aCoordinate;" +
                "varying vec2 vCoordinate;" +
                "void main() {" +
                "  gl_Position = aPosition;" +
                "  vCoordinate = aCoordinate;" +
                "}";
    }

    private String getFragmentShader() {
        return "precision mediump float;" +
                "uniform sampler2D uTexture;" +
                "varying vec2 vCoordinate;" +
                "void main() {" +
                "  vec4 color = texture2D(uTexture, vCoordinate);" +
                "  gl_FragColor = color;" +
                "}";
    }

    @Override
    public void release() {

    }
}
