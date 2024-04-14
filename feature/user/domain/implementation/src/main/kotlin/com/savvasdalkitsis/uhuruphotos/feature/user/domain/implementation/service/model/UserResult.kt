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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserResult(
    val id: Int,
    val username: String,
    val email: String,
    @SerialName("scan_directory") val scanDirectory: String,
    val confidence: Float,
    @SerialName("transcode_videos") val transcodeVideos: Boolean,
    @SerialName("semantic_search_topk") val semanticSearchTopK: Int,
    @SerialName("first_name") val firstName: String?,
    @SerialName("last_name") val lastName: String?,
    @SerialName("public_photo_count") val publicPhotoCount: Int,
    @SerialName("date_joined") val dateJoined: String?,
    @SerialName("avatar") val avatar: String?,
    @SerialName("photo_count") val photoCount: Int,
    @SerialName("nextcloud_server_address") val nextcloudServerAddress: String?,
    @SerialName("nextcloud_username") val nextcloudUsername: String?,
    @SerialName("nextcloud_scan_directory") val nextcloudScanDirectory: String?,
    @SerialName("avatar_url") val avatarUrl: String?,
    @SerialName("favorite_min_rating") val favoriteMinRating: Int,
    @SerialName("image_scale") val imageScale: Int,
    @SerialName("save_metadata_to_disk") val saveMetadataToDisk: String,
    @SerialName("datetime_rules") val datetimeRules: String,
    @SerialName("default_timezone") val defaultTimezone: String,
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