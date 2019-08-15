package pl.grzeslowski.jsupla.gui.view

import com.jfoenix.controls.JFXSlider
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.Property
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ColorPicker
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import pl.grzeslowski.jsupla.gui.i18n.InternationalizationService
import pl.grzeslowski.jsupla.gui.uidevice.*

class RgbDeviceViewBuilder(private val internationalizationService: InternationalizationService) : DeviceViewBuilder {
    override fun build(device: UiDevice, tile: Node): Node? {
        if (isRgbDevice(device).not()) {
            return null
        }

        tile.styleClass.addAll("color-device")

        @Suppress("ThrowableNotThrown")
        val rgbChannel = device.channels.stream()
                .filter { isRgbChannel(it.state) }
                .findAny()
                .orElseThrow { IllegalStateException("Should not happen!") }

        val node = VBox(3.0)

        val updating = device.updating
        when {
            rgbChannel.state is UiColorState -> hsvSliders(rgbChannel.state.hue, rgbChannel.state.saturation, rgbChannel.state.value, rgbChannel.state.rgb, updating, node)
            rgbChannel.state is UiDimmerState -> dimmerSlider(rgbChannel.state.brightness, updating, node)
            rgbChannel.state is UiColorAndBrightnessState -> {
                hsvSliders(rgbChannel.state.hue, rgbChannel.state.saturation, rgbChannel.state.value, rgbChannel.state.rgb, updating, node)
                dimmerSlider(rgbChannel.state.brightness, updating, node)
            }
        }

        return node
    }

    private fun dimmerSlider(brightness: DoubleProperty, updating: BooleanProperty, node: VBox) {
        val dimmer = JFXSlider()
        dimmer.valueProperty().bindBidirectional(brightness)
        dimmer.disableProperty().bind(updating)
        node.children.addAll(
                Label(internationalizationService.findMessage("jSuplaGui.tile.brightness")),
                dimmer
        )
    }

    private fun hsvSliders(hue: DoubleProperty,
                           saturation: DoubleProperty,
                           value: DoubleProperty,
                           color: Property<Color>,
                           updating: BooleanProperty,
                           node: VBox) {
        val colorPicker = ColorPicker()
        colorPicker.disableProperty().bind(updating)
        VBox.setVgrow(colorPicker, Priority.ALWAYS)
        colorPicker.maxWidth = Double.MAX_VALUE
        colorPicker.valueProperty().bindBidirectional(color)
        val hueSlider = JFXSlider()
        hueSlider.disableProperty().bind(updating)
        hueSlider.max = 359.0
        hueSlider.valueProperty().bindBidirectional(hue)
        val saturationSlider = JFXSlider()
        saturationSlider.disableProperty().bind(updating)
        saturationSlider.valueProperty().bindBidirectional(saturation)
        val valueSlider = JFXSlider()
        valueSlider.disableProperty().bind(updating)
        valueSlider.valueProperty().bindBidirectional(value)

        node.alignment = Pos.TOP_CENTER
        node.children.addAll(
                colorPicker,
                Label(internationalizationService.findMessage("jSuplaGui.tile.hue")),
                hueSlider,
                Label(internationalizationService.findMessage("jSuplaGui.tile.saturation")),
                saturationSlider,
                Label(internationalizationService.findMessage("jSuplaGui.tile.value")),
                valueSlider
        )
    }

    private fun isRgbDevice(device: UiDevice) =
            device.channels
                    .stream()
                    .map { it.state }
                    .anyMatch { isRgbChannel(it) }

    private fun isRgbChannel(state: UiState) =
            state is UiColorState || state is UiColorAndBrightnessState || state is UiDimmerState
}