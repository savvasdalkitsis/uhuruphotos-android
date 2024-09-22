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
package com.savvasdalkitsis.uhuruphotos.feature.user.domain.implementation.service.model

import com.savvasdalkitsis.uhuruphotos.feature.db.domain.api.user.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class UserResult(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "username") val username: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "scan_directory") val scanDirectory: String,
    @field:Json(name = "confidence") val confidence: Float,
    @field:Json(name = "transcode_videos") val transcodeVideos: Boolean,
    @field:Json(name = "semantic_search_topk") val semanticSearchTopK: Int,
    @field:Json(name = "first_name") val firstName: String?,
    @field:Json(name = "last_name") val lastName: String?,
    @field:Json(name = "public_photo_count") val publicPhotoCount: Int,
    @field:Json(name = "date_joined") val dateJoined: String?,
    @field:Json(name = "avatar") val avatar: String?,
    @field:Json(name = "photo_count") val photoCount: Int,
    @field:Json(name = "nextcloud_server_address") val nextcloudServerAddress: String?,
    @field:Json(name = "nextcloud_username") val nextcloudUsername: String?,
    @field:Json(name = "nextcloud_scan_directory") val nextcloudScanDirectory: String?,
    @field:Json(name = "avatar_url") val avatarUrl: String?,
    @field:Json(name = "favorite_min_rating") val favoriteMinRating: Int,
    @field:Json(name = "image_scale") val imageScale: Int,
    @field:Json(name = "save_metadata_to_disk") val saveMetadataToDisk: String,
    @field:Json(name = "datetime_rules") val datetimeRules: String,
    @field:Json(name = "default_timezone") val defaultTimezone: String,
) {
    fun toUser() = User(
        id = id,
        username = username,
        email = email,
        avatar = avatarUrl,
        firstName = firstName,
        lastName = lastName,
        favoriteMinRating = favoriteMinRating,
        scanDirectory = scanDirectory,
        confidence = confidence,
        transcodeVideos = transcodeVideos,
        semanticSearchTopK = semanticSearchTopK,
        publicPhotoCount = publicPhotoCount,
        dateJoined = dateJoined,
        photoCount = photoCount,
        nextcloudServerAddress = nextcloudServerAddress,
        nextcloudUsername = nextcloudUsername,
        nextcloudScanDirectory = nextcloudScanDirectory,
        avatarUrl = avatarUrl,
        imageScale = imageScale,
        saveMetadataToDisk = saveMetadataToDisk,
        datetimeRules = datetimeRules,
        defaultTimezone = defaultTimezone,
    )
}