package com.savvasdalkitsis.librephotos.auth.db.entities

import androidx.room.TypeConverter

class TokenTypeConverter {

    @TypeConverter
    fun toTokenType(type: String) = enumValueOf<TokenType>(type)

    @TypeConverter
    fun fromTokenType(type: TokenType) = type.name
}