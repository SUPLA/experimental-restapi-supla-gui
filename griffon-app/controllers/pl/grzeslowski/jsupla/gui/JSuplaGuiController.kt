package pl.grzeslowski.jsupla.gui

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading
import javafx.stage.Window
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import pl.grzeslowski.jsupla.gui.preferences.PreferencesService
import javax.annotation.Nonnull
import javax.inject.Inject

@ArtifactProviderFor(GriffonController::class)
class JSuplaGuiController @Inject constructor(private val preferencesService: PreferencesService) : AbstractGriffonController() {
    @set:[MVCMember Nonnull]
    lateinit var model: JSuplaGuiModel

    override fun mvcGroupInit(args: Map<String, Any>) {
        init()
    }

    fun init() {
        val token = preferencesService.read("token")
        if (token == null || token.isBlank()) {
            log.debug("Token is not yet stored")
            createMVCGroup("login")
            application.getWindowManager<Window>().show("loginWindow")
        }
    }

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    fun click() {
        val count = Integer.parseInt(model.clickCount)
        model.clickCount = (count + 1).toString()
    }
}
