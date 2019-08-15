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

import com.jfoenix.controls.JFXProgressBar
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.supla.gui.i18n.InternationalizationService
import org.supla.gui.uidevice.UiDevice
import javax.inject.Inject

internal class ViewBuilderImpl @Inject constructor(
        private val internationalizationService: InternationalizationService,
        gateDeviceViewBuilder: GateDeviceViewBuilder,
        lightDeviceViewBuilder: LightDeviceViewBuilder,
        temperatureAndHumidityDeviceViewBuilder: TemperatureAndHumidityDeviceViewBuilder,
        rgbDeviceViewBuilder: RgbDeviceViewBuilder,
        rollerShutterDeviceViewBuilder: RollerShutterDeviceViewBuilder) : ViewBuilder {
    private val builders: List<DeviceViewBuilder> = listOf(
            gateDeviceViewBuilder,
            lightDeviceViewBuilder,
            temperatureAndHumidityDeviceViewBuilder,
            rgbDeviceViewBuilder,
            rollerShutterDeviceViewBuilder)

    override fun buildViewForDevice(device: UiDevice): Node {
        val deviceName = Label()
        deviceName.textProperty().bind(device.name)
        deviceName.isWrapText = true
        deviceName.styleClass.addAll("title")
        val deviceComment: Label? = if (device.comment.value != null && device.comment.value.isNotBlank()) {
            val comment = Label()
            comment.textProperty().bind(device.comment)
            comment.styleClass.addAll("sub-title")
            comment.isWrapText = true
            comment
        } else {
            null
        }
        val node = VBox(3.0)
        node.styleClass.add("tile")

        deviceName.prefWidthProperty().bind(node.prefWidthProperty())
        deviceComment?.prefWidthProperty()?.bind(deviceName.prefWidthProperty())

        val spinner = JFXProgressBar()
        spinner.visibleProperty().bind(device.updating)
        spinner.maxWidth = Double.MAX_VALUE

        val header = VBox(9.0)
        header.styleClass.addAll("header")
        header.children.addAll(deviceName)
        if (deviceComment != null) {
            header.children.add(deviceComment)
            VBox.setVgrow(deviceComment, Priority.ALWAYS)
        }
        VBox.setVgrow(deviceName, Priority.ALWAYS)
        VBox.setVgrow(spinner, Priority.ALWAYS)
        header.children.addAll(spinner)
        node.children.addAll(header)

        val body = builders.stream()
                .map { it.build(device, node) }
                .filter { it != null }
                .findAny()
                .orElse(buildUnknownLabel())!!
        body.maxWidth(Double.MAX_VALUE)
        VBox.setVgrow(body, Priority.ALWAYS)
        body.styleClass.addAll("body")
        node.children.add(body)
        return node
    }

    private fun buildUnknownLabel(): Node {
        val label = Label(internationalizationService.findMessage("jSuplaGui.tile.unknown"))
        label.styleClass.addAll("value")
        return label
    }
}
