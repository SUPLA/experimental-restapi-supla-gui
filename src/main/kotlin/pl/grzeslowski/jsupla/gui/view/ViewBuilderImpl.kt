package pl.grzeslowski.jsupla.gui.view

import javafx.scene.Node
import javafx.scene.control.ColorPicker
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.control.Slider
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.controlsfx.control.ToggleSwitch
import pl.grzeslowski.jsupla.api.channel.Channel
import pl.grzeslowski.jsupla.api.channel.DimmerAndRgbLightningChannel
import pl.grzeslowski.jsupla.api.channel.OnOffChannel
import pl.grzeslowski.jsupla.api.channel.RgbLightningChannel
import pl.grzeslowski.jsupla.api.channel.state.BrightnessState
import pl.grzeslowski.jsupla.api.channel.state.ColorState
import pl.grzeslowski.jsupla.api.device.Device
import java.util.stream.Collectors

internal class ViewBuilderImpl : ViewBuilder {
    override fun buildViewForDevice(device: Device): Node {
        val deviceName = Label(device.name)
        deviceName.styleClass.addAll("tile-header")
        val deviceComment: Label? = if (device.comment != null && device.comment.isNotBlank()) {
            val comment = Label(device.comment)
            comment.styleClass.addAll("tile-label")
            comment
        } else {
            null
        }

        val separator = Separator()
        separator.styleClass.add("tile-separator")

        val channels = device.channels.stream()
                .map { buildViewForChannel(it) }
                .filter { it.isNotEmpty() }
                .flatMap { it.stream() }
                .collect(Collectors.toList())

        val node = VBox(10.0)

        node.prefWidthProperty().bindBidirectional(node.prefHeightProperty())
        deviceName.prefWidthProperty().bind(node.prefWidthProperty())
        deviceComment?.prefWidthProperty()?.bind(node.prefWidthProperty())

        node.styleClass.add("tile")
        node.children.addAll(deviceName)
        if (deviceComment != null) {
            node.children.add(deviceComment)
        }
        node.children.add(separator)
        node.children.addAll(channels)
        return node
    }

    private fun buildViewForChannel(channel: Channel): List<Node> =
            when (channel) {
                is OnOffChannel -> {
                    val toggle = ToggleSwitch(channel.caption)
                    toggle.styleClass.addAll("tile-toggle")
                    toggle.isSelected = channel.isConnected
                    listOf(toggle)
                }
                is RgbLightningChannel -> {
//                    val hueSlider = buildHueSlider(channel.state)
//                    val saturationSlider = buildSaturationSlider(channel.state)
//                    val valueSlider = buildValueSlider(channel.state)
//
//                    listOf(hueSlider, saturationSlider, valueSlider)
                    val element = ColorPicker(Color.RED)
                    listOf(element)
                }
                is DimmerAndRgbLightningChannel -> {
                    val hueSlider = buildHueSlider(channel.state)
                    val saturationSlider = buildSaturationSlider(channel.state)
                    val valueSlider = buildValueSlider(channel.state)
                    val dimmerSlider = buildDimmerSlider(channel.state)

                    listOf(hueSlider, saturationSlider, valueSlider, dimmerSlider)
                }
                else -> listOf()
            }

    private fun buildHueSlider(state: ColorState): Node {
        val hueSlider = Slider(0.0, 359.0, state.hsv.hue)
        hueSlider.styleClass.addAll("tile-slider")
        return hueSlider
    }

    private fun buildSaturationSlider(state: ColorState): Node {
        val saturationSlider = Slider(0.0, 100.0, state.hsv.saturation * 100)
        saturationSlider.styleClass.addAll("tile-slider")
        return saturationSlider
    }

    private fun buildValueSlider(state: ColorState): Node {
        val valueSlider = Slider(0.0, 100.0, state.hsv.value * 100)
        valueSlider.styleClass.addAll("tile-slider")
        return valueSlider
    }

    private fun buildDimmerSlider(state: BrightnessState): Node {
        val brightnessSlider = Slider(0.0, 100.0, state.brightness.percentage.toDouble())
        brightnessSlider.styleClass.addAll("tile-slider")
        return brightnessSlider
    }
}
