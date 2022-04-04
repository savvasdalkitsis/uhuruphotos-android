package com.savvasdalkitsis.librephotos.user.repository

import com.savvasdalkitsis.librephotos.extensions.crud
import com.savvasdalkitsis.librephotos.user.User
import com.savvasdalkitsis.librephotos.user.UserQueries
import com.savvasdalkitsis.librephotos.user.api.UserApi
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApi: UserApi,
    private val userQueries: UserQueries,
) {

    fun getUser(): Flow<User> = userQueries.getUser().asFlow().mapToOneNotNull()

    suspend fun refreshUser() {
        val userResults = userApi.getUser()
        for (userResult in userResults.results) {
            crud { userQueries.addUser(userResult.toUser()) }
        }
    }

    suspend fun removeUser() {
        crud { userQueries.deleteUser() }
    }
}