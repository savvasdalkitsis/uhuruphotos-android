/*
Copyright 2026 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.implementation.usecase

import android.app.PendingIntent
import android.content.ClipData
import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Bundle
import android.provider.BaseColumns

class FakeMediaStoreProvider : ContentProvider() {

    val existingIds = mutableSetOf<Long>()
    val rejectedIds = mutableSetOf<Long>()
    val deleteRequests = mutableListOf<List<Uri>>()

    override fun onCreate() = true

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle {
        val uris = extras.uris()
        uris.firstOrNull { ContentUris.parseId(it) !in existingIds - rejectedIds }?.let {
            throw IllegalArgumentException("Invalid Uri: $it")
        }
        deleteRequests += uris
        return Bundle().apply {
            putParcelable("result", PendingIntent.getActivity(context, 0, Intent(), PendingIntent.FLAG_IMMUTABLE))
        }
    }

    @Suppress("DEPRECATION")
    private fun Bundle?.uris(): List<Uri> = this?.getParcelable<ClipData>("clip_data")?.let { clip ->
        (0 until clip.itemCount).map { clip.getItemAt(it).uri }
    }.orEmpty()

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor = MatrixCursor(arrayOf(BaseColumns._ID)).apply {
        ContentUris.parseId(uri).takeIf { it in existingIds }?.let { addRow(arrayOf(it)) }
    }

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?) = 0
}
