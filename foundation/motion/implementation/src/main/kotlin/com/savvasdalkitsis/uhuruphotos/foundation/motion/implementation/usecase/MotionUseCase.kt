/*
Copyright 2024 Savvas Dalkitsis

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
package com.savvasdalkitsis.uhuruphotos.foundation.motion.implementation.usecase

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.xmp.XmpDirectory
import com.savvasdalkitsis.uhuruphotos.foundation.motion.api.model.MotionVideoOffset
import com.savvasdalkitsis.uhuruphotos.foundation.motion.api.usecase.MotionUseCase
import se.ansman.dagger.auto.AutoBind
import java.io.InputStream
import javax.inject.Inject

@AutoBind
class MotionUseCase @Inject constructor(
) : MotionUseCase {

    override fun getMotionVideoOffset(stream: InputStream): MotionVideoOffset? = ImageMetadataReader.readMetadata(stream)
        .getDirectoriesOfType(XmpDirectory::class.java)
        .find { it.xmpProperties.containsKey("GCamera:MotionPhoto") }
        ?.xmpProperties
        ?.run {
            offsetV1 ?: offsetV2
        }

    private val MutableMap<String, String>.offsetV1 get() = get("GCamera:MicroVideoOffset")
        ?.toLongOrNull()
        ?.let { MotionVideoOffset(it, fromStart = false) }

    private val MutableMap<String, String>.offsetV2 get() = calculateOffsetV2()
        ?.let { MotionVideoOffset(it, fromStart = true) }

    private fun MutableMap<String, String>.calculateOffsetV2(): Long? {
        var i = 1
        var offset = 0L
        while (true) {
            val mime = get(key(i, "Mime"))
            val length = get(key(i,"Length"))
            val padding = get(key(i, "Padding"))
            when {
                i == 1 && mime.isNullOrBlank() -> return null
                mime.isNullOrBlank() -> break
                mime.startsWith("video") -> {
                    offset += padding?.toLongOrNull() ?: 0
                    return offset
                }
                else -> offset += length?.toLongOrNull() ?: 0
            }
            i += 1
        }
        return null
    }

    private fun key(i: Int, name: String) = "Container:Directory[$i]/Container:Item/Item:$name"
}