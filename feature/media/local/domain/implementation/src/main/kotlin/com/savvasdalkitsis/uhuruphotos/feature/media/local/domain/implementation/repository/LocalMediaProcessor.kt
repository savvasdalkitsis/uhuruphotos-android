/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.repository

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory.decodeStream
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC
import androidx.palette.graphics.Palette
import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.media.local.LocalMediaItemDetails
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.SplittableInputStream
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.module.LocalMediaModule
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaStoreServiceItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaStoreServiceItem.Photo
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaStoreServiceItem.Video
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.usecase.BitmapUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.exif.api.usecase.ExifUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import com.savvasdalkitsis.uhuruphotos.foundation.motion.api.usecase.MotionUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import org.joda.time.format.DateTimeFormatter
import java.io.File
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest
import javax.inject.Inject

class LocalMediaProcessor @Inject constructor(
    @LocalMediaModule.LocalMediaDateTimeFormat
    private val dateTimeFormat: DateTimeFormatter,
    @ApplicationContext private val context: Context,
    private val contentResolver: ContentResolver,
    private val exifUseCase: ExifUseCase,
    private val bitmapUseCase: BitmapUseCase,
    private val motionUseCase: MotionUseCase,
) {

    fun processNewItem(
        item: LocalMediaStoreServiceItem,
        onNewItem: (LocalMediaItemDetails) -> Unit
    ) {
        val is1 = SplittableInputStream(contentResolver.openInputStream(item.contentUri)!!)
        val is2 = is1.split()
        val is3 = is1.split()
        val is4 = is1.split()
        val is5 = is1.split()

        val exif = is1.use(exifUseCase::extractFrom)
        val (size, md5) = is2.use(::sizeAndMd5)
        val motionVideoOffset = is3.use(motionUseCase::getMotionVideoOffset)

        val thumbnail = when (item) {
            is Photo -> getBitmap(is4)
            is Video -> item.bitmap
        }
        val thumbnailPath = thumbnail?.save(item, is5)
        val fallbackColor = thumbnail.palette
        thumbnail?.recycle()
        onNewItem(
            LocalMediaItemDetails(
                id = item.id,
                displayName = item.displayName,
                dateTaken = (exif.dateTime ?: item.dateTaken).toDateString(),
                bucketId = item.bucketId,
                bucketName = item.bucketName,
                width = item.width ?: 0,
                height = item.height ?: 0,
                size = item.size ?: size,
                contentUri = item.contentUri.toString(),
                md5 = md5,
                video = item is Video,
                duration = (item as? Video)?.duration,
                latLon = exif.latLon?.let { (lat, lon) -> "$lat,$lon" },
                fallbackColor = "#${fallbackColor.toUInt().toString(16).padStart(6, '0')}",
                path = item.path,
                orientation = item.orientation,
                thumbnailPath = thumbnailPath,
            )
        )
    }

    private fun sizeAndMd5(stream: InputStream): Pair<Int, String> {
        val digest = MessageDigest.getInstance("MD5")
        val buffer = ByteArray(64 * 1024)
        var totalRead = 0
        do {
            val read = stream.read(buffer)
            if (read > 0) {
                totalRead += read
                digest.update(buffer, 0, read)
            }
        } while (read > 0)
        return totalRead to BigInteger(1, digest.digest()).toString(16).padStart(32, '0')
    }

    private fun Long.toDateString(): String =
        dateTimeFormat.print(this)

    private fun getBitmap(inputStream: InputStream) = try {
        inputStream.use { stream ->
            decodeStream(stream)
        }.scale()
    } catch (e: Exception) {
        log(e)
        null
    }

    private val thumbWidth = 400

    private val Video.bitmap get() = try {
        MediaMetadataRetriever().apply {
            setDataSource(context, contentUri)
        }.getFrameAtTime(0, OPTION_CLOSEST_SYNC).scale()
    } catch (e: Exception) {
        log(e)
        null
    }


    private fun Bitmap?.scale() = try {
        this?.let { frame ->
            val ratio = frame.height / frame.width.toFloat()
            Bitmap.createScaledBitmap(frame, thumbWidth, (thumbWidth * ratio).toInt(), true).also {
                frame.recycle()
            }
        }
    }
    catch (e: Exception) {
        log(e)
        null
    }

    private val Bitmap?.palette get() = this?.let { bitmap ->
        try {
            Palette.from(bitmap).generate().lightMutedSwatch?.rgb
        } catch (e: Exception) {
            null
        }
    } ?: Color.rgb(231, 231, 231)

    private fun Bitmap?.save(item: LocalMediaStoreServiceItem, inputStream: InputStream): String? = try {
        this?.let { bitmap ->
            val file = context.filesDir.subFolder("localThumbnails").subFile("${item.id}.jpg")
            with(bitmapUseCase) {
                file.writeBytes(
                    bitmap.toJpeg().copyExifFrom(inputStream)
                )
            }
            return file.absolutePath
        }
    } catch (e: Exception) {
        log(e)
        null
    }

    private fun File.subFolder(name: String) = subFile(name).apply {
        mkdirs()
    }
    private fun File.subFile(name: String) = File(this, name)
}