package pl.grzeslowski.jsupla.gui.view

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXSlider
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import pl.grzeslowski.jsupla.api.channel.RollerShutterChannel
import pl.grzeslowski.jsupla.api.device.Device
import pl.grzeslowski.jsupla.gui.view.executor.RollerShutterExecutor
import javax.inject.Provider

class RollerShutterDeviceViewBuilder(private val rollerShutterExecutor: Provider<RollerShutterExecutor>) : DeviceViewBuilder {
    override fun build(device: Device, tile: Node): Node? {
        if (isRollerShutterDevice(device).not()) {
            return null
        }
        tile.styleClass.addAll("roller-shutter-device")

        @Suppress("ThrowableNotThrown")
        val rollerShutterChannel = device.channels.stream()
                .filter { it is RollerShutterChannel }
                .map { it as RollerShutterChannel }
                .findAny()
                .orElseThrow { IllegalStateException("Should not happen") }

        val node = VBox(6.0)

        val state = rollerShutterChannel.state
        val openSlider = JFXSlider(0.0, 100.0, state.open.percentage.toDouble())

        val openCloseBox = HBox()
        val openButton = JFXButton("Open")
        val closeButton = JFXButton("Close")
        openButton.maxWidth = Double.MAX_VALUE
        closeButton.maxWidth = Double.MAX_VALUE
        HBox.setHgrow(openButton, Priority.ALWAYS)
        HBox.setHgrow(closeButton, Priority.ALWAYS)
        openButton.alignment = Pos.CENTER
        closeButton.alignment = Pos.CENTER
        openCloseBox.children.addAll(openButton, Separator(Orientation.VERTICAL), closeButton)

        val rollerShutterState = Label("Roller Shutter State:")
        rollerShutterExecutor.get().bind(rollerShutterChannel, openButton, closeButton, openSlider)

        node.children.addAll(rollerShutterState, openSlider, openCloseBox)

        return node
    }

    private fun isRollerShutterDevice(device: Device) = device.channels.stream().anyMatch { it is RollerShutterChannel }
}