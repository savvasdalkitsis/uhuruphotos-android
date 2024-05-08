/*
Code provided by https://blog.stackademic.com/jetpack-compose-multiplatform-scrollbar-scrolling-7c231a002ee1
 */
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scrollbar

/**
 * Enumerates the conditions under which scrollbar actions (such as dragging) are enabled.
 */
enum class ScrollbarSelectionActionable {
    /**
     * Indicates that the scrollbar actions are always enabled, regardless of the scrollbar's visibility.
     * Users can interact with the scrollbar at any time.
     */
    Always,

    /**
     * Indicates that scrollbar actions are only enabled when the scrollbar is visible.
     * If the scrollbar is hidden due to lack of overflow or other reasons, it cannot be interacted with.
     */
    WhenVisible,
}