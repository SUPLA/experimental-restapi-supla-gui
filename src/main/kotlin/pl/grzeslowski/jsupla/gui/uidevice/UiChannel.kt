// Copyright (C) AC SOFTWARE SP. Z O.O.
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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
