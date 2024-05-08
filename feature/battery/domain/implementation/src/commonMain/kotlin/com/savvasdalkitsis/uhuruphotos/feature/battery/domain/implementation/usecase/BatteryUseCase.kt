package com.savvasdalkitsis.uhuruphotos.feature.battery.domain.implementation.usecase

import com.savvasdalkitsis.uhuruphotos.feature.battery.domain.api.model.BatteryOptimizationStatus
import com.savvasdalkitsis.uhuruphotos.feature.battery.domain.api.usecase.BatteryUseCase
import com.savvasdalkitsis.uhuruphotos.feature.battery.domain.implementation.repository.BatteryRepository
import com.savvasdalkitsis.uhuruphotos.foundation.system.api.usecase.BatteryOptimization
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
                BatteryOptimization.NotSupported -> {
                    emit(BatteryOptimizationStatus.BATTERY_NOT_OPTIMIZED)
                    stopChecking()
                }

                is BatteryOptimization.Supported -> {
                    if (!batteryRepository.shouldCheckBatteryOptimization()) {
                        emit(BatteryOptimizationStatus.NOT_CHECKED)
                        stopChecking()
                    } else {
                        if (optimization.isOptimized) {
                            emit(BatteryOptimizationStatus.BATTERY_OPTIMIZED)
                            delay(2000)
                        } else {
                            emit(BatteryOptimizationStatus.BATTERY_NOT_OPTIMIZED)
                            stopChecking()
                        }
                    }
                }
            }
        } while (currentCoroutineContext().isActive)
    }

    override fun doNotCheckBatteryOptimization() {
        batteryRepository.doNotCheckBatteryOptimization()
    }

    private suspend fun stopChecking() {
        currentCoroutineContext().cancel(null)
    }
}