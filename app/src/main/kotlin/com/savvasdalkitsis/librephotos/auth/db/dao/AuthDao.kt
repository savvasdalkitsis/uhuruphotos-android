package com.savvasdalkitsis.librephotos.auth.db.dao

import androidx.room.*
import com.savvasdalkitsis.librephotos.auth.db.entities.Token
import com.savvasdalkitsis.librephotos.auth.db.entities.TokenTypeConverter
import javax.inject.Inject

@Dao
@TypeConverters(TokenTypeConverter::class)
interface AuthDao {

    @Query("SELECT token from Token where type == 'ACCESS' limit 1")
    suspend fun getAccessToken(): String?

    @Query("SELECT token from Token where type == 'REFRESH' limit 1")
    suspend fun getRefreshToken(): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveToken(token: Token)
}