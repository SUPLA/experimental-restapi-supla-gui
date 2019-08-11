package pl.grzeslowski.jsupla.gui.view

import javafx.scene.Node
import pl.grzeslowski.jsupla.gui.uidevice.UiDevice

interface DeviceViewBuilder {
    fun build(device: UiDevice, tile: Node): Node?
}