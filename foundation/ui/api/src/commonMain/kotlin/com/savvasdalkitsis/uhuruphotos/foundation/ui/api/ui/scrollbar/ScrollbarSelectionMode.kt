/*
Code provided by https://blog.stackademic.com/jetpack-compose-multiplatform-scrollbar-scrolling-7c231a002ee1
 */
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scrollbar

/**
 * Enumerates the modes of selection available for a scrollbar. Defines how a scrollbar
 * can be interacted with or if it's interactable at all.
 */
enum class ScrollbarSelectionMode {
    /**
     * Allows interaction with the full scrollbar. Users can click or drag anywhere on the scrollbar
     * to scroll the content.
     */
    Full,

    /**
     * Limits interaction to the scrollbar's thumb only. Users can only click or drag the thumb
     * (the part of the scrollbar indicating the current scroll position) to scroll the content.
     */
    Thumb,

    /**
     * Disables interaction with the scrollbar. The scrollbar may still be visible for
     * informational purposes (indicating the current scroll position), but it cannot be interacted with.
     */
    Disabled
}