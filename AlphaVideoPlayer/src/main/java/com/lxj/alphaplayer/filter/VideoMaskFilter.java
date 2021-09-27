package com.lxj.alphaplayer.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.lxj.alphaplayer.R;
import com.lxj.alphaplayer.utils.OpenGlUtils;
import com.lxj.alphaplayer.utils.Rotation;
import com.lxj.alphaplayer.utils.TextureRotationUtil;
import javax.microedition.khronos.opengles.GL10;

public class VideoMaskFilter extends GPUImageFilter {

    private int mMixLocation;
    private float mMix;

    public VideoMaskFilter(Context context) {
        super(OpenGlUtils.readShaderFromRawResource(context, R.raw.default_vertex),
                OpenGlUtils.readShaderFromRawResource(context, R.raw.video_mask));
    }

    public VideoMaskFilter(final String vertexShader, final String fragmentShader) {
        super(vertexShader, fragmentShader);
    }

    protected void onInit() {
        super.onInit();
//        mTextureTransformMatrixLocation = GLES20.glGetUniformLocation(mGLProgId, "textureTransform");

//        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer();
        mGLTextureBuffer.clear();
        mGLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.VIDEO_RIGHT, false, false)).position(0);

        mMix = -1.0f;

        mMixLocation = GLES20.glGetUniformLocation(getProgram(), "isleft");
    }

    public void setMaskIsLeft(boolean isLeft) {

        setMix(isLeft ? -1.0f : 1.0f);
        mGLTextureBuffer.rewind();
        mGLTextureBuffer.put(TextureRotationUtil.getRotation(isLeft ? Rotation.VIDEO_RIGHT : Rotation.VIDEO_LEFT, false, false)).position(0);
    }

    public void setMaskIsTop(boolean isTop) {
        setMix(isTop ? -1.0f : 1.0f);
        mGLTextureBuffer.rewind();
        mGLTextureBuffer.put(TextureRotationUtil.getRotation(isTop ? Rotation.TEXTURE_VIDEO_BOTTOM : Rotation.TEXTURE_VIDEO_TOP, false, false)).position(0);
    }

    @Override
    public void onInitialized() {
        super.onInitialized();
        setMix(mMix);
    }

    /**
     * @param mix ranges from 0.0 (only image 1) to 1.0 (only image 2), with 0.5 (half of either) as the normal level
     */
    private void setMix(final float mix) {
        mMix = mix;
        setFloat(mMixLocation, mMix);
    }


    @Override
    protected void onDrawArraysPre() {
        GLES20.glEnable(GL10.GL_BLEND);
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_DST_ALPHA);

        super.onDrawArraysPre();
    }

    @Override
    protected void onDrawArraysAfter() {
        super.onDrawArraysAfter();
        GLES20.glDisable(GL10.GL_BLEND);
    }
}