// Copyright (C) AC SOFTWARE SP. Z O.O.
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
package org.supla.gui

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import griffon.core.GriffonApplication
import griffon.core.event.EventHandler
import javafx.application.Platform
import org.slf4j.LoggerFactory
import org.supla.gui.preferences.PreferencesKeys
import org.supla.gui.preferences.PreferencesService
import javax.inject.Inject


class ApplicationEventHandler @Inject constructor(
        application: GriffonApplication,
        private val preferencesService: PreferencesService) : EventHandler {
    private val logger = LoggerFactory.getLogger(ApplicationEventHandler::class.java)

    init {
        val debugMode = preferencesService.readBoolWithDefault(PreferencesKeys.debugMode, false)
        logger.info("debug mode = $debugMode")
        application.eventRouter.addEventListener("BootstrapEnd") { onBootstrapEnd() }
        application.eventRouter.addEventListener("UncaughtGriffonViewInitializationException") { args -> onUncaughtGriffonViewInitializationException(args) }
    }


    private fun onUncaughtGriffonViewInitializationException(args: Array<Any>) {
        if (args.isNotEmpty()) {
            val first = args[0]
            if (first is Exception) {
                logger.error("Error", first)
            } else {
                logger.error("Error: {}", args[0])
            }
        }
        Platform.exit()
    }

    private fun onBootstrapEnd() {
        logger.debug("onBootstrapEnd")
        val debugMode = preferencesService.readBoolWithDefault(PreferencesKeys.debugMode, false)
        if (debugMode) {
            logger.info("Setting debug mode to `true`")
            setLevel(Logger.ROOT_LOGGER_NAME, Level.DEBUG)
            setLevel("org.supla.gui", Level.TRACE)
            setLevel("pl.grzeslowski", Level.TRACE)
        }
    }

    private fun setLevel(logger: String, level: Level) {
        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        val rootLogger = loggerContext.getLogger(logger)
        (rootLogger as Logger).level = level
    }
}