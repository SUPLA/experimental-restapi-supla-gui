package pl.grzeslowski.jsupla.gui.view.executor

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.Slider
import org.slf4j.LoggerFactory
import pl.grzeslowski.jsupla.api.ApiException
import pl.grzeslowski.jsupla.api.Color
import pl.grzeslowski.jsupla.api.channel.Channel
import pl.grzeslowski.jsupla.api.channel.action.Action
import pl.grzeslowski.jsupla.api.channel.action.SetBrightnessAction
import pl.grzeslowski.jsupla.api.channel.action.SetBrightnessAndColor
import pl.grzeslowski.jsupla.api.channel.action.SetColorAction
import pl.grzeslowski.jsupla.gui.api.ChannelApi
import javax.inject.Inject

class ColorExecutor @Inject constructor(private val channelApi: ChannelApi) {
    private val logger = LoggerFactory.getLogger(OnOffExecutor::class.java)
    fun bind(channel: Channel,
             hue: Slider?,
             saturation: Slider?,
             value: Slider?,
             dimmer: Slider?) {
        val listener = ColorListener(channel, hue, saturation, value, dimmer)
        var present = false
        if (hue != null && saturation != null && value != null) {
            hue.valueProperty().addListener(listener)
            saturation.valueProperty().addListener(listener)
            value.valueProperty().addListener(listener)
            present = true
        }
        if (dimmer != null) {
            dimmer.valueProperty().addListener(listener)
            present = true
        }
        if (present.not()) {
            throw IllegalStateException("Everything cannot be null!")
        }
    }

    private inner class ColorListener(private val channel: Channel,
                                      private val hue: Slider?,
                                      private val saturation: Slider?,
                                      private val value: Slider?,
                                      private val dimmer: Slider?) : ChangeListener<Number> {
        override fun changed(observable: ObservableValue<out Number>?, oldValue: Number?, newValue: Number?) {
            val action = buildAction()
            logger.debug("Changing channel#${channel.id} to $action")
            try {
                channelApi.executeAction(channel, action)
            } catch (ex: ApiException) {
                logger.error("Error while executing action on channel#${channel.id}", ex)
            }
        }

        @Suppress("unused") // maybe for future
        private fun isChanging(): Boolean =
                hue?.isValueChanging ?: false
                        || saturation?.isValueChanging ?: false
                        || value?.isValueChanging ?: false
                        || dimmer?.isValueChanging ?: false

        private fun buildAction(): Action {
            return if (hue != null && saturation != null && value != null) {
                val hsv = Color.Hsv(hue.value, saturation.value / 100.0, value.value / 100.0)
                if (dimmer != null) {
                    SetBrightnessAndColor(dimmer.value.toInt(), hsv)
                } else {
                    SetColorAction.setHsv(hsv)
                }
            } else if (dimmer != null) {
                SetBrightnessAction(dimmer.value.toInt())
            } else {
                throw IllegalStateException("Everything cannot be null!")
            }
        }

    }
}
