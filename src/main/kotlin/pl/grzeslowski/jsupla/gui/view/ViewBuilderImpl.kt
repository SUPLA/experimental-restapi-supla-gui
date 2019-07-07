package pl.grzeslowski.jsupla.gui.view

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.VBox
import pl.grzeslowski.jsupla.api.device.Device

internal class ViewBuilderImpl : ViewBuilder {
    private val builders: List<DeviceViewBuilder> = listOf(
            LightDeviceViewBuilder(),
            TemperatureAndHumidityDeviceViewBuilder(),
            RgbDeviceViewBuilder(),
            RollerShutterDeviceViewBuilder())

    override fun buildViewForDevice(device: Device): Node {
        val deviceName = Label(device.name)
        deviceName.styleClass.addAll("header")
        val deviceComment: Label? = if (device.comment != null && device.comment.isNotBlank()) {
            val comment = Label(device.comment)
            comment.styleClass.addAll("comment")
            comment
        } else {
            null
        }
        val node = VBox(3.0)

        deviceName.prefWidthProperty().bind(node.prefWidthProperty())
        deviceComment?.prefWidthProperty()?.bind(node.prefWidthProperty())

        node.styleClass.add("tile")
        node.children.addAll(deviceName)
        if (deviceComment != null) {
            node.children.add(deviceComment)
        }
        node.children.add(Separator())
        val body = builders.stream()
                .map { it.build(device, node) }
                .filter { it != null }
                .findAny()
                .orElse(buildUnknownLabel())
        node.children.add(body)
        return node
    }

    private fun buildUnknownLabel(): Node {
        val label = Label("Unknown device type")
        label.styleClass.addAll("value")
        return label
    }
}
