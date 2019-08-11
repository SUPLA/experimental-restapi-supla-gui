package pl.grzeslowski.jsupla.gui.view

import javafx.scene.Node
import pl.grzeslowski.jsupla.gui.uidevice.UiDevice

interface ViewBuilder {
    fun buildViewForDevice(device: UiDevice): Node
}