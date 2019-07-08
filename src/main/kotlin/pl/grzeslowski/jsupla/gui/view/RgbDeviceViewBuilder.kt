package pl.grzeslowski.jsupla.gui.view

import com.jfoenix.controls.JFXSlider
import javafx.scene.Node
import javafx.scene.control.ColorPicker
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import pl.grzeslowski.jsupla.api.channel.Channel
import pl.grzeslowski.jsupla.api.channel.DimmerAndRgbLightningChannel
import pl.grzeslowski.jsupla.api.channel.DimmerChannel
import pl.grzeslowski.jsupla.api.channel.RgbLightningChannel
import pl.grzeslowski.jsupla.api.channel.state.BrightnessState
import pl.grzeslowski.jsupla.api.channel.state.ColorState
import pl.grzeslowski.jsupla.api.device.Device
import pl.grzeslowski.jsupla.gui.view.executor.ColorExecutor
import javax.inject.Provider

class RgbDeviceViewBuilder(private val colorExecutor: Provider<ColorExecutor>) : DeviceViewBuilder {
    override fun build(device: Device, tile: Node): Node? {
        if (isRgbDevice(device).not()) {
            return null
        }

        tile.styleClass.addAll("color-device")

        @Suppress("ThrowableNotThrown")
        val rgbChannel = device.channels.stream()
                .filter { isRgbChannel(it) }
                .findAny()
                .orElseThrow { IllegalStateException("Should not happen!") }

        val node = VBox(3.0)

        var hue: Slider? = null
        var saturation: Slider? = null
        var value: Slider? = null
        var dimmer: Slider? = null

        if (rgbChannel is RgbLightningChannel || rgbChannel is DimmerAndRgbLightningChannel) {
            val state: ColorState = rgbChannel.state as ColorState
            hue = buildHueSlider(state)
            saturation = buildSaturationSlider(state)
            value = buildValueSlider(state)
            node.children.addAll(
                    buildColorPicker(state),
                    Label("Hue:"),
                    hue,
                    Label("Saturation:"),
                    saturation,
                    Label("Value:"),
                    value
            )
        }
        if (rgbChannel is DimmerChannel || rgbChannel is DimmerAndRgbLightningChannel) {
            val state: BrightnessState = rgbChannel.state as BrightnessState
            dimmer = buildDimmerSlider(state)
            node.children.addAll(
                    Label("Brightness:"),
                    dimmer
            )
        }

        colorExecutor.get().bind(rgbChannel, hue, saturation, value, dimmer)

        return node
    }

    private fun buildHueSlider(state: ColorState): Slider {
        val hueSlider = JFXSlider(0.0, 359.0, state.hsv.hue)
        return hueSlider
    }

    private fun buildSaturationSlider(state: ColorState): Slider {
        val saturationSlider = JFXSlider(0.0, 100.0, state.hsv.saturation * 100)
        return saturationSlider
    }

    private fun buildValueSlider(state: ColorState): Slider {
        val valueSlider = JFXSlider(0.0, 100.0, state.hsv.value * 100)
        return valueSlider
    }

    private fun buildDimmerSlider(state: BrightnessState): Slider {
        val brightnessSlider = JFXSlider(0.0, 100.0, state.brightness.percentage.toDouble())
        return brightnessSlider
    }

    private fun buildColorPicker(state: ColorState): Node {
        val rgb = state.rgb
        val color: Color = Color.color(rgb.red / 256.0, rgb.blue / 256.0, rgb.green / 256.0)
        return ColorPicker(color)
    }

    private fun isRgbDevice(device: Device) = device.channels.stream().anyMatch { isRgbChannel(it) }

    private fun isRgbChannel(channel: Channel) =
            channel is RgbLightningChannel || channel is DimmerAndRgbLightningChannel || channel is DimmerChannel
}