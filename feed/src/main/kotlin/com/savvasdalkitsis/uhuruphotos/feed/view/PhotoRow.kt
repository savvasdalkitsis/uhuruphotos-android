package com.savvasdalkitsis.uhuruphotos.feed.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.savvasdalkitsis.uhuruphotos.feed.view.PhotoRowSlot.EmptySlot
import com.savvasdalkitsis.uhuruphotos.feed.view.PhotoRowSlot.PhotoSlot
import com.savvasdalkitsis.uhuruphotos.photos.model.Photo

@Composable
fun PhotoRow(
    modifier: Modifier = Modifier,
    maintainAspectRatio: Boolean = true,
    onPhotoSelected: PhotoSelected,
    onPhotoLongPressed: (Photo) -> Unit,
    vararg slots: PhotoRowSlot
) {
    Row(modifier = modifier) {
        for (item in slots) {
            when (item) {
                is PhotoSlot -> {
                    val aspectRatio = when {
                        maintainAspectRatio -> item.photo.ratio
                        else -> 1f
                    }
                    PhotoThumbnail(
                        modifier = Modifier
                            .weight(aspectRatio),
                        photo = item.photo,
                        aspectRatio = aspectRatio,
                        contentScale = when {
                            maintainAspectRatio -> ContentScale.FillBounds
                            else ->ContentScale.Crop
                        },
                        onPhotoSelected = onPhotoSelected,
                        onLongClick = onPhotoLongPressed,
                    )
                }
                EmptySlot -> Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}