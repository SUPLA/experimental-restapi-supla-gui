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
package org.supla.gui.uidevice

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
            .filter { channel -> channel.isConnected }
            .map { channel -> UiChannel(channel) }
            .collect(Collectors.toList())
    val updating = SimpleBooleanProperty()

    override fun compareTo(other: UiDevice): Int = nativeDevice.compareTo(other.nativeDevice)

    override fun toString(): String {
        return "UiDevice(id=$id, name=${name.value}, comment=${comment.value})"
    }
}
