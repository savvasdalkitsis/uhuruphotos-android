package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model

import android.content.Context
import java.io.File

fun localMediaThumbnailFile(context: Context, id: Long) = context.filesDir.subFolder("localThumbnails").subFile("$id.jpg")

private fun File.subFolder(name: String) = subFile(name).apply {
    mkdirs()
}

private fun File.subFile(name: String) = File(this, name)
