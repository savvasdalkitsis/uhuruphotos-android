/*
Copyright 2024 Savvas Dalkitsis

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.savvasdalkitsis.uhuruphotos.feature.battery.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.battery.domain.api.model.BatteryOptimizationStatus
import com.savvasdalkitsis.uhuruphotos.feature.battery.domain.api.model.BatteryOptimizationStatus.BATTERY_NOT_OPTIMIZED
import com.savvasdalkitsis.uhuruphotos.feature.battery.domain.api.model.BatteryOptimizationStatus.BATTERY_OPTIMIZED
import com.savvasdalkitsis.uhuruphotos.feature.battery.domain.api.model.BatteryOptimizationStatus.NOT_CHECKED
import com.savvasdalkitsis.uhuruphotos.feature.battery.domain.api.usecase.BatteryUseCase
import com.savvasdalkitsis.uhuruphotos.feature.battery.domain.implementation.repository.BatteryRepository
import com.savvasdalkitsis.uhuruphotos.foundation.system.api.usecase.BatteryOptimization.NotSupported
import com.savvasdalkitsis.uhuruphotos.foundation.system.api.usecase.BatteryOptimization.Supported
import com.savvasdalkitsis.uhuruphotos.foundation.system.api.usecase.SystemUseCase
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

class BatteryUseCase(
    private val systemUseCase: SystemUseCase,
    private val batteryRepository: BatteryRepository,
) : BatteryUseCase {

    override fun observerBatteryOptimizationStatus(): Flow<BatteryOptimizationStatus> = flow {
        do {
            when (val optimization = systemUseCase.isIgnoringBatteryOptimizations()) {
                NotSupported -> {
                    emit(BATTERY_NOT_OPTIMIZED)
                    stopChecking()
                }
                is Supported -> {
                    if (!batteryRepository.shouldCheckBatteryOptimization()) {
                        emit(NOT_CHECKED)
                        stopChecking()
                    } else {
                        if (optimization.isOptimized) {
                            emit(BATTERY_OPTIMIZED)
                            delay(2000)
                        } else {
                            emit(BATTERY_NOT_OPTIMIZED)
                            stopChecking()
                        }
                    }
                }
            }
        } while(currentCoroutineContext().isActive)
    }

    override fun doNotCheckBatteryOptimization() {
        batteryRepository.doNotCheckBatteryOptimization()
    }

    private suspend fun stopChecking() {
        currentCoroutineContext().cancel(null)
    }
}
