package com.savvasdalkitsis.librephotos.user.usecase

import com.savvasdalkitsis.librephotos.extensions.crud
import com.savvasdalkitsis.librephotos.user.User
import com.savvasdalkitsis.librephotos.user.UserQueries
import com.savvasdalkitsis.librephotos.user.api.UserApi
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val userApi: UserApi,
    private val userQueries: UserQueries,
) {

    fun getUser(): Flow<User> = userQueries.getUser().asFlow().mapToOneNotNull()
        .onStart {
            CoroutineScope(Dispatchers.IO).launch {
                val userResults = userApi.getUser()
                for (userResult in userResults.results) {
                    crud { userQueries.addUser(userResult.toUser()) }
                }
            }
        }

}