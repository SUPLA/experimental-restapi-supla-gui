package pl.grzeslowski.jsupla.gui.uidevice

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import pl.grzeslowski.jsupla.api.device.Device
import java.util.stream.Collectors

class UiDevice(private val nativeDevice: Device) : Comparable<UiDevice> {
    val id = nativeDevice.id
    val name = SimpleStringProperty(nativeDevice.name)
    val comment = SimpleStringProperty(nativeDevice.comment)
    val channels: List<UiChannel> = nativeDevice.channels
            .stream()
            .map { channel -> UiChannel(channel) }
            .collect(Collectors.toList())
    val updating = SimpleBooleanProperty()

    override fun compareTo(other: UiDevice): Int = nativeDevice.compareTo(other.nativeDevice)

    override fun toString(): String {
        return "UiDevice(id=$id, name=${name.value}, comment=${comment.value})"
    }
}
