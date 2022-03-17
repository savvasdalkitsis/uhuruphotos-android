package com.savvasdalkitsis.librephotos.auth.db.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Token(
    @PrimaryKey @NonNull @ColumnInfo(name = "token") val token: String,
)
