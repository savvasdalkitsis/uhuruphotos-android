package com.savvasdalkitsis.uhuruphotos.user.usecase

import com.savvasdalkitsis.uhuruphotos.db.user.User
import com.savvasdalkitsis.uhuruphotos.infrastructure.extensions.safelyOnStartIgnoring
import com.savvasdalkitsis.uhuruphotos.user.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {

    fun getUser(): Flow<User> = userRepository.getUser()
        .safelyOnStartIgnoring {
            userRepository.refreshUser()
        }

}