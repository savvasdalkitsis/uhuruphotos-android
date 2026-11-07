/*
Copyright 2024 Savvas Dalkitsis

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
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC
import androidx.exifinterface.media.ExifInterface
import androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
import androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaStoreServiceItem
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaStoreServiceItem.Photo
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.service.model.LocalMediaStoreServiceItem.Video
import com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.usecase.BitmapUseCase
import com.savvasdalkitsis.uhuruphotos.foundation.log.api.log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class LocalMediaThumbnails @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contentResolver: ContentResolver,
    private val bitmapUseCase: BitmapUseCase,
) {

    private val thumbWidth = 400
    private val folder get() = File(context.filesDir, "localThumbnails")

    fun bitmap(item: LocalMediaStoreServiceItem): Bitmap? = when (item) {
        is Photo -> item.bitmap
        is Video -> item.bitmap
    }

    fun save(bitmap: Bitmap, item: LocalMediaStoreServiceItem): String {
        val file = File(folder.apply { mkdirs() }, "${item.id}.jpg")
        with(bitmapUseCase) {
            file.writeBytes(bitmap.toJpeg().copyExifFrom(item.contentUri))
        }
        ExifInterface(file).apply {
            setAttribute(TAG_ORIENTATION, ORIENTATION_NORMAL.toString())
            saveAttributes()
        }
        return file.absolutePath
    }

    fun delete(vararg ids: Long) {
        ids.forEach { File(folder, "$it.jpg").delete() }
    }

    fun deleteAllExcept(ids: Set<Long>) {
        folder.listFiles().orEmpty()
            .filter { it.nameWithoutExtension.toLongOrNull() !in ids }
            .forEach { it.delete() }
    }

    private val Video.bitmap get() = try {
        MediaMetadataRetriever().apply {
            setDataSource(context, contentUri)
        }.getFrameAtTime(0, OPTION_CLOSEST_SYNC).scale()
    } catch (e: Exception) {
        log(e)
        null
    }

    private val Photo.bitmap get() = try {
        contentResolver.openInputStream(contentUri)!!.use { stream ->
            decodeStream(stream)
        }.scale()?.rotatedBy(orientation)
    } catch (e: Exception) {
        log(e)
        null
    }

    private fun Bitmap.rotatedBy(orientation: String?): Bitmap = when (val degrees = orientation.degrees) {
        0f -> this
        else -> Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply { postRotate(degrees) }, true)
            .also { recycle() }
    }

    private val String?.degrees get() = this?.toFloatOrNull() ?: 0f

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
}
