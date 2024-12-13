package com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes

import android.os.Build
import androidx.compose.runtime.Immutable
import com.savvasdalkitsis.uhuruphotos.foundation.theme.api.themes.ThemeContrast.NORMAL

@Immutable
enum class ThemeVariant(
    delegate: Theme,
    val supportedContrasts: List<ThemeContrast> = ThemeContrast.entries
) : Theme by delegate {
    DYNAMIC(DynamicTheme, listOf(NORMAL)),
    BLUE(BlueTheme),
    YELLOW(YellowTheme),
    GREEN(GreenTheme),
    RED(RedTheme),
    ;

    companion object {
        val default = validEntries().first()

        fun validEntries() = entries.filter {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S || it != DYNAMIC
        }
    }
}