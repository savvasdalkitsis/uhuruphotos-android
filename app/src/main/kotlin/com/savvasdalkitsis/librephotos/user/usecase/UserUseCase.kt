package com.savvasdalkitsis.librephotos.user.usecase

import com.savvasdalkitsis.librephotos.user.User
import com.savvasdalkitsis.librephotos.user.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {

    fun getUser(): Flow<User> = userRepository.getUser()
        .onStart {
            CoroutineScope(Dispatchers.IO).launch {
                userRepository.refreshUser()
            }
        }

}