package pl.grzeslowski.jsupla.gui

import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import javax.annotation.PreDestroy

abstract class AbstractController : AbstractGriffonController(), AutoCloseable {
    override fun mvcGroupInit(args: MutableMap<String, Any>) {
        application.eventRouter.addEventListener("WindowShown") { eventArgs ->
            val windowName = findWindowNameInEventArgs(eventArgs)
            if (windowName() == windowName) {
                initOutsideUi()
            }
        }
        application.eventRouter.addEventListener("WindowHidden") { eventArgs ->
            val windowName = findWindowNameInEventArgs(eventArgs)
            if (windowName() == windowName) {
                close()
            }
        }
    }

    private fun findWindowNameInEventArgs(args2: Array<Any?>) =
            if (args2.isNotEmpty()) {
                args2[0]?.toString()
            } else {
                null
            }

    protected abstract fun windowName(): String

    protected abstract fun initOutsideUi()

    @PreDestroy
    override fun close() {
        // empty
    }
}