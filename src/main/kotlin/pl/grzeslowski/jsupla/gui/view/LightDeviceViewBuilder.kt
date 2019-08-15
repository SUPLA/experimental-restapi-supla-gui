// Copyright (C) AC SOFTWARE SP. Z O.O.
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
package pl.grzeslowski.jsupla.gui.view

import com.jfoenix.controls.JFXToggleButton
import javafx.beans.property.BooleanProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.VBox
import pl.grzeslowski.jsupla.gui.uidevice.UiChannel
import pl.grzeslowski.jsupla.gui.uidevice.UiDevice
import pl.grzeslowski.jsupla.gui.uidevice.UiOnOffState
import java.util.stream.Collectors

class LightDeviceViewBuilder : DeviceViewBuilder {
    override fun build(device: UiDevice, tile: Node): Node? {
        if (isLightDevice(device).not()) {
            return null
        }
        tile.styleClass.addAll("light-device")
        val node = VBox(3.0)
        val channels = device.channels
                .stream()
                .map { buildChannel(it, device.updating) }
                .collect(Collectors.toList())
        node.alignment = Pos.TOP_CENTER
        node.children.addAll(channels)
        return node
    }

    private fun buildChannel(channel: UiChannel, updating: BooleanProperty): Node {
        return when (channel.state) {
            is UiOnOffState -> {
                val toggle = JFXToggleButton()
                toggle.isDisable = channel.nativeChannel.isInput
                toggle.isWrapText = true
                toggle.disableProperty().bind(updating)
                toggle.textProperty().bindBidirectional(channel.caption)
                toggle.selectedProperty().bindBidirectional(channel.state.on)
                toggle
            }
            else -> throw IllegalStateException("Should never happen!")
        }
    }

    private fun isLightDevice(device: UiDevice): Boolean {
        val numberOfOnOffChannels = device.channels
                .stream()
                .map { it.state }
                .filter { it is UiOnOffState }
                .count()
                .toInt()
        return numberOfOnOffChannels == device.channels.size
    }
}
