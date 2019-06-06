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
import pl.grzeslowski.jsupla.api.generated.model.Device

@ArtifactProviderFor(GriffonModel::class)
class JSuplaGuiModel : AbstractGriffonModel() {
    val address: StringProperty = SimpleStringProperty(this, "addressValueLabel", "N/A")
    val cloudVersion: StringProperty = SimpleStringProperty(this, "cloudVersionValueLabel", "N/A")
    val apiVersion: StringProperty = SimpleStringProperty(this, "apiVersionValueLabel", "N/A")
    val supportedApiVersions: StringProperty = SimpleStringProperty(this, "supportedApiVersionsValueLabel", "N/A")

    val devices: SetProperty<Device> = SimpleSetProperty(this, "devices", uiThreadAwareObservableSet(observableSet(HashSet())))
}
