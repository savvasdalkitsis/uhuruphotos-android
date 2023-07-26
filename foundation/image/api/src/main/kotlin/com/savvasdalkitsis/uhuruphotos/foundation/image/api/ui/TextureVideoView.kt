package com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui

import android.content.Context
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.TextureVideoView.ScaleType.CENTER_CROP
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.TextureVideoView.ScaleType.FIT
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.TextureVideoView.State.END
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.TextureVideoView.State.PAUSE
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.TextureVideoView.State.PLAY
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.TextureVideoView.State.READY
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.TextureVideoView.State.STOP
import java.io.IOException

class TextureVideoView(context: Context) : TextureView(context), SurfaceTextureListener {

    var mediaPlayer: MediaPlayer? = null
    private var videoHeight = 0f
    private var videoWidth = 0f
    private var isDataSourceSet = false
    private var isViewAvailable = false
    private var isVideoPrepared = false
    private var isPlayCalled = false
    private var state: State = READY
    private var scaleType: ScaleType = FIT
    private var onPrepared: () -> Unit = {}
    private var onEnd: () -> Unit = {}
    private var onError: () -> Unit = {}

    enum class ScaleType {
        CENTER_CROP,
        FIT,
    }

    enum class State {
        READY,
        PLAY,
        STOP,
        PAUSE,
        END
    }

    init {
        initPlayer()
        setScaleType(CENTER_CROP)
        surfaceTextureListener = this
    }

    fun setScaleType(scaleType: ScaleType) {
        this.scaleType = scaleType
    }

    private fun initPlayer() {
        when (val m = mediaPlayer) {
            null -> mediaPlayer = MediaPlayer()
            else -> m.reset()
        }
        isVideoPrepared = false
        isPlayCalled = false
        state = READY
    }

    fun setVideoPath(path: String) {
        initPlayer()
        try {
            mediaPlayer!!.setDataSource(path)
            isDataSourceSet = true
            prepare()
        } catch (e: IOException) {
            onError()
        }
    }

    private fun prepare() {
        try {
            with(mediaPlayer!!) {
                setOnErrorListener { _, _, _ ->
                    onError()
                    false
                }
                setOnVideoSizeChangedListener { _, width, height ->
                    this@TextureVideoView.videoWidth = width.toFloat()
                    this@TextureVideoView.videoHeight = height.toFloat()
                    updateTextureViewSize()
                }
                setOnCompletionListener {
                    state = END
                    onEnd()
                }
                setOnPreparedListener {
                    isVideoPrepared = true
                    if (isPlayCalled && isViewAvailable) {
                        startPlayback()
                    }
                    onPrepared()
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            onError()
        }
    }

    fun startPlayback() {
        val m = mediaPlayer
        if (m == null || !isDataSourceSet) {
            return
        }

        isPlayCalled = true
        if (!isVideoPrepared || !isViewAvailable) {
            return
        }

        when (state) {
            PLAY -> {}
            STOP, END -> {
                state = PLAY
                m.seekTo(0)
                m.start()
            }
            PAUSE, READY -> {
                state = PLAY
                m.start()
            }
        }
    }

    fun stopPlayback() {
        if (state == STOP || state == END) {
            return
        }
        state = STOP
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
            mediaPlayer!!.seekTo(0)
        }
    }

    val isPlaying get() = mediaPlayer?.isPlaying ?: false

    fun setListener(
        onPrepared: () -> Unit = {},
        onEnd: () -> Unit = {},
        onError: () -> Unit = {},
    ) {
        this.onPrepared = onPrepared
        this.onEnd = onEnd
        this.onError = onError
    }

    override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
        when (val m = mediaPlayer) {
            null -> onError()
            else -> {
                m.setSurface(Surface(texture))
                isViewAvailable = true
                updateTextureViewSize(width, height)
                if (isDataSourceSet && isPlayCalled && isVideoPrepared) {
                    startPlayback()
                }
            }
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        updateTextureViewSize(width, height)
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture) = false

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

    private fun updateTextureViewSize(w: Int? = null, h: Int? = null) {
        val viewWidth = (w ?: width).toFloat()
        val viewHeight = (h ?: height).toFloat()

        val (scaleX, scaleY) = when(scaleType) {
            CENTER_CROP -> centerCrop(viewWidth, viewHeight, videoWidth, videoHeight)
            FIT -> 1f to 1f
        }
        setTransform(Matrix().apply {
            setScale(scaleX, scaleY, viewWidth / 2, viewHeight / 2)
        })
    }

    private fun centerCrop(viewWidth: Float, viewHeight: Float, videoWidth: Float, videoHeight: Float) =
        when {
            videoWidth > viewWidth && videoHeight > viewHeight ->
                videoWidth / viewWidth to videoHeight / viewHeight
            videoWidth < viewWidth && videoHeight < viewHeight ->
                viewHeight / videoHeight to viewWidth / videoWidth
            viewWidth > videoWidth ->
                1f to viewWidth / videoWidth / (viewHeight / videoHeight)
            viewHeight > videoHeight ->
                viewHeight / videoHeight / (viewWidth / videoWidth) to 1f
            else -> 1f to 1f
        }
}
