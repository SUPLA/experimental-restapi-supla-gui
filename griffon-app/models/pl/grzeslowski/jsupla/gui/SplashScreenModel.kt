package pl.grzeslowski.jsupla.gui

import com.jfoenix.controls.JFXProgressBar
import griffon.core.artifact.GriffonModel
import griffon.metadata.ArtifactProviderFor
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.scene.Node
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonModel

@ArtifactProviderFor(GriffonModel::class)
class SplashScreenModel : AbstractGriffonModel() {
    var loadingInfo: StringProperty = SimpleStringProperty(this, "loadingInfo", "")
    var version: StringProperty = SimpleStringProperty(this, "version", "")

    // view things
    lateinit var centerBoxChildren: ObservableList<Node>
    lateinit var progressBar: JFXProgressBar
}