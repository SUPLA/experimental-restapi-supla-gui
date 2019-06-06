package pl.grzeslowski.jsupla.gui

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.TextField
import pl.grzeslowski.jsupla.gui.preferences.PreferencesService
import javax.annotation.Nonnull
import javax.inject.Inject

@ArtifactProviderFor(GriffonView::class)
class LoginView @Inject constructor(preferencesService: PreferencesService) : AbstractView(preferencesService) {
    @set:[MVCMember Nonnull]
    lateinit var model: LoginModel
    @set:[MVCMember Nonnull]
    lateinit var controller: LoginController

    lateinit private @FXML
    var tokenTextField: TextField

    override fun windowName() = "loginWindow"

    override fun internalInit(): Scene {
        val node: Parent = loadParentFxml()
        model.token.bindBidirectional(tokenTextField.textProperty())
        connectActions(node, controller)
        connectMessageSource(node)
        return Scene(node)
    }
}