package pl.grzeslowski.jsupla.gui.view.executor

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Slider
import org.slf4j.LoggerFactory
import pl.grzeslowski.jsupla.api.ApiException
import pl.grzeslowski.jsupla.api.channel.Channel
import pl.grzeslowski.jsupla.api.channel.action.ShutRevealAction
import pl.grzeslowski.jsupla.gui.api.ChannelApi
import javax.inject.Inject

class RollerShutterExecutor @Inject constructor(private val channelApi: ChannelApi) {
    private val logger = LoggerFactory.getLogger(OnOffExecutor::class.java)

    fun bind(channel: Channel, open: Button, close: Button, openPercentage: Slider) {
        val listener = RollerShutterListener(channel, openPercentage)
        open.onAction = EventHandler { listener.open(it) }
        close.onAction = EventHandler { listener.close(it) }
        openPercentage.valueProperty().addListener(listener)
    }

    private inner class RollerShutterListener(private val channel: Channel, private val openPercentage: Slider) : ChangeListener<Number> {
        fun open(event: ActionEvent) {
            logger.trace("Open event {}", event)
            execute(ShutRevealAction.reveal())
        }

        fun close(event: ActionEvent) {
            logger.trace("Close event {}", event)
            execute(ShutRevealAction.shut())
        }

        override fun changed(observable: ObservableValue<out Number>?, oldValue: Number?, newValue: Number) {
            logger.trace("Percentage changed to {}/{}", newValue, newValue.toInt())
            execute(ShutRevealAction.reveal(newValue.toInt()))
        }

        private fun execute(action: ShutRevealAction) {
            logger.debug("Changing channel#${channel.id} to $action")
            try {
                channelApi.executeAction(channel, action)
                openPercentage.valueProperty().removeListener(this)
                openPercentage.value = action.reveal.toDouble()
                openPercentage.valueProperty().addListener(this)
            } catch (ex: ApiException) {
                logger.error("Error while executing action on channel#${channel.id}", ex)
            }
        }
    }
}