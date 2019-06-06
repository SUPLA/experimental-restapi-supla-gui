package pl.grzeslowski.jsupla.gui

import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel

@ArtifactProviderFor(GriffonModel::class)
class JSuplaGuiModel : AbstractGriffonModel() {
    private var _clickCount: StringProperty = SimpleStringProperty(this, "clickCount", "0")

    var clickCount: String
        get() = _clickCount.get()
        set(s) = _clickCount.set(s)

    fun clickCountProperty() = _clickCount
}