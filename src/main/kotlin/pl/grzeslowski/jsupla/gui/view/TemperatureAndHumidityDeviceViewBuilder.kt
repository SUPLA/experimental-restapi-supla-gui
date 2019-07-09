package pl.grzeslowski.jsupla.gui.view

import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import pl.grzeslowski.jsupla.api.channel.Channel
import pl.grzeslowski.jsupla.api.channel.HumidityChannel
import pl.grzeslowski.jsupla.api.channel.TemperatureAndHumidityChannel
import pl.grzeslowski.jsupla.api.channel.TemperatureChannel
import pl.grzeslowski.jsupla.api.channel.state.HumidityState
import pl.grzeslowski.jsupla.api.channel.state.TemperatureState
import pl.grzeslowski.jsupla.api.device.Device

class TemperatureAndHumidityDeviceViewBuilder : DeviceViewBuilder {
    override fun build(device: Device, tile: Node): Node? {
        if (isTempAndHumDevice(device).not()) {
            return null
        }

        tile.styleClass.addAll("temp-hum-device")

        @Suppress("ThrowableNotThrown")
        val channel = device.channels.stream()
                .filter { isTempOrHumChannel(it) }
                .findAny()
                .orElseThrow { IllegalStateException("should not happen!") }

        val left = VBox(3.0)
        val right = VBox(3.0)

        if (channel is TemperatureChannel) {
            val label = Label("Temperature:")
            val value = buildTemperatureLabel(channel.state)
            addRow(left, right, label, value)
        }

        if (channel is HumidityChannel) {
            val label = Label("Humidity:")
            val value = buildHumidityLabel(channel.state)
            addRow(left, right, label, value)
        }

        val node = HBox(6.0)
        node.children.addAll(left, right)
        return node
    }

    private fun isTempAndHumDevice(device: Device) =
            device.channels.stream().anyMatch { isTempOrHumChannel(it) }

    private fun isTempOrHumChannel(channel: Channel) =
            channel is TemperatureChannel || channel is HumidityChannel || channel is TemperatureAndHumidityChannel

    private fun addRow(left: Pane, right: Pane, label: Label, value: Label) {
        label.styleClass.addAll("value")
        label.alignment = Pos.CENTER_RIGHT
        label.maxWidth = Double.MAX_VALUE

        value.styleClass.addAll("value")
        value.alignment = Pos.CENTER_LEFT
        value.maxWidth = Double.MAX_VALUE

        left.children.addAll(label)
        right.children.addAll(value)
    }

    private fun buildTemperatureLabel(state: TemperatureState): Label {
        return Label(state.temperatureState.toString() + " °C")
    }

    private fun buildHumidityLabel(state: HumidityState): Label {
        return Label(state.humidityState.percentage.toString() + "%")
    }
}
