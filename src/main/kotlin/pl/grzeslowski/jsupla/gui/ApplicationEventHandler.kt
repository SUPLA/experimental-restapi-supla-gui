package pl.grzeslowski.jsupla.gui

import griffon.core.event.EventHandler
import griffon.exceptions.GriffonViewInitializationException
import javafx.application.Platform
import org.slf4j.LoggerFactory

class ApplicationEventHandler : EventHandler {
    private val logger = LoggerFactory.getLogger(ApplicationEventHandler::class.java)

    @Suppress("unused")
    fun onUncaughtGriffonViewInitializationException(ex: GriffonViewInitializationException) {
        logger.error("Error", ex)
        Platform.exit()
    }
}