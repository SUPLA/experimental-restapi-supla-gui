package pl.grzeslowski.jsupla.gui.view

import javafx.scene.Node
import pl.grzeslowski.jsupla.api.device.Device

interface DeviceViewBuilder {
    fun build(device: Device, tile: Node): Node?
}