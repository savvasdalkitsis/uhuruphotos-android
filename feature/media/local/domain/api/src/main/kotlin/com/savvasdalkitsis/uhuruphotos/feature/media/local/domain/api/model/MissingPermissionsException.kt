package com.savvasdalkitsis.uhuruphotos.feature.media.local.domain.api.model

class MissingPermissionsException(
    val deniedPermissions: List<String>,
) : Exception("Missing permissions: $deniedPermissions")