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
package com.savvasdalkitsis.uhuruphotos.user.service.model

import com.savvasdalkitsis.uhuruphotos.db.user.User
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserResult(
    val id: Int,
    val username: String,
    val email: String,
    @Json(name = "scan_directory") val scanDirectory: String,
    val confidence: Float,
    @Json(name = "transcode_videos") val transcodeVideos: Boolean,
    @Json(name = "semantic_search_topk") val semanticSearchTopK: Int,
    @Json(name = "first_name") val firstName: String?,
    @Json(name = "last_name") val lastName: String?,
    @Json(name = "public_photo_count") val publicPhotoCount: Int,
    @Json(name = "date_joined") val dateJoined: String?,
    @Json(name = "avatar") val avatar: String?,
    @Json(name = "photo_count") val photoCount: Int,
    @Json(name = "nextcloud_server_address") val nextcloudServerAddress: String?,
    @Json(name = "nextcloud_username") val nextcloudUsername: String?,
    @Json(name = "nextcloud_scan_directory") val nextcloudScanDirectory: String?,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "favorite_min_rating") val favoriteMinRating: Int,
    @Json(name = "image_scale") val imageScale: Int,
    @Json(name = "save_metadata_to_disk") val saveMetadataToDisk: String,
    @Json(name = "datetime_rules") val datetimeRules: String,
    @Json(name = "default_timezone") val defaultTimezone: String,
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