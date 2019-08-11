package pl.grzeslowski.jsupla.gui.uidevice

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import pl.grzeslowski.jsupla.api.channel.Channel

class UiChannel(val nativeChannel: Channel) : Comparable<UiChannel> {
    val id = nativeChannel.id
    val caption = SimpleStringProperty(nativeChannel.caption)
    val connected = SimpleBooleanProperty(nativeChannel.isConnected)
    val state: UiState = buildUiState(nativeChannel.state)

    override fun compareTo(other: UiChannel): Int = nativeChannel.compareTo(other.nativeChannel)

    override fun toString(): String {
        return "UiChannel(id=$id, caption=${caption.value}, connected=${connected.value})"
    }
}
