package pl.grzeslowski.jsupla.gui.view

import com.jfoenix.controls.JFXToggleButton
import javafx.scene.Node
import javafx.scene.layout.VBox
import pl.grzeslowski.jsupla.api.channel.Channel
import pl.grzeslowski.jsupla.api.channel.OnOffChannel
import pl.grzeslowski.jsupla.api.device.Device
import java.util.stream.Collectors

class LightDeviceViewBuilder : DeviceViewBuilder {
    override fun build(device: Device, tile: Node): Node? {
        if (isLightDevice(device).not()) {
            return null
        }
        tile.styleClass.addAll("light-device")
        val node = VBox(3.0)
        val channels = device.channels.stream()
                .map { buildChannel(it) }
                .collect(Collectors.toList())
        node.children.addAll(channels)
        return node
    }

    private fun buildChannel(channel: Channel): Node {
        return when (channel) {
            is OnOffChannel -> {
                val toggle = JFXToggleButton()
                toggle.text = channel.caption
                toggle.isSelected = channel.isConnected
                toggle
            }
            else -> throw IllegalStateException("Should never happen!")
        }
    }

    private fun isLightDevice(device: Device): Boolean {
        val numberOfOnOffChannels = device.channels.stream()
                .filter { it is OnOffChannel }
                .count()
                .toInt()
        return numberOfOnOffChannels == device.channels.size
    }
}
