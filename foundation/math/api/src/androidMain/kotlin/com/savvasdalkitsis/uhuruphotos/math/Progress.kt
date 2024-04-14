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
package com.savvasdalkitsis.uhuruphotos.math

fun Long.toProgressPercent100(total: Long): Int =
    (100 * toProgressPercent(total)).toInt()

fun Long.toProgressPercent(total: Long): Float = when (this) {
    total -> 0f
    else -> (this + 1) / total.toFloat()
}