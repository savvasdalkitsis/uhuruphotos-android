package com.savvasdalkitsis.uhuruphotos.foundation.image.api.video

import android.graphics.Color
import android.view.View
import android.view.ViewTreeObserver
import com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui.TextureVideoView
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.lang.ref.WeakReference
import java.util.*

private val attachedLoaders by lazy {
    WeakHashMap<TextureVideoView, LoaderData>()
}

/**
 * Start loading with caching and playing.
 *
 * Note: loading will be stopped in [View.OnAttachStateChangeListener.onViewDetachedFromWindow]
 * and resumed in [View.OnAttachStateChangeListener.onViewAttachedToWindow].
 */
@Suppress("Unused")
fun TextureVideoView.playUrl(url: String, headers: Map<String, String>? = null) {
    var loader = attachedLoaders[this]

    if (loader == null) {
        loader = LoaderData(WeakReference(this))
        attachedLoaders[this] = loader
        attachToViewLifecycle(loader)
    }

    loader.apply {
        disposables.clear()
        videoToLoad = VideoRequestParam(url, headers)
        loadVideoIfHasToLoad()
        playCalled = true
    }
}

/**
 * Stop loading and playing.
 */
@Suppress("Unused")
fun TextureVideoView.stop() {
    val loader = attachedLoaders[this] ?: return
    loader.disposables.clear()
    loader.videoView?.stopPlayback()
    loader.videoView?.setBackgroundColor(Color.BLACK)
    loader.playCalled = false
}

private fun TextureVideoView.attachToViewLifecycle(loader: LoaderData) {
    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {

        override fun onViewAttachedToWindow(v: View) {
            viewTreeObserver.addOnWindowFocusChangeListener(loader.onWindowFocusListener)
            loader.loadVideoIfHasToLoad()
            if (loader.playCalled) {
                loader.videoView?.startPlayback()
            }
        }

        override fun onViewDetachedFromWindow(v: View) {
            viewTreeObserver.removeOnWindowFocusChangeListener(loader.onWindowFocusListener)
            loader.disposables.clear()
            loader.videoView?.let { videoView ->
                if (videoView.isPlaying) {
                    videoView.stopPlayback()
                }
            }
        }
    })

    if (isAttachedToWindow) {
        viewTreeObserver.addOnWindowFocusChangeListener(loader.onWindowFocusListener)
    }
}

private class LoaderData(
    val videoViewRef: WeakReference<TextureVideoView>,
) {
    var playCalled = false
    var videoToLoad: VideoRequestParam? = null
    var isLoading = false
    val disposables = CompositeDisposable()

    val onWindowFocusListener: ViewTreeObserver.OnWindowFocusChangeListener =
        ViewTreeObserver.OnWindowFocusChangeListener { hasFocus ->
            if (hasFocus && playCalled && videoView?.isPlaying != true) {
                videoView?.startPlayback()
            }
        }

    val videoView: TextureVideoView?
        get() = videoViewRef.get()

    fun loadVideoIfHasToLoad() {
        if (isLoading) {
            return
        }
        videoToLoad?.let {
            val context = videoView?.context ?: return@let
            isLoading = true
            val disposable = VideoViewCache
                .loadInFileCached(it.url, it.headers, context)
                .doFinally {
                    isLoading = false
                }
                .subscribe(
                    { filePath ->
                        if (filePath.isEmpty()) {
                            return@subscribe
                        }
                        videoToLoad = null
                        videoView?.apply {
                            setVideoPath(filePath)
                            startPlayback()
                            background = null
                        }
                    },
                    { _ -> }
                )
            disposables.add(disposable)
        }
    }
}
