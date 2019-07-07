@file:Suppress("MemberVisibilityCanBePrivate")

package pl.grzeslowski.jsupla.gui

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.stage.Window
import jfxtras.styles.jmetro8.JMetro
import jfxtras.styles.jmetro8.JMetro.Style.DARK
import jfxtras.styles.jmetro8.JMetro.Style.LIGHT
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView
import pl.grzeslowski.jsupla.gui.preferences.PreferencesKeys
import pl.grzeslowski.jsupla.gui.preferences.PreferencesService

abstract class AbstractView(protected val preferencesService: PreferencesService) : AbstractJavaFXGriffonView() {
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
        application.getWindowManager<Window>().attach(windowName(), stage)
    }

    protected fun applyTheme(scene: Scene) {
        val (add, remove) = findStyle()
        scene.stylesheets.remove(JMetro::class.java.getResource(remove).toExternalForm())
        JMetro(add).applyTheme(scene)
        scene.stylesheets.add("css/main-light.css")
    }

    private fun findStyle(): Pair<JMetro.Style, String> {
        val isDark = preferencesService.readBoolWithDefault(PreferencesKeys.theme, false)
        return if (isDark) Pair(DARK, "JMetroLightTheme.css") else Pair(LIGHT, "JMetroDarkTheme.css")
    }

    protected abstract fun internalInit(): Scene

    protected abstract fun windowName(): String
}