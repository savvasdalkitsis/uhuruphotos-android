package com.savvasdalkitsis.uhuruphotos.user.repository

import com.savvasdalkitsis.uhuruphotos.db.extensions.crud
import com.savvasdalkitsis.uhuruphotos.db.user.User
import com.savvasdalkitsis.uhuruphotos.db.user.UserQueries
import com.savvasdalkitsis.uhuruphotos.user.api.UserApi
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