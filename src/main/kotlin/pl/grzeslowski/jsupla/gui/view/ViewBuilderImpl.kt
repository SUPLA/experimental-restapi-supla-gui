package pl.grzeslowski.jsupla.gui.view

import com.jfoenix.controls.JFXSlider
import com.jfoenix.controls.JFXToggleButton
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ColorPicker
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import pl.grzeslowski.jsupla.api.channel.*
import pl.grzeslowski.jsupla.api.channel.state.BrightnessState
import pl.grzeslowski.jsupla.api.channel.state.ColorState
import pl.grzeslowski.jsupla.api.device.Device
import java.util.stream.Collectors

internal class ViewBuilderImpl : ViewBuilder {
    override fun buildViewForDevice(device: Device): Node {
        val deviceName = Label(device.name)
        deviceName.styleClass.addAll("header")
        val deviceComment: Label? = if (device.comment != null && device.comment.isNotBlank()) {
            val comment = Label(device.comment)
            comment.styleClass.addAll("comment")
            comment
        } else {
            null
        }

        val channels = device.channels.stream()
                .map { buildViewForChannel(it) }
                .filter { it.isNotEmpty() }
                .flatMap { it.stream() }
                .collect(Collectors.toList())

        val node = VBox(3.0)

        // set tile color
        val numberOfOnOffChannels = device.channels.stream()
                .filter { it is OnOffChannel }
                .count()
                .toInt()
        val isTempHum = device.channels.stream()
                .anyMatch { it is TemperatureChannel || it is HumidityChannel || it is TemperatureAndHumidityChannel }
        val isColorDevice = device.channels.stream()
                .anyMatch { it is RgbLightningChannel || it is DimmerAndRgbLightningChannel || it is DimmerChannel }

        when {
            numberOfOnOffChannels == device.channels.size -> node.styleClass.addAll("light-device")
            isTempHum -> node.styleClass.addAll("temp-hum-device")
            isColorDevice -> node.styleClass.addAll("color-device")
        }

        node.prefWidthProperty().bindBidirectional(node.prefHeightProperty())
        deviceName.prefWidthProperty().bind(node.prefWidthProperty())
        deviceComment?.prefWidthProperty()?.bind(node.prefWidthProperty())

        node.styleClass.add("tile")
        node.children.addAll(deviceName)
        if (deviceComment != null) {
            node.children.add(deviceComment)
        }
        node.children.add(Separator())
        node.children.addAll(channels)
        return node
    }

    private fun buildViewForChannel(channel: Channel): List<Node> =
            when (channel) {
                is OnOffChannel -> {
                    val toggle = JFXToggleButton()
                    toggle.text = channel.caption
                    toggle.styleClass.addAll("tile-toggle")
                    toggle.isSelected = channel.isConnected
                    listOf(toggle)
                }
                is RgbLightningChannel -> {
                    val colorPicker = buildColorPicker(channel.state)
                    val hueSlider = buildHueSlider(channel.state)
                    val saturationSlider = buildSaturationSlider(channel.state)
                    val valueSlider = buildValueSlider(channel.state)
                    listOf(colorPicker, hueSlider, saturationSlider, valueSlider)
                }
                is DimmerAndRgbLightningChannel -> {
                    val colorPicker = buildColorPicker(channel.state)
                    val hueSlider = buildHueSlider(channel.state)
                    val saturationSlider = buildSaturationSlider(channel.state)
                    val valueSlider = buildValueSlider(channel.state)
                    val dimmerSlider = buildDimmerSlider(channel.state)

                    listOf(colorPicker, hueSlider, saturationSlider, valueSlider, dimmerSlider)
                }
                is DimmerChannel -> {
                    listOf(buildDimmerSlider(channel.state))
                }
                is TemperatureAndHumidityChannel -> {
                    val node = HBox()
                    val temp = Label(channel.state.temperatureState.toString() + " °C")
                    val hum = Label(channel.state.humidityState.percentage.toString() + "%")
                    node.children.addAll(temp, Separator(Orientation.VERTICAL), hum)
                    temp.maxWidth = Double.MAX_VALUE
                    hum.maxWidth = Double.MAX_VALUE
                    HBox.setHgrow(temp, Priority.ALWAYS)
                    HBox.setHgrow(hum, Priority.ALWAYS)
                    temp.alignment = Pos.CENTER
                    hum.alignment = Pos.CENTER
                    temp.styleClass.addAll("value")
                    hum.styleClass.addAll("value")
                    listOf(node)
                }
                is TemperatureChannel -> {
                    val temp = Label(channel.state.temperatureState.toString() + " °C")
                    temp.maxWidth = Double.MAX_VALUE
                    HBox.setHgrow(temp, Priority.ALWAYS)
                    temp.alignment = Pos.CENTER
                    temp.styleClass.addAll("value")
                    listOf(temp)
                }
                is HumidityChannel -> {
                    val hum = Label(channel.state.humidityState.percentage.toString() + "%")
                    hum.maxWidth = Double.MAX_VALUE
                    HBox.setHgrow(hum, Priority.ALWAYS)
                    hum.alignment = Pos.CENTER
                    hum.styleClass.addAll("value")
                    listOf(hum)
                }
                else -> listOf()
            }

    private fun buildHueSlider(state: ColorState): Node {
        val hueSlider = JFXSlider(0.0, 359.0, state.hsv.hue)
        return hueSlider
    }

    private fun buildSaturationSlider(state: ColorState): Node {
        val saturationSlider = JFXSlider(0.0, 100.0, state.hsv.saturation * 100)
        return saturationSlider
    }

    private fun buildValueSlider(state: ColorState): Node {
        val valueSlider = JFXSlider(0.0, 100.0, state.hsv.value * 100)
        return valueSlider
    }

    private fun buildDimmerSlider(state: BrightnessState): Node {
        val brightnessSlider = JFXSlider(0.0, 100.0, state.brightness.percentage.toDouble())
        return brightnessSlider
    }

    private fun buildColorPicker(state: ColorState): Node {
        val rgb = state.rgb
        val color: Color = Color.color(rgb.red / 256.0, rgb.blue / 256.0, rgb.green / 256.0)
        return ColorPicker(color)
    }
}
