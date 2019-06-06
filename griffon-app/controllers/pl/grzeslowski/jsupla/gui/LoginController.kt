package pl.grzeslowski.jsupla.gui

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading
import javafx.stage.Window
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import org.slf4j.LoggerFactory
import pl.grzeslowski.jsupla.gui.preferences.PreferencesKeys
import pl.grzeslowski.jsupla.gui.preferences.PreferencesService
import javax.annotation.Nonnull
import javax.inject.Inject

@ArtifactProviderFor(GriffonController::class)
class LoginController @Inject constructor(private val preferencesService: PreferencesService) : AbstractGriffonController() {
    private val logger = LoggerFactory.getLogger(LoginController::class.java)

    @set:[MVCMember Nonnull]
    lateinit var model: LoginModel

    @Suppress("unused") // FXML
    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    fun click() {
        val token = model.token.get()
        if (token != null && token.isNotBlank()) {
            logger.trace("Logging for token {}", token)
            preferencesService.write(PreferencesKeys.token, token)
            application.getWindowManager<Window>().hide("loginWindow")
            application.eventRouter.publishEvent("NewToken")
        }
    }
}