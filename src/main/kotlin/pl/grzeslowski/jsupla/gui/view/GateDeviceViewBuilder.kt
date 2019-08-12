package pl.grzeslowski.jsupla.gui.view

import com.jfoenix.controls.JFXButton
import javafx.beans.binding.Bindings
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import pl.grzeslowski.jsupla.gui.i18n.InternationalizationService
import pl.grzeslowski.jsupla.gui.uidevice.UiDevice
import pl.grzeslowski.jsupla.gui.uidevice.UiGateState

class GateDeviceViewBuilder(private val internationalizationService: InternationalizationService) : DeviceViewBuilder {
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
        gateState.textProperty().bind(
                Bindings.`when`(state.open)
                        .then(internationalizationService.findMessage("jSuplaGui.tile.gate.open"))
                        .otherwise(internationalizationService.findMessage("jSuplaGui.tile.gate.close")))
        val button = JFXButton(internationalizationService.findMessage("jSuplaGui.tile.gate.button"))
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
