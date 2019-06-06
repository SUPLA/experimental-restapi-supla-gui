package pl.grzeslowski.jsupla.gui.view

import javafx.scene.Node
import pl.grzeslowski.jsupla.api.generated.model.Device

interface ViewBuilder {
    fun buildViewForDevice(device: Device): Node
}