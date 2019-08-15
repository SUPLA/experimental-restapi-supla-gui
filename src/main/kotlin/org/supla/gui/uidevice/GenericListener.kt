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

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import org.slf4j.LoggerFactory
import org.supla.gui.api.ChannelApi
import pl.grzeslowski.jsupla.api.channel.action.Action
import pl.grzeslowski.jsupla.api.channel.action.ShutRevealAction
import pl.grzeslowski.jsupla.api.channel.action.TurnOnOffAction

abstract class GenericListener<T>(
        private val channelApi: ChannelApi,
        private val channel: UiChannel) : ChangeListener<T> {
    private val logger = LoggerFactory.getLogger(GenericListener::class.java)

    override fun changed(observable: ObservableValue<out T>?, oldValue: T, newValue: T) {
        val state = channel.state
        when {
            state.isUpdatingByApi() -> logger.trace("Will not update `$state` because it's updated by API")
            oldValue == newValue -> logger.trace("Old value is equal to new value $oldValue == $newValue")
            else -> {
                val action = buildAction(newValue)
                logger.debug("Changing channel#${channel.id} to $action")
                try {
                    channelApi.executeAction(channel.nativeChannel, action)
                    state.updateByApi {
                        afterUpdate()
                    }
                } catch (ex: Exception) {
                    logger.error("Error while executing action on channel#${channel.id}", ex)
                    state.updateByApi {
                        logger.trace("Update was not successful! Rolling back to `$oldValue` for `$state`")
                        rollback(oldValue)
                    }
                }
            }
        }
    }

    protected abstract fun buildAction(newValue: T): Action

    protected abstract fun rollback(oldValue: T)

    protected open fun afterUpdate() {
    }
}

class OnOffChangeListener(
        channelApi: ChannelApi,
        private val state: UiOnOffState,
        channel: UiChannel) : GenericListener<Boolean>(channelApi, channel) {
    override fun buildAction(newValue: Boolean): Action = if (newValue) {
        TurnOnOffAction.ON
    } else {
        TurnOnOffAction.OFF
    }

    override fun rollback(oldValue: Boolean) {
        state.on.value = oldValue
    }
}

class RollerShutterOpenListener(
        channelApi: ChannelApi,
        private val state: UiRollerShutterState,
        channel: UiChannel) : GenericListener<Number>(channelApi, channel) {
    override fun buildAction(newValue: Number): Action = ShutRevealAction.reveal(newValue.toInt())
    override fun rollback(oldValue: Number) {
        state.open.value = oldValue.toDouble()
    }
}