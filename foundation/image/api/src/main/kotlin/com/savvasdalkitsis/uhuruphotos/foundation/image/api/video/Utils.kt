package com.savvasdalkitsis.uhuruphotos.foundation.image.api.video

import java.io.Closeable
import java.io.File

internal fun Closeable?.closeSilent() {
    try {
        this?.close()
    } catch (e: Exception) {
        // Ignore
    }
}

internal fun File.tryDelete(): Boolean =
    tryDeleteFile(this)

private fun tryDeleteFile(file: File): Boolean {
    try {
        if (file.exists()) {
            return file.delete()
        }
    } catch (_: Exception) {
    }
    return false
}