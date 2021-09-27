package com.lxj.alphaplayer.view;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import com.lxj.alphaplayer.filter.MagicCameraInputFilter;
import com.lxj.alphaplayer.filter.VideoMaskFilter;
import com.lxj.alphaplayer.utils.OpenGlUtils;
import com.seu.magicfilter.R;

import java.io.IOException;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import tv.danmaku.ijk.media.exo.IjkExoMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;

public class AlphaVideoSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener, IMediaPlayer.OnCompletionListener {

    private String videoPath;
    private boolean frameAvailable = false;
    VideoMaskFilter mVideoMaskFilter;
    private boolean mIsLeft;
    private boolean mIsTop;
    private boolean isVertical = false;

    private int[] textures = new int[1];

    private int width, height;

    private float[] videoTextureTransform = new float[16];
    private SurfaceTexture videoTexture;
    private
    IMediaPlayer mediaPlayer;

    private boolean isMute;

    private int resVideo;
    private MagicCameraInputFilter inputFilter;
    private Surface mSurface;

    public AlphaVideoSurfaceView(Context context) {
        this(context, null);
    }

    public AlphaVideoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public void init() {
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 24, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
//        setZOrderMediaOverlay(true);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public AlphaVideoSurfaceView setVideoPath(String videoPath) {
        this.videoPath = videoPath;
        return this;
    }

    public AlphaVideoSurfaceView setVideoRes(int videoPath) {
        this.resVideo = videoPath;
        return this;
    }

    private boolean end;

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        end = true;
        if (onEndListener != null) {
            onEndListener.onEnd();
        }
        requestRender();
    }

    private void releaseTexture() {
        if (videoTexture != null) {
            videoTexture.release();
            videoTexture = null;
        }

        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }

    }

   private OnPlayEndListener onEndListener = null;
    public void setOnPlayEndListener(OnPlayEndListener onCompletionListener) {
        this.onEndListener = onCompletionListener;
    }

    public void seekTo(int msec) {
        mediaPlayer.seekTo(msec);
        mediaPlayer.start();
    }

    private void playVideo() {
        if (mediaPlayer == null) {
            mediaPlayer = new IjkExoMediaPlayer(getContext());
//            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
            mediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer mp) {
                    end = false;
                    if (isMute) {
                        mp.setVolume(0, 0);
                    }
                    mp.start();
                }
            });
//            mediaPlayer.setLooping(true);
            mediaPlayer.setOnCompletionListener(this);
        } else {
            mediaPlayer.reset();
        }

        releaseTexture();

        if (mSurface == null) {
            setupTexture();
            mSurface = new Surface(videoTexture);
        }
        mediaPlayer.setSurface(mSurface);


//        surface.release();
        try {
            if (!TextUtils.isEmpty(videoPath)) {
                mediaPlayer.setDataSource(videoPath);
                mediaPlayer.prepareAsync();
                end = false;
//                mediaPlayer.start();
            } else {
                AssetFileDescriptor file = getResources().openRawResourceFd(resVideo);
//                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
//                        file.getLength());
//                mediaPlayer.prepareAsync();
                file.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void start() {
        if ((videoPath == null || videoPath.isEmpty()) && (resVideo == 0)) {
            throw new IllegalStateException("Video path is null");
        }
        playVideo();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            if (onEndListener != null) {
                onEndListener.onEnd();
            }
            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }
            releaseTexture();
            end = true;
        }

    }


    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // setupTexture();
        if (isVertical) {
            mVideoMaskFilter = new VideoMaskFilter(OpenGlUtils.readShaderFromRawResource(getContext(), R.raw.default_vertex),
                    OpenGlUtils.readShaderFromRawResource(getContext(), R.raw.video_masktb));
        } else {
            mVideoMaskFilter = new VideoMaskFilter(getContext());
        }
        mVideoMaskFilter.init();
        if (isVertical) {
            mVideoMaskFilter.setMaskIsTop(mIsTop);
        } else {
            mVideoMaskFilter.setMaskIsLeft(mIsLeft);
        }

        inputFilter = new MagicCameraInputFilter(getContext());
        inputFilter.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        inputFilter.initCameraFrameBuffer(width, height);
        inputFilter.onDisplaySizeChanged(width, height);
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        if (end) {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            GLES20.glViewport(0, 0, width, height);
            return;
        }
        try {
            synchronized (this) {
                if (frameAvailable || videoTexture != null) {
                    videoTexture.updateTexImage();
                    videoTexture.getTransformMatrix(videoTextureTransform);
                    frameAvailable = false;
                } else {
                    return;
                }
            }
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            GLES20.glViewport(0, 0, width, height);

            inputFilter.setTextureTransformMatrix(videoTextureTransform);

//        if (true) {
//            inputFilter.onDrawFrame(textures[0]);
//            return;
//        }

            int textid = inputFilter.onDrawToTexture(textures[0]);
            mVideoMaskFilter.onDrawFrame(textid);
        } catch (Exception e) {

        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        synchronized (this) {
            frameAvailable = true;
            requestRender();
        }
    }

    private void setupTexture() {
        // Generate the actual texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glGenTextures(1, textures, 0);
        checkGlError("Texture generate");

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        checkGlError("Texture bind");

        videoTexture = new SurfaceTexture(textures[0]);
        videoTexture.setOnFrameAvailableListener(this);
    }

    public void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("SurfaceTest", op + ": glError " + GLUtils.getEGLErrorString(error));
        }
    }

    public long getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }
    public void setAlphaPosition(AlphaPosition position){
        if(position==AlphaPosition.Left || position==AlphaPosition.Right){
            setVideoMaskIsLeft(position==AlphaPosition.Left);
        }else {
            setVideoMaskIsTop(position==AlphaPosition.Top);
        }
    }

    private void setVideoMaskIsLeft(boolean isLeft) {
        mIsLeft = isLeft;
        if (mVideoMaskFilter != null) {
            mVideoMaskFilter.setMaskIsLeft(isLeft);
        }
    }

    private void setVideoMaskIsTop(boolean isTop) {
        isVertical = true;
        mIsTop = isTop;
        if (mVideoMaskFilter != null) {
            mVideoMaskFilter.setMaskIsTop(mIsTop);
        }
    }

    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            end = true;
            requestRender();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDestroy();
    }
}
