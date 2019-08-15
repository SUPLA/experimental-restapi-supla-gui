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
@file:Suppress("MemberVisibilityCanBePrivate")

package pl.grzeslowski.jsupla.gui

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.Window
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView
import org.slf4j.LoggerFactory


abstract class AbstractView() : AbstractJavaFXGriffonView() {
    private val logger = LoggerFactory.getLogger(AbstractView::class.java)
    protected lateinit var scene: Scene

    protected fun loadParentFxml(): Parent = loadFromFXML() as Parent

    override fun mvcGroupInit(args: Map<String, Any>) {
        application.eventRouter.addEventListener("ThemeChanged") { applyTheme(scene) }
    }

    override fun initUI() {
        val stage: Stage = application.createApplicationContainer(mapOf()) as Stage
        stage.title = application.configuration.getAsString("application.title")
        this.scene = internalInit()
        stage.scene = scene
        applyTheme(scene)
        tweakStage(stage)
        application.getWindowManager<Window>().attach(windowName(), stage)
    }

    protected open fun tweakStage(stage: Stage) {

    }

    /**
     *  https://stackoverflow.com/a/29560563/1819402
     */
    protected fun centerWindow(stage: Stage) {
        val primScreenBounds = Screen.getPrimary().visualBounds
        stage.x = (primScreenBounds.width - stage.width) / 2
        stage.y = (primScreenBounds.height - stage.height) / 2
        if (logger.isTraceEnabled) {
            logger.trace("Centering window; Stage {}x{}, screen {}x{}, new position {}x{}",
                    stage.width, stage.height,
                    primScreenBounds.width, primScreenBounds.height,
                    stage.x, stage.y)
        }
    }


    protected fun applyTheme(scene: Scene) {
//        val (add, remove) = findStyle()
//        scene.stylesheets.remove(JMetro::class.java.getResource(remove).toExternalForm())
//        JMetro(add).applyTheme(scene)
        scene.stylesheets.add("css/jfoenix-design.css")
        scene.stylesheets.add("css/main-light.css")
        scene.stylesheets.add("css/jfoenix-fonts.css")
    }

//    private fun findStyle(): Pair<JMetro.Style, String> {
//        val isDark = preferencesService.readBoolWithDefault(PreferencesKeys.theme, false)
//        return if (isDark) Pair(DARK, "JMetroLightTheme.css") else Pair(LIGHT, "JMetroDarkTheme.css")
//    }

    protected abstract fun internalInit(): Scene

    protected abstract fun windowName(): String
}