package com.savvasdalkitsis.uhuruphotos.log

import android.util.Log
import timber.log.Timber


inline fun log(tag: String = "", msg: () -> String) {
    if (BuildConfig.DEBUG) {
        if (tag.isNotEmpty()) {
            Timber.tag(tag).log(Log.VERBOSE, msg())
        } else {
            Timber.log(Log.VERBOSE, msg())
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun log(t: Throwable) {
    if (BuildConfig.DEBUG) {
        Timber.log(Log.WARN, t)
    }
}