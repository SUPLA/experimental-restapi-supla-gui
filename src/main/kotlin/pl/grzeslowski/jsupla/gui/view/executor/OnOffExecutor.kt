package pl.grzeslowski.jsupla.gui.view.executor

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.Toggle
import org.slf4j.LoggerFactory
import pl.grzeslowski.jsupla.api.ApiException
import pl.grzeslowski.jsupla.api.channel.Channel
import pl.grzeslowski.jsupla.api.channel.action.TurnOnOffAction
import pl.grzeslowski.jsupla.gui.api.ChannelApi
import javax.inject.Inject

class OnOffExecutor @Inject constructor(private val channelApi: ChannelApi) {
    private val logger = LoggerFactory.getLogger(OnOffExecutor::class.java)
    fun bind(channel: Channel, toggle: Toggle) {
        toggle.selectedProperty().addListener(OnOffListener(channel))
    }

    private inner class OnOffListener(private val channel: Channel) : ChangeListener<Boolean> {
        override fun changed(observable: ObservableValue<out Boolean>?, oldValue: Boolean?, newValue: Boolean) {
            val action = if (newValue) {
                TurnOnOffAction.ON
            } else {
                TurnOnOffAction.OFF
            }
            logger.debug("Changing channel#${channel.id} to $action")
            try {
                channelApi.executeAction(channel, action)
            } catch (ex: ApiException) {
                logger.error("Error while executing action on channel#${channel.id}", ex)
            }
        }
    }
}