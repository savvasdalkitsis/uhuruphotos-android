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

import app.cash.turbine.test
import com.savvasdalkitsis.uhuruphotos.feature.battery.domain.api.model.BatteryOptimizationStatus.BATTERY_NOT_OPTIMIZED
import com.savvasdalkitsis.uhuruphotos.feature.battery.domain.api.model.BatteryOptimizationStatus.BATTERY_OPTIMIZED
import com.savvasdalkitsis.uhuruphotos.feature.battery.domain.api.model.BatteryOptimizationStatus.NOT_CHECKED
import com.savvasdalkitsis.uhuruphotos.feature.battery.domain.implementation.repository.BatteryRepository
import com.savvasdalkitsis.uhuruphotos.foundation.system.api.usecase.BatteryOptimization.NotSupported
import com.savvasdalkitsis.uhuruphotos.foundation.system.api.usecase.BatteryOptimization.Supported
import com.savvasdalkitsis.uhuruphotos.foundation.system.api.usecase.SystemUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Test
import com.savvasdalkitsis.uhuruphotos.feature.battery.domain.implementation.usecase.BatteryUseCase

class BatteryUseCaseTest {

    private val systemUseCase = mockk<SystemUseCase>()
    private val batteryRepository = mockk<BatteryRepository>()
    private val underTest = BatteryUseCase(systemUseCase, batteryRepository)

    @Test
    fun `emits not optimised state and terminates when battery optimisation is not supported`() = runTest {
        systemUseCase.doesNotSupportBatteryOptimization()

        underTest.observerBatteryOptimizationStatus().test {
            assert(awaitItem() == BATTERY_NOT_OPTIMIZED)
            awaitComplete()
        }
    }

    @Test
    fun `emits not checked state and terminates when battery optimisation is supported but is instructed not to check`() = runTest {
        systemUseCase.supportsBatteryOptimization(isOptimised = false)
        shouldCheckBatteryOptimization(false)

        underTest.observerBatteryOptimizationStatus().test {
            assert(awaitItem() == NOT_CHECKED)
            awaitComplete()
        }
    }

    @Test
    fun `emits not optimised state and terminates when battery optimisation is not supported even if is instructed not to check`() = runTest {
        systemUseCase.doesNotSupportBatteryOptimization()
        shouldCheckBatteryOptimization(false)

        underTest.observerBatteryOptimizationStatus().test {
            assert(awaitItem() == BATTERY_NOT_OPTIMIZED)
            awaitComplete()
        }
    }

    @Test
    fun `emits current optimised state every 2 seconds until not optimised after which it terminates so we don't keep checking forever`() = runTest {
        systemUseCase.supportsBatteryOptimization(isOptimised = true)
        shouldCheckBatteryOptimization(true)

        underTest.observerBatteryOptimizationStatus().test {
            assert(awaitItem() == BATTERY_OPTIMIZED)
            advanceTimeBy(2001)
            assert(awaitItem() == BATTERY_OPTIMIZED)
            systemUseCase.supportsBatteryOptimization(isOptimised = false)
            advanceTimeBy(2001)
            assert(awaitItem() == BATTERY_NOT_OPTIMIZED)
            awaitComplete()
        }
    }

    @Test
    fun `stops checking for optimisation state when asked to`() = runTest {
        systemUseCase.supportsBatteryOptimization(isOptimised = true)
        shouldCheckBatteryOptimization(true)

        underTest.observerBatteryOptimizationStatus().test {
            awaitEvent()
            advanceTimeBy(2001)
            awaitEvent()
            shouldCheckBatteryOptimization(false)
            advanceTimeBy(2001)
            assert(awaitItem() == NOT_CHECKED)
            awaitComplete()
        }
    }

    private fun shouldCheckBatteryOptimization(shouldCheck: Boolean) {
        every { batteryRepository.shouldCheckBatteryOptimization() } returns shouldCheck
    }

    private fun SystemUseCase.doesNotSupportBatteryOptimization() {
        every { isIgnoringBatteryOptimizations() } returns NotSupported
    }

    private fun SystemUseCase.supportsBatteryOptimization(isOptimised: Boolean) {
        every { isIgnoringBatteryOptimizations() } returns Supported(isOptimised)
    }
}
