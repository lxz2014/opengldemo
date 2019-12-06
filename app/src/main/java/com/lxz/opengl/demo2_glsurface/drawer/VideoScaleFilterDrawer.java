package com.lxz.opengl.demo2_glsurface.drawer;

import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.lxz.opengl.Lg;
import com.lxz.opengl.ShaderUtils;
import com.lxz.opengl.Utils;
import com.lxz.opengl.demo2_glsurface.ISurfaceTextureCreate;

import java.nio.FloatBuffer;

public class VideoScaleFilterDrawer implements IDrawer {
    private static final String TAG = "VideoScaleFilterDrawer";
    // 顶点坐标
    private float[] mVertexCoors = new float[]{
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
    };

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

    private int mWorldWidth = -1;
    private int mWorldHeight = -1;
    private int mVideoWidth = -1;
    private int mVideoHeight = -1;
    private int mVertexTimeHandler = -1;
    private float count;

//    //坐标变换矩阵
//    private float mMatrix[] = null;
//
//    //矩阵变换接收者
//    private int mVertexMatrixHandler = -1;

    public VideoScaleFilterDrawer() {
        // 【步骤1: 初始化顶点坐标】
        mVertexBuffer = Utils.asFloatBuffer(mVertexCoors);
        mTextureBuffer = Utils.asFloatBuffer(mTextureCoors);
    }

    @Override
    public void setVideoSize(int videoW, int videoH) {
        mVideoWidth = videoW;
        mVideoHeight = videoH;
    }

    @Override
    public void setWorldSize(int worldW, int worldH) {
        mWorldWidth = worldW;
        mWorldHeight = worldH;
    }

    @Override
    public void setTextureID(int textureId) {
        this.mTextureId = textureId;
        mSurfaceTexture = new SurfaceTexture(textureId);
        if (listener != null) {
            listener.onSurfaceTextureCreate(mSurfaceTexture);
        }
    }

    @Override
    public void draw() {
        if (mTextureId != -1) {
            //initDefMatrix();
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

//    private void initDefMatrix() {
//        if (mMatrix != null) return;
//        if (mVideoWidth != -1 && mVideoHeight != -1 && mWorldWidth != -1 && mWorldHeight != -1) {
//            mMatrix = new float[16];
//            Matrix.orthoM(
//                    mMatrix, 0,
//                    -1f, 1f,
//                    -1, 1,
//                    -1f, 3f
//            );
//        }
//    }

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
            mTextureHandler = GLES20.glGetUniformLocation(mProgram, "uTexture");
            mTexturePosHandler = GLES20.glGetAttribLocation(mProgram, "aCoordinate");
            mVertexTimeHandler = GLES20.glGetUniformLocation(mProgram, "updateTime");

            Lg.d(TAG, "mTexturePosHandler %d mVertexTimeHandler %d" , mTexturePosHandler, mVertexTimeHandler);
            //【新增2: 获取顶点着色器中的矩阵变量】
//            mVertexMatrixHandler = GLES20.glGetUniformLocation(mProgram, "uMatrix");
//            Lg.d(TAG, "mVertexMatrixHandler " + mVertexMatrixHandler);
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
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    private void updateTexture() {
        mSurfaceTexture.updateTexImage();

    }

    private void doDraw() {
        //启用顶点的句柄
        GLES20.glEnableVertexAttribArray(mVertexPosHandler);
        GLES20.glEnableVertexAttribArray(mTexturePosHandler);

        // 【新增3: 将变换矩阵传递给顶点着色器】
        //GLES20.glUniformMatrix4fv(mVertexMatrixHandler, 1, false, mMatrix, 0);
        count++;
        //Lg.d(TAG, "time %f", count);
        GLES20.glUniform1f(mVertexTimeHandler, count);

//        float duration = 6f;
//        float maxAmplitude = 0.3f;
//        float time = count / duration;
//        float amplitude = (float) (1.0 + maxAmplitude * Math.abs(Math.sin(time * (Math.PI / duration))));
//        Lg.d(TAG, "time %f  amplitude %f", time, amplitude);

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
        return ShaderUtils.getVertexshader("scale_filter");
    }

    private String getFragmentShader(){
        return ShaderUtils.getFragmentShader("wb_filter");
    }
}
