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