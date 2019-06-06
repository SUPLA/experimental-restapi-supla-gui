package pl.grzeslowski.jsupla.gui

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import org.slf4j.LoggerFactory
import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonController::class)
class LoginController : AbstractGriffonController() {
    private val logger = LoggerFactory.getLogger(LoginController::class.java)

    @set:[MVCMember Nonnull]
    lateinit var model: LoginModel

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    fun click() {
        logger.info("Logging for token {}", model.token.get())
    }
}