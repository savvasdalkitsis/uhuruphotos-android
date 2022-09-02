package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.state

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class Title {
    @Composable
    abstract fun toText(): String

    data class Text(val title: String) : Title() {
        @Composable
        override fun toText(): String = title
    }

    data class Resource(@StringRes val title: Int) : Title() {
        @Composable
        override fun toText(): String = stringResource(title)
    }

}

fun String?.toTitleOr(@StringRes title: Int): Title = this?.let { Title.Text(it) } ?: Title.Resource(title)
