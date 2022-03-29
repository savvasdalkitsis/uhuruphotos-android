package com.savvasdalkitsis.librephotos.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun onMain(action: suspend () -> Unit) = CoroutineScope(Dispatchers.Main).launch {
    action()
}