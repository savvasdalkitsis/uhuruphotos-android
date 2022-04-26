package com.savvasdalkitsis.uhuruphotos.user.repository

import com.savvasdalkitsis.uhuruphotos.db.extensions.crud
import com.savvasdalkitsis.uhuruphotos.db.user.User
import com.savvasdalkitsis.uhuruphotos.db.user.UserQueries
import com.savvasdalkitsis.uhuruphotos.log.log
import com.savvasdalkitsis.uhuruphotos.user.service.UserService
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService,
    private val userQueries: UserQueries,
) {

    fun getUser(): Flow<User> = userQueries.getUser().asFlow().mapToOneNotNull()

    suspend fun refreshUser() {
        try {
            val userResults = userService.getUser()
            for (userResult in userResults.results) {
                crud { userQueries.addUser(userResult.toUser()) }
            }
        } catch (e: IOException) {
            log(e)
        }
    }
}