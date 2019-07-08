package pl.grzeslowski.jsupla.gui.view

import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.VBox
import pl.grzeslowski.jsupla.api.device.Device
import pl.grzeslowski.jsupla.gui.view.executor.ColorExecutor
import pl.grzeslowski.jsupla.gui.view.executor.OnOffExecutor
import pl.grzeslowski.jsupla.gui.view.executor.RollerShutterExecutor
import javax.inject.Inject
import javax.inject.Provider

internal class ViewBuilderImpl @Inject constructor(
        onOffExecutor: Provider<OnOffExecutor>,
        colorExecutor: Provider<ColorExecutor>,
        rollerShutterExecutor: Provider<RollerShutterExecutor>) : ViewBuilder {
    private val builders: List<DeviceViewBuilder> = listOf(
            LightDeviceViewBuilder(onOffExecutor),
            TemperatureAndHumidityDeviceViewBuilder(),
            RgbDeviceViewBuilder(colorExecutor),
            RollerShutterDeviceViewBuilder(rollerShutterExecutor))

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
