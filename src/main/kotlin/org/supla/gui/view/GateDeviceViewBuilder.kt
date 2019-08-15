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
package org.supla.gui.view

import com.jfoenix.controls.JFXButton
import javafx.beans.binding.Bindings
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import org.supla.gui.i18n.InternationalizationService
import org.supla.gui.uidevice.UiDevice
import org.supla.gui.uidevice.UiGateState
import pl.grzeslowski.jsupla.api.channel.state.GateState
import java.util.concurrent.Callable
import javax.inject.Inject

class GateDeviceViewBuilder @Inject constructor(
        private val internationalizationService: InternationalizationService) : DeviceViewBuilder {
    override fun build(device: UiDevice, tile: Node): Node? {
        if (isGateDevice(device).not()) {
            return null
        }

        tile.styleClass.addAll("gate-device")
        val node = VBox(6.0)
        @Suppress("ThrowableNotThrown")
        val state =
                device.channels
                        .stream()
                        .map { it.state }
                        .filter { it is UiGateState }
                        .map { it as UiGateState }
                        .findAny()
                        .orElseThrow { IllegalStateException("Should never happen!") }
        val header = Label(internationalizationService.findMessage("jSuplaGui.tile.gate.header"))
        val gateState = Label()
        gateState.styleClass.addAll("value")
        addDirtyLabelListener(device.updating, gateState)
        gateState.textProperty().bind(
                Bindings.createStringBinding(
                        Callable {
                            val key = when (state.gateState.value) {
                                GateState.Position.OPENED -> "jSuplaGui.tile.gate.open"
                                GateState.Position.CLOSED -> "jSuplaGui.tile.gate.close"
                                GateState.Position.PARTIALLY_OPENED -> "jSuplaGui.tile.gate.partiallyOpen"
                                null -> "jSuplaGui.tile.gate.pending"
                            }
                            internationalizationService.findMessage(key)
                        },
                        state.gateState))
        val button = JFXButton(internationalizationService.findMessage("jSuplaGui.tile.gate.button"))
        button.disableProperty().bind(device.updating)
        button.onAction = EventHandler<ActionEvent> { state.fireOpenClose() }
        node.alignment = Pos.TOP_CENTER
        node.children.addAll(header, gateState, button)
        return node
    }

    private fun isGateDevice(device: UiDevice): Boolean =
            device.channels
                    .stream()
                    .map { it.state }
                    .anyMatch { it is UiGateState }
}
