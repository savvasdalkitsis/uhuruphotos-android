package com.savvasdalkitsis.librephotos.auth.db.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthDao {

    @Query("SELECT * from Token")
    fun getToken(): Flow<String?>
}