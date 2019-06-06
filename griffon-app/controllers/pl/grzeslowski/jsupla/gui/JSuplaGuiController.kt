package pl.grzeslowski.jsupla.gui

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import griffon.transform.Threading
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import javax.annotation.Nonnull

@ArtifactProviderFor(GriffonController::class)
class JSuplaGuiController : AbstractGriffonController() {
    @set:[MVCMember Nonnull]
    lateinit var model: JSuplaGuiModel

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    fun click() {
        val count = Integer.parseInt(model.clickCount)
        model.clickCount = (count + 1).toString()
    }
}