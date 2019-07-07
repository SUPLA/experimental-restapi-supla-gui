package pl.grzeslowski.jsupla.gui.view

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import pl.grzeslowski.jsupla.api.channel.Channel
import pl.grzeslowski.jsupla.api.channel.HumidityChannel
import pl.grzeslowski.jsupla.api.channel.TemperatureAndHumidityChannel
import pl.grzeslowski.jsupla.api.channel.TemperatureChannel
import pl.grzeslowski.jsupla.api.channel.state.HumidityState
import pl.grzeslowski.jsupla.api.channel.state.TemperatureAndHumidityState
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

        return when (channel) {
            is TemperatureAndHumidityChannel -> buildTemperatureAndHumidityLabels(channel.state)
            is TemperatureChannel -> buildTemperatureLabel(channel.state)
            is HumidityChannel -> buildHumidityLabel(channel.state)
            else -> throw IllegalStateException("Should not happen!")
        }
    }

    private fun isTempAndHumDevice(device: Device) =
            device.channels.stream().anyMatch { isTempOrHumChannel(it) }

    private fun isTempOrHumChannel(channel: Channel) =
            channel is TemperatureChannel || channel is HumidityChannel || channel is TemperatureAndHumidityChannel

    private fun buildTemperatureLabel(state: TemperatureState): Label {
        val temp = Label(state.temperatureState.toString() + " °C")
        temp.maxWidth = Double.MAX_VALUE
        HBox.setHgrow(temp, Priority.ALWAYS)
        temp.alignment = Pos.CENTER
        temp.styleClass.addAll("value")
        return temp
    }

    private fun buildHumidityLabel(state: HumidityState): Label {
        val hum = Label(state.humidityState.percentage.toString() + "%")
        hum.maxWidth = Double.MAX_VALUE
        HBox.setHgrow(hum, Priority.ALWAYS)
        hum.alignment = Pos.CENTER
        hum.styleClass.addAll("value")
        return hum
    }

    private fun buildTemperatureAndHumidityLabels(state: TemperatureAndHumidityState): Node {
        val node = HBox()
        val temp = buildTemperatureLabel(state)
        val hum = buildHumidityLabel(state)
        node.children.addAll(temp, Separator(Orientation.VERTICAL), hum)
        temp.maxWidth = Double.MAX_VALUE
        hum.maxWidth = Double.MAX_VALUE
        HBox.setHgrow(temp, Priority.ALWAYS)
        HBox.setHgrow(hum, Priority.ALWAYS)
        temp.alignment = Pos.CENTER
        hum.alignment = Pos.CENTER
        temp.styleClass.addAll("value")
        hum.styleClass.addAll("value")
        return node
    }
}
