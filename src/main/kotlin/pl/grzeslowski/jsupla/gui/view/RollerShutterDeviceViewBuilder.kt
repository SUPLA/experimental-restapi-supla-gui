package pl.grzeslowski.jsupla.gui.view

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
import pl.grzeslowski.jsupla.gui.i18n.InternationalizationService
import pl.grzeslowski.jsupla.gui.uidevice.UiDevice
import pl.grzeslowski.jsupla.gui.uidevice.UiRollerShutterState
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