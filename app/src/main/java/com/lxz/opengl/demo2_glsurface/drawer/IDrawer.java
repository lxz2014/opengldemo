package com.lxz.opengl.demo2_glsurface.drawer;

import com.lxz.opengl.demo2_glsurface.ISurfaceTextureCreate;

public interface IDrawer {
    //设置视频的原始宽高
    void setVideoSize(int videoW, int videoH);
    //设置OpenGL窗口宽高
    void setWorldSize(int worldW, int worldH);

    void setTextureID(int textureId);

    void draw();

    void release();

    void setISurfaceTextureCreate(ISurfaceTextureCreate listener);
}
