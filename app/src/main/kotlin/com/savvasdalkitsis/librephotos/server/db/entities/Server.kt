package com.savvasdalkitsis.librephotos.server.db.entities

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Server(
    @PrimaryKey @NonNull val url: String,
)