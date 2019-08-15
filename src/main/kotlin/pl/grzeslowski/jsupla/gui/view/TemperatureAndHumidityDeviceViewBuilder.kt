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

import javafx.beans.binding.Bindings
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import pl.grzeslowski.jsupla.api.channel.state.Percentage
import pl.grzeslowski.jsupla.gui.i18n.InternationalizationService
import pl.grzeslowski.jsupla.gui.uidevice.*
import java.math.BigDecimal
import java.util.concurrent.Callable
import javax.inject.Inject

class TemperatureAndHumidityDeviceViewBuilder @Inject constructor(private val internationalizationService: InternationalizationService) : DeviceViewBuilder {
    override fun build(device: UiDevice, tile: Node): Node? {
        if (isTempAndHumDevice(device).not()) {
            return null
        }

        tile.styleClass.addAll("temp-hum-device")

        @Suppress("ThrowableNotThrown")
        val channel = device.channels.stream()
                .filter { isTempOrHumChannel(it.state) }
                .findAny()
                .orElseThrow { IllegalStateException("should not happen!") }

        val left = VBox(3.0)
        val right = VBox(3.0)

        val state = channel.state
        if (state is UiTemperatureState) {
            addTemperatureLabel(state.temperature, left, right, device.updating)
        }

        if (state is UiHumidityState) {
            addHumidityLabel(state.humidity, left, right, device.updating)
        }

        if (state is UiTemperatureAndHumidityState) {
            addTemperatureLabel(state.temperature, left, right, device.updating)
            addHumidityLabel(state.humidity, left, right, device.updating)
        }

        val node = HBox(6.0)
        node.alignment = Pos.TOP_CENTER
        node.children.addAll(left, right)
        return node
    }

    private fun isTempAndHumDevice(device: UiDevice) =
            device.channels
                    .stream()
                    .map { it.state }
                    .anyMatch { isTempOrHumChannel(it) }

    private fun isTempOrHumChannel(state: UiState) =
            state is UiTemperatureState || state is UiHumidityState || state is UiTemperatureAndHumidityState

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

    private fun addTemperatureLabel(temperature: SimpleObjectProperty<BigDecimal>, left: Pane, right: Pane, updating: BooleanProperty) {
        val label = Label(internationalizationService.findMessage("jSuplaGui.tile.temperature"))
        val value = Label()
        value.textProperty().bind(
                Bindings.createDoubleBinding(Callable { temperature.value.toDouble() }, temperature)
                        .asString()
                        .concat(" °C")
        )
        addDirtyLabelListener(updating, value)
        addRow(left, right, label, value)
    }

    private fun addHumidityLabel(humidity: SimpleObjectProperty<Percentage>, left: Pane, right: Pane, updating: BooleanProperty) {
        val label = Label(internationalizationService.findMessage("jSuplaGui.tile.humidity"))
        val value = Label()
        value.textProperty().bind(
                Bindings.createDoubleBinding(Callable { humidity.value.percentage.toDouble() }, humidity)
                        .asString()
                        .concat("%")
        )
        addDirtyLabelListener(updating, value)
        addRow(left, right, label, value)
    }
}
