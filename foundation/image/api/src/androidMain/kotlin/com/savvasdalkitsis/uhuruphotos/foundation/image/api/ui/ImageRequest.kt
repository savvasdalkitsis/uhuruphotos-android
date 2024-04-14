/*
Copyright 2022 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.foundation.image.api.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.request.CachePolicy
import coil.request.DefaultRequestOptions
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Precision

@Composable
fun String.toRequest(
    precision: Precision = Precision.INEXACT,
    onError: () -> Unit = {},
    onSuccess: (SuccessResult) -> Unit,
) = ImageRequest.Builder(LocalContext.current)
        .data(this)
        .diskCachePolicy(CachePolicy.ENABLED)
        .allowHardware(true)
        .crossfade(true)
        .defaults(
            DefaultRequestOptions(
                precision = precision,
            )
        )
        .listener(
            onSuccess = { _, result -> onSuccess(result) },
            onError = { _, _ -> onError() },
        )
        .build()