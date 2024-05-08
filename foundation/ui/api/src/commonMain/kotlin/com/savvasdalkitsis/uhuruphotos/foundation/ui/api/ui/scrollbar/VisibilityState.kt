/*
Code provided by https://blog.stackademic.com/jetpack-compose-multiplatform-scrollbar-scrolling-7c231a002ee1
 */
package com.savvasdalkitsis.uhuruphotos.foundation.ui.api.ui.scrollbar

/**
 * Enumerates the possible visibility states of an element within the user interface.
 */
enum class VisibilityState {
    /** The element is fully visible to the user.*/
    COMPLETELY_VISIBLE,
    /** Only a portion of the element is visible to the user.*/
    PARTIALLY_VISIBLE,
    /** The element is not visible to the user at all.*/
    NOT_VISIBLE
}