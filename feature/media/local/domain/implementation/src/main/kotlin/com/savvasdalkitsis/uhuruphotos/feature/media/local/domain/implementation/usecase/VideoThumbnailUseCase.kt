package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.usecase

import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
import android.media.MediaCodecList
import android.media.MediaCodecList.ALL_CODECS
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaFormat.KEY_BIT_RATE
import android.media.MediaFormat.KEY_COLOR_FORMAT
import android.media.MediaFormat.KEY_FRAME_RATE
import android.media.MediaFormat.KEY_I_FRAME_INTERVAL
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT
import android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH
import android.media.MediaMuxer
import android.media.MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
import android.net.Uri
import android.view.Surface
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.model.InputSurface
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.model.OutputSurface
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.model.subFile
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.model.subFolder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.math.floor

class VideoThumbnailUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun createAnimatedThumbnail(id: String, uri: Uri, width: Int): String {
        val (videoCodecInfo, mimeType) = selectCodec()
            ?: throw IllegalStateException("Could not find video codec for $OUTPUT_VIDEO_MIME_TYPE_PREFIX")
        var videoExtractor: MediaExtractor? = null
        var outputSurface: OutputSurface? = null
        var videoDecoder: MediaCodec? = null
        var videoEncoder: MediaCodec? = null
        var muxer: MediaMuxer? = null
        var inputSurface: InputSurface? = null
        try {
            videoExtractor = uri.mediaExtractor()
            val videoInputTrack = videoExtractor.getAndSelectVideoTrackIndex()
            val inputFormat = videoExtractor.getTrackFormat(videoInputTrack)
            val (outputWidth, outputHeight) = uri.extractDimensions().maybeSwap(width)
                .constraintToMultiplesOf16()
            val outputVideoFormat = createVideoFormat(mimeType, outputWidth, outputHeight)
            val (encoder, surface) = createVideoEncoder(videoCodecInfo, outputVideoFormat)
            videoEncoder = encoder
            inputSurface = InputSurface(surface).apply { makeCurrent() }
            outputSurface = OutputSurface()
            videoDecoder = createVideoDecoder(inputFormat, outputSurface.surface!!)
            val outputFile = id.outputFilePath()

            muxer = MediaMuxer(outputFile, MUXER_OUTPUT_MPEG_4)
            changeResolution(
                videoExtractor,
                videoDecoder,
                videoEncoder,
                muxer,
                inputSurface,
                outputSurface
            )
            return outputFile
        } finally {
            tryTo {
                videoExtractor?.release()
            }.tryTo {
                videoDecoder?.apply {
                    stop()
                    release()
                }
            }.tryTo {
                outputSurface?.release()
            }.tryTo {
                videoEncoder?.apply {
                    stop()
                    release()
                }
            }.tryTo {
                muxer?.apply {
                    stop()
                    release()
                }
            }.tryTo {
                inputSurface?.release()
            }?.let {
                throw it
            }
        }
    }

    private fun String.outputFilePath(): String = context.filesDir
        .subFolder("localAnimatedThumbnails")
        .subFile("${this}.mp4")
        .absolutePath

    private fun createVideoFormat(mimeType: String, outputWidth: Int, outputHeight: Int) =
        MediaFormat.createVideoFormat(mimeType, outputWidth, outputHeight).apply {
            setInteger(KEY_COLOR_FORMAT, OUTPUT_VIDEO_COLOR_FORMAT)
            setInteger(KEY_BIT_RATE, OUTPUT_VIDEO_BIT_RATE)
            setInteger(KEY_FRAME_RATE, OUTPUT_VIDEO_FRAME_RATE)
            setInteger(KEY_I_FRAME_INTERVAL, OUTPUT_VIDEO_IFRAME_INTERVAL)
        }

    private fun Pair<Int, Int>.maybeSwap(width: Int): Pair<Int, Int> {
        val ratio = second / first.toFloat()
        val height = (width * ratio).toInt()
        return if (first > second) {
            if (width < height) {
                height to width
            } else {
                width to height
            }
        } else {
            if (width > height) {
                height to width
            } else {
                width to height
            }
        }
    }

    private fun tryTo(action: () -> Unit) = null.tryTo(action)

    private fun Exception?.tryTo(action: () -> Unit) = try {
        action()
        this
    } catch (e: Exception) {
        e
    }

    private fun Uri.extractDimensions(): Pair<Int, Int> = with(mediaMetadataRetriever()) {
        try {
            int(METADATA_KEY_VIDEO_WIDTH) to int(METADATA_KEY_VIDEO_HEIGHT)
        } catch (e: Exception) {
            with(frameAtTime!!) {
                (width to height).also {
                    recycle()
                }
            }
        }
    }

    private fun MediaMetadataRetriever.int(key: Int) = extractMetadata(key)!!.toInt()

    private fun Uri.mediaMetadataRetriever() = MediaMetadataRetriever().apply {
        setDataSource(context, this@mediaMetadataRetriever)
    }

    private fun Uri.mediaExtractor() = MediaExtractor().apply {
        setDataSource(context, this@mediaExtractor, emptyMap())
    }

    private fun createVideoDecoder(inputFormat: MediaFormat, surface: Surface): MediaCodec =
        MediaCodec.createDecoderByType(inputFormat.getMimeTypeFor()!!).apply {
            configure(inputFormat, surface, null, 0)
            start()
        }

    private fun createVideoEncoder(
        codecInfo: MediaCodecInfo, format: MediaFormat
    ): Pair<MediaCodec, Surface> = MediaCodec.createByCodecName(codecInfo.name).let {
        it.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        val surface = it.createInputSurface()
        it.start()
        it to surface
    }

    private fun MediaExtractor.getAndSelectVideoTrackIndex(): Int {
        for (index in 0 until trackCount) {
            if (getTrackFormat(index).isVideoFormat()) {
                selectTrack(index)
                return index
            }
        }
        return -1
    }

    private fun changeResolution(
        videoExtractor: MediaExtractor,
        videoDecoder: MediaCodec,
        videoEncoder: MediaCodec,
        muxer: MediaMuxer,
        inputSurface: InputSurface,
        outputSurface: OutputSurface
    ) {
        val videoDecoderInputBuffers: Array<ByteBuffer?>?
        var videoDecoderOutputBuffers: Array<ByteBuffer?>?
        var videoEncoderOutputBuffers: Array<ByteBuffer?>?
        val videoDecoderOutputBufferInfo: MediaCodec.BufferInfo?
        val videoEncoderOutputBufferInfo: MediaCodec.BufferInfo?
        videoDecoderInputBuffers = videoDecoder.inputBuffers
        videoDecoderOutputBuffers = videoDecoder.outputBuffers
        videoEncoderOutputBuffers = videoEncoder.outputBuffers
        videoDecoderOutputBufferInfo = MediaCodec.BufferInfo()
        videoEncoderOutputBufferInfo = MediaCodec.BufferInfo()
        var decoderOutputVideoFormat: MediaFormat? = null
        var encoderOutputVideoFormat: MediaFormat? = null
        var outputVideoTrack = -1
        var videoExtractorDone = false
        var videoDecoderDone = false
        var videoEncoderDone = false
        var muxing = false
        while (!videoEncoderDone) {
            while (!videoExtractorDone
                && (encoderOutputVideoFormat == null || muxing)
            ) {
                val decoderInputBufferIndex = videoDecoder.dequeueInputBuffer(TIMEOUT_USEC.toLong())
                if (decoderInputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) break
                val decoderInputBuffer = videoDecoderInputBuffers[decoderInputBufferIndex]
                val size = videoExtractor.readSampleData(decoderInputBuffer!!, 0)
                val presentationTime = videoExtractor.sampleTime
                if (size >= 0) {
                    videoDecoder.queueInputBuffer(
                        decoderInputBufferIndex,
                        0,
                        size,
                        presentationTime,
                        videoExtractor.sampleFlags
                    )
                }
                videoExtractorDone = !videoExtractor.advance()
                if (videoExtractorDone) videoDecoder.queueInputBuffer(
                    decoderInputBufferIndex,
                    0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM
                )
                break
            }
            while (!videoDecoderDone
                && (encoderOutputVideoFormat == null || muxing)
            ) {
                val decoderOutputBufferIndex = videoDecoder.dequeueOutputBuffer(
                    videoDecoderOutputBufferInfo, TIMEOUT_USEC.toLong()
                )
                if (decoderOutputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) break
                if (decoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    videoDecoderOutputBuffers = videoDecoder.outputBuffers
                    break
                }
                if (decoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    decoderOutputVideoFormat = videoDecoder.outputFormat
                    break
                }
                val decoderOutputBuffer = videoDecoderOutputBuffers!![decoderOutputBufferIndex]
                if (videoDecoderOutputBufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG
                    != 0
                ) {
                    videoDecoder.releaseOutputBuffer(decoderOutputBufferIndex, false)
                    break
                }
                val render = videoDecoderOutputBufferInfo.size != 0
                videoDecoder.releaseOutputBuffer(decoderOutputBufferIndex, render)
                if (render) {
                    outputSurface.awaitNewImage()
                    outputSurface.drawImage()
                    inputSurface.setPresentationTime(
                        videoDecoderOutputBufferInfo.presentationTimeUs * 1000
                    )
                    inputSurface.swapBuffers()
                }
                if ((videoDecoderOutputBufferInfo.flags
                            and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0
                ) {
                    videoDecoderDone = true
                    videoEncoder.signalEndOfInputStream()
                }
                break
            }
            while (!videoEncoderDone
                && (encoderOutputVideoFormat == null || muxing)
            ) {
                val encoderOutputBufferIndex = videoEncoder.dequeueOutputBuffer(
                    videoEncoderOutputBufferInfo, TIMEOUT_USEC.toLong()
                )
                if (encoderOutputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) break
                if (encoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    videoEncoderOutputBuffers = videoEncoder.outputBuffers
                    break
                }
                if (encoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    encoderOutputVideoFormat = videoEncoder.outputFormat
                    break
                }
                val encoderOutputBuffer = videoEncoderOutputBuffers!![encoderOutputBufferIndex]
                if (videoEncoderOutputBufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG
                    != 0
                ) {
                    videoEncoder.releaseOutputBuffer(encoderOutputBufferIndex, false)
                    break
                }
                if (videoEncoderOutputBufferInfo.size != 0) {
                    muxer.writeSampleData(
                        outputVideoTrack, encoderOutputBuffer!!, videoEncoderOutputBufferInfo
                    )
                }
                if (videoEncoderOutputBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM
                    != 0
                ) {
                    videoEncoderDone = true
                }
                videoEncoder.releaseOutputBuffer(encoderOutputBufferIndex, false)
                break
            }
            if (!muxing && encoderOutputVideoFormat != null) {
                outputVideoTrack = muxer.addTrack(encoderOutputVideoFormat)
                muxer.start()
                muxing = true
            }
        }
    }

    private fun MediaFormat.isVideoFormat() = getMimeTypeFor().startsWith("video/")

    private fun MediaFormat.getMimeTypeFor() = getString(MediaFormat.KEY_MIME)!!

    private fun selectCodec(): Pair<MediaCodecInfo, String>? =
        MediaCodecList(ALL_CODECS).codecInfos.find { codecInfo ->
            codecInfo.isEncoder && codecInfo.supportedTypes.any {
                it.startsWith(OUTPUT_VIDEO_MIME_TYPE_PREFIX)
            }
        }?.let {
            it to it.supportedTypes.first { it.startsWith(OUTPUT_VIDEO_MIME_TYPE_PREFIX) }
        }

    private fun Pair<Int, Int>.constraintToMultiplesOf16(): Pair<Int, Int> =
        (first / 16) * 16 to (second / 16) * 16


    companion object {
        private const val TIMEOUT_USEC = 10000
        private const val OUTPUT_VIDEO_MIME_TYPE_PREFIX = "video/mp4"
        private const val OUTPUT_VIDEO_BIT_RATE = 2048 * 1024
        private const val OUTPUT_VIDEO_FRAME_RATE = 20
        private const val OUTPUT_VIDEO_IFRAME_INTERVAL = 30
        private const val OUTPUT_VIDEO_COLOR_FORMAT = COLOR_FormatSurface
    }
}