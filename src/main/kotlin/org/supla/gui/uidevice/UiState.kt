// Copyright (C) AC SOFTWARE SP. Z O.O.
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
package org.supla.gui.uidevice

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color
import pl.grzeslowski.jsupla.api.channel.state.*
import pl.grzeslowski.jsupla.api.channel.state.OnOffState.OnOff.ON
import java.util.concurrent.atomic.AtomicBoolean

sealed class UiState {
    private val updatingByApi = AtomicBoolean(false)

    private fun startUpdate() {
        updatingByApi.set(true)
    }

    private fun stopUpdate() {
        updatingByApi.set(false)
    }

    fun isUpdatingByApi() = updatingByApi.get()

    fun updateByApi(runnable: () -> Unit) {
        startUpdate()
        runnable()
        stopUpdate()
    }
}

class UiOnOffState(state: OnOffState) : UiState() {
    val on = SimpleBooleanProperty(state.onOffState == ON)
}

// temp & hum
class UiTemperatureAndHumidityState(state: TemperatureAndHumidityState) : UiState() {
    val temperature = SimpleObjectProperty(state.temperatureState)
    val humidity = SimpleObjectProperty(state.humidityState)
}

class UiTemperatureState(state: TemperatureState) : UiState() {
    val temperature = SimpleObjectProperty(state.temperatureState)
}

class UiHumidityState(state: HumidityState) : UiState() {
    val humidity = SimpleObjectProperty(state.humidityState)
}

// color
class UiColorState(state: ColorState) : UiState() {
    val hue = SimpleDoubleProperty(state.hsv.hue)
    val saturation = SimpleDoubleProperty(state.hsv.saturation * 100.0)
    val value = SimpleDoubleProperty(state.hsv.value * 100.0)
    val rgb = SimpleObjectProperty(Color.hsb(
            state.hsv.hue,
            state.hsv.saturation,
            state.hsv.value
    ))
}

class UiColorAndBrightnessState(state: ColorAndBrightnessState) : UiState() {
    val hue = SimpleDoubleProperty(state.hsv.hue)
    val saturation = SimpleDoubleProperty(state.hsv.saturation)
    val value = SimpleDoubleProperty(state.hsv.value)
    val brightness = SimpleDoubleProperty(state.brightness.percentage.toDouble())
    val rgb = SimpleObjectProperty(Color.hsb(
            state.hsv.hue,
            state.hsv.saturation,
            state.hsv.value
    ))
}

class UiDimmerState(state: BrightnessState) : UiState() {
    val brightness = SimpleDoubleProperty(state.brightness.percentage.toDouble())
}

// Rollershutter
class UiRollerShutterState(state: RollerShutterState) : UiState() {
    val open = SimpleDoubleProperty(state.open.percentage.toDouble())
    private val openListeners = ArrayList<() -> Unit>()
    private val closeListeners = ArrayList<() -> Unit>()

    fun listenOnOpen(listener: () -> Unit) {
        openListeners.add(listener)
    }

    fun listenOnClose(listener: () -> Unit) {
        closeListeners.add(listener)
    }

    fun fireOpen() {
        openListeners.forEach { it() }
    }

    fun fireClose() {
        closeListeners.forEach { it() }
    }
}

// Gate
class UiGateState(state: GateState) : UiState() {
    val gateState: SimpleObjectProperty<GateState.Position?> = SimpleObjectProperty(state.position)
    private val openCloseListeners = ArrayList<() -> Unit>()

    fun listenOnOpenClose(listener: () -> Unit) {
        openCloseListeners.add(listener)
    }

    fun fireOpenClose() {
        openCloseListeners.forEach { it() }
    }
}

object UndefinedState : UiState()

fun buildUiState(state: State): UiState =
        when (state) {
            is GateState -> UiGateState(state)
            is RollerShutterState -> UiRollerShutterState(state)
            is OnOffState -> UiOnOffState(state)
            is TemperatureAndHumidityState -> UiTemperatureAndHumidityState(state)
            is TemperatureState -> UiTemperatureState(state)
            is HumidityState -> UiHumidityState(state)
            is ColorAndBrightnessState -> UiColorAndBrightnessState(state)
            is ColorState -> UiColorState(state)
            is BrightnessState -> UiDimmerState(state)
            else -> UndefinedState
        }

fun updateState(uiState: UiState, state: State) {
    when (uiState) {
        is UiGateState -> uiState.gateState.value = (state as GateState).position
        is UiRollerShutterState -> uiState.open.value = (state as RollerShutterState).open.percentage.toDouble()
        is UiOnOffState ->
            uiState.on.value = (state as OnOffState).onOffState == ON
        is UiTemperatureAndHumidityState -> {
            uiState.temperature.value = (state as TemperatureState).temperatureState
            uiState.humidity.value = (state as HumidityState).humidityState
        }
        is UiTemperatureState ->
            uiState.temperature.value = (state as TemperatureState).temperatureState
        is UiHumidityState ->
            uiState.humidity.value = (state as HumidityState).humidityState
    }
}