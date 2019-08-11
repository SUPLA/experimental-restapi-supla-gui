package pl.grzeslowski.jsupla.gui.uidevice

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import org.slf4j.LoggerFactory
import pl.grzeslowski.jsupla.api.channel.action.Action
import pl.grzeslowski.jsupla.api.channel.action.ShutRevealAction
import pl.grzeslowski.jsupla.api.channel.action.TurnOnOffAction
import pl.grzeslowski.jsupla.gui.api.ChannelApi

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