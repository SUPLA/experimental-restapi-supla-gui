package pl.grzeslowski.jsupla.gui.view

import javafx.scene.Node
import pl.grzeslowski.jsupla.api.device.Device

interface ViewBuilder {
    fun buildViewForDevice(device: Device): Node
}