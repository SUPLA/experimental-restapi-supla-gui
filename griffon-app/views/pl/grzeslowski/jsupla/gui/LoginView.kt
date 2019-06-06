package pl.grzeslowski.jsupla.gui

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.fxml.FXML
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.Window
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView
import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonView::class)
class LoginView : AbstractJavaFXGriffonView() {
    @set:[MVCMember Nonnull]
    lateinit var model: LoginModel
    @set:[MVCMember Nonnull]
    lateinit var controller: LoginController

    lateinit private @FXML
    var tokenTextField: TextField

    override fun initUI() {
        val stage: Stage = application.createApplicationContainer(mapOf()) as Stage
        stage.title = application.configuration.getAsString("login.title")
        stage.scene = internalInit()
        application.getWindowManager<Window>().attach("loginWindow", stage)
    }

    private fun internalInit(): Scene {
        val scene: Scene = Scene(Group())
        scene.fill = Color.WHITE

        val node = loadFromFXML()
        model.token.bindBidirectional(tokenTextField.textProperty())
        (scene.root as Group).children.addAll(node)
        connectActions(node, controller)
        connectMessageSource(node)
        return scene
    }
}