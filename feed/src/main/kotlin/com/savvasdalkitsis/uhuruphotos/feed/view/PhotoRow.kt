package com.savvasdalkitsis.uhuruphotos.feed.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.savvasdalkitsis.uhuruphotos.feed.view.PhotoRowSlot.EmptySlot
import com.savvasdalkitsis.uhuruphotos.feed.view.PhotoRowSlot.PhotoSlot
import com.savvasdalkitsis.uhuruphotos.photos.model.Photo

@Composable
fun PhotoRow(
    modifier: Modifier,
    onPhotoSelected: PhotoSelected,
    onPhotoLongPressed: (Photo) -> Unit,
    vararg slots: PhotoRowSlot
) {
    Row(modifier = modifier) {
        for (item in slots) {
            when (item) {
                is PhotoSlot -> PhotoThumbnail(
                    modifier = Modifier
                        .weight(item.photo.ratio),
                    photo = item.photo,
                    onPhotoSelected = onPhotoSelected,
                    onLongClick = onPhotoLongPressed,
                )
                EmptySlot -> Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}