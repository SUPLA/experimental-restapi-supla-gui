package pl.grzeslowski.jsupla.gui.view

import javafx.beans.property.BooleanProperty
import javafx.scene.Node
import javafx.scene.control.Label
import pl.grzeslowski.jsupla.gui.uidevice.UiDevice

interface DeviceViewBuilder {
    fun build(device: UiDevice, tile: Node): Node?
}

private const val dirtyLabel = "dirty"
fun addDirtyLabelListener(updating: BooleanProperty, vararg labels: Label) {
    updating.addListener { _, _, new ->
        if (new) {
            labels.forEach { it.styleClass.addAll(dirtyLabel) }
        } else {
            labels.forEach { it.styleClass.removeAll(dirtyLabel) }
        }
    }
}