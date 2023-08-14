package com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.LibraryAction
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.seam.actions.LocalBucketSelected
import com.savvasdalkitsis.uhuruphotos.feature.library.view.implementation.ui.state.LibraryLocalMedia
import com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.SectionHeader

@Composable
internal fun LocalFolders(
    title: String,
    media: LibraryLocalMedia.Found,
    action: (LibraryAction) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SectionHeader(
            modifier = Modifier.padding(8.dp),
            title = title,
        )
        LazyRow(
            modifier = Modifier.heightIn(min = 120.dp)
        ) {
            for ((bucket, vitrineState) in media.buckets) {
                item(bucket.id) {
                    LibraryItem(
                        modifier = Modifier.animateItemPlacement(),
                        state = vitrineState,
                        photoGridModifier = Modifier.width(120.dp),
                        title = bucket.displayName,
                        iconFallback = -1,
                    ) {
                        action(LocalBucketSelected(bucket))
                    }
                }
            }
        }
    }
}