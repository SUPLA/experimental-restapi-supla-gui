package org.supla.gui.view

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import org.supla.gui.i18n.InternationalizationService
import org.supla.gui.uidevice.UiDevice
import javax.inject.Inject

class EmptyDeviceViewBuilder @Inject constructor(
        private val internationalizationService: InternationalizationService) : DeviceViewBuilder {
    override fun build(device: UiDevice, tile: Node): Node? =
            if (device.channels.isEmpty()) {
                val label = Label(internationalizationService.findMessage("jSuplaGui.tile.empty"))
                label.isWrapText = true
                label.styleClass.addAll("value")
                tile.styleClass.addAll("unknown")
                StackPane(label)
            } else {
                null
            }
}
