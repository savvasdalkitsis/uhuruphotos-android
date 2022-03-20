package com.savvasdalkitsis.librephotos.auth.db.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [
    Index(value = ["type"], unique = true)
])
data class Token(
    @PrimaryKey @NonNull @ColumnInfo(name = "token") val token: String,
    @NonNull val type: TokenType,
)