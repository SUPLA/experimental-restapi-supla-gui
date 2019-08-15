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
package pl.grzeslowski.jsupla.gui

import griffon.core.artifact.GriffonModel
import griffon.javafx.collections.GriffonFXCollections.uiThreadAwareObservableSet
import griffon.metadata.ArtifactProviderFor
import javafx.beans.property.SetProperty
import javafx.beans.property.SimpleSetProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections.observableSet
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel
import pl.grzeslowski.jsupla.gui.uidevice.UiDevice
import java.util.*
import java.util.concurrent.atomic.AtomicReference

@ArtifactProviderFor(GriffonModel::class)
class JSuplaGuiModel : AbstractGriffonModel() {
    val address: StringProperty = SimpleStringProperty(this, "addressValueLabel", "N/A")
    val cloudVersion: StringProperty = SimpleStringProperty(this, "cloudVersionValueLabel", "N/A")
    val apiVersion: StringProperty = SimpleStringProperty(this, "apiVersionValueLabel", "N/A")
    val supportedApiVersions: StringProperty = SimpleStringProperty(this, "supportedApiVersionsValueLabel", "N/A")

    val devices: SetProperty<UiDevice> = SimpleSetProperty(this, "devices", uiThreadAwareObservableSet(observableSet(TreeSet())))

    private val refreshListener = AtomicReference<() -> Unit>()

    fun fireRefresh() {
        refreshListener.get()?.invoke()
    }

    fun listenOnRefresh(runnable: () -> Unit) {
        refreshListener.set(runnable)
    }

    fun clearOnRefresh() {
        refreshListener.set { }
    }
}
