package pl.grzeslowski.jsupla.gui

import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel

@ArtifactProviderFor(GriffonModel::class)
class LoginModel : AbstractGriffonModel() {
    var token: StringProperty = SimpleStringProperty(this, "tokenTextField", "")
}