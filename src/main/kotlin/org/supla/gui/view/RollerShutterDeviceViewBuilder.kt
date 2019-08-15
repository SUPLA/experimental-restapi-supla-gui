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
import com.jfoenix.controls.JFXSlider
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.supla.gui.i18n.InternationalizationService
import org.supla.gui.uidevice.UiDevice
import org.supla.gui.uidevice.UiRollerShutterState
import javax.inject.Inject

class RollerShutterDeviceViewBuilder @Inject constructor(private val internationalizationService: InternationalizationService) : DeviceViewBuilder {
    override fun build(device: UiDevice, tile: Node): Node? {
        if (isRollerShutterDevice(device).not()) {
            return null
        }
        tile.styleClass.addAll("roller-shutter-device")

        @Suppress("ThrowableNotThrown")
        val channel = device.channels
                .stream()
                .filter { it.state is UiRollerShutterState }
                .findAny()
                .orElseThrow { IllegalStateException("Should not happen") }
        val state = channel.state as UiRollerShutterState

        val node = VBox(6.0)

        val openSlider = JFXSlider()
        openSlider.valueProperty().bindBidirectional(state.open)
        openSlider.disableProperty().bind(device.updating)

        val openCloseBox = HBox()
        val openButton = JFXButton(internationalizationService.findMessage("jSuplaGui.tile.rollerShutter.open"))
        val closeButton = JFXButton(internationalizationService.findMessage("jSuplaGui.tile.rollerShutter.close"))
        openButton.disableProperty().bind(device.updating)
        closeButton.disableProperty().bind(device.updating)
        openButton.maxWidth = Double.MAX_VALUE
        closeButton.maxWidth = Double.MAX_VALUE
        HBox.setHgrow(openButton, Priority.ALWAYS)
        HBox.setHgrow(closeButton, Priority.ALWAYS)
        openButton.alignment = Pos.CENTER
        closeButton.alignment = Pos.CENTER
        openButton.onAction = EventHandler<ActionEvent> { state.fireOpen() }
        closeButton.onAction = EventHandler<ActionEvent> { state.fireClose() }
        openCloseBox.children.addAll(openButton, Separator(Orientation.VERTICAL), closeButton)
        val rollerShutterState = Label()
        rollerShutterState.textProperty().bindBidirectional(channel.caption)

        node.alignment = Pos.TOP_CENTER
        node.children.addAll(rollerShutterState, openSlider, openCloseBox)

        return node
    }

    private fun isRollerShutterDevice(device: UiDevice) =
            device.channels
                    .stream()
                    .map { it.state }
                    .anyMatch { it is UiRollerShutterState }
}