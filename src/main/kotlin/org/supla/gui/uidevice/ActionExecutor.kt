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

import org.slf4j.LoggerFactory
import org.supla.gui.api.ChannelApi
import pl.grzeslowski.jsupla.api.channel.action.ShutRevealAction
import pl.grzeslowski.jsupla.api.channel.action.ToggleAction.OPEN_CLOSE
import javax.inject.Inject
import javax.inject.Provider

class ActionExecutor @Inject constructor(private val channelApiProvider: Provider<ChannelApi>) {
    private val logger = LoggerFactory.getLogger(ActionExecutor::class.java)

    fun listenOnStates(states: Map<UiChannel, UiState>) {
        states.entries
                .stream()
                .map { listenOnState(it.key, it.value) }
                .forEach { it() }
    }

    private fun listenOnState(channel: UiChannel, state: UiState): () -> Unit =
            when (state) {
                is UiGateState -> listenOnState(channel, state)
                is UiOnOffState -> listenOnState(channel, state)
                is UiColorState -> listenOnState(channel, state)
                is UiColorAndBrightnessState -> listenOnState(channel, state)
                is UiDimmerState -> listenOnState(channel, state)
                is UiRollerShutterState -> listenOnState(channel, state)
                UndefinedState,
                is UiTemperatureAndHumidityState,
                is UiTemperatureState,
                is UiHumidityState -> ({})
            }

    private fun listenOnState(channel: UiChannel, state: UiGateState): () -> Unit = {
        val channelApi = channelApiProvider.get()
        state.listenOnOpenClose {
            state.gateState.value = null
            channelApi.executeAction(channel.nativeChannel, OPEN_CLOSE)
        }
    }

    private fun listenOnState(channel: UiChannel, state: UiOnOffState): () -> Unit = {
        state.on.addListener(OnOffChangeListener(channelApiProvider.get(), state, channel))
    }

    private fun listenOnState(channel: UiChannel, state: UiDimmerState): () -> Unit = {
        state.brightness.addListener(BrightnessStateChangeListener(
                channelApiProvider.get(),
                channel,
                state.brightness))
    }

    private fun listenOnState(channel: UiChannel, state: UiColorState): () -> Unit = {
        val channelApi = channelApiProvider.get()
        state.hue.addListener(HueStateChangeListener(
                channelApi,
                channel,
                state.hue,
                state.saturation,
                state.value,
                state.rgb))
        state.saturation.addListener(SaturationStateChangeListener(
                channelApi,
                channel,
                state.hue,
                state.saturation,
                state.value,
                state.rgb))
        state.value.addListener(ValueStateChangeListener(
                channelApi,
                channel,
                state.hue,
                state.saturation,
                state.value,
                state.rgb))
        state.rgb.addListener(ColorStateChangeListener(
                channelApi,
                channel,
                state.hue,
                state.saturation,
                state.value,
                state.rgb))
    }

    private fun listenOnState(channel: UiChannel, state: UiColorAndBrightnessState): () -> Unit = {
        val channelApi = channelApiProvider.get()
        state.hue.addListener(HueStateChangeListener(
                channelApi,
                channel,
                state.hue,
                state.saturation,
                state.value,
                state.brightness,
                state.rgb))
        state.saturation.addListener(SaturationStateChangeListener(
                channelApi,
                channel,
                state.hue,
                state.saturation,
                state.value,
                state.brightness,
                state.rgb))
        state.value.addListener(ValueStateChangeListener(
                channelApi,
                channel,
                state.hue,
                state.saturation,
                state.value,
                state.brightness,
                state.rgb))
        state.brightness.addListener(ColorAndBrightnessStateChangeListener(
                channelApi,
                channel,
                state.hue,
                state.saturation,
                state.value,
                state.brightness))
        state.rgb.addListener(ColorStateChangeListener(
                channelApi,
                channel,
                state.hue,
                state.saturation,
                state.value,
                state.rgb,
                state.brightness))
    }

    private fun listenOnState(channel: UiChannel, state: UiRollerShutterState): () -> Unit = {
        val channelApi = channelApiProvider.get()
        state.open.addListener(RollerShutterOpenListener(channelApi, state, channel))
        state.listenOnOpen(buildListenOnOpenClose(true, channelApi, channel, state))
        state.listenOnClose(buildListenOnOpenClose(false, channelApi, channel, state))
    }

    private fun buildListenOnOpenClose(open: Boolean, channelApi: ChannelApi, channel: UiChannel, state: UiRollerShutterState): () -> Unit = {
        val (action, newValue) =
                if (open) {
                    Pair(ShutRevealAction.reveal(), 100.0)
                } else {
                    Pair(ShutRevealAction.shut(), 0.0)
                }
        channelApi.executeAction(channel.nativeChannel, action)
        state.updateByApi {
            state.open.value = newValue
        }
    }
}
