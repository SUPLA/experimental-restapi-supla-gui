package pl.grzeslowski.jsupla.gui

import griffon.core.artifact.GriffonController
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.stage.Window
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import pl.grzeslowski.jsupla.gui.api.DeviceApi
import pl.grzeslowski.jsupla.gui.api.ServerApi
import pl.grzeslowski.jsupla.gui.preferences.PreferencesKeys
import pl.grzeslowski.jsupla.gui.preferences.PreferencesService
import javax.annotation.Nonnull
import javax.inject.Inject
import javax.inject.Provider

@ArtifactProviderFor(GriffonController::class)
class JSuplaGuiController @Inject constructor(
        private val preferencesService: PreferencesService,
        private val serverApiProvider: Provider<ServerApi>,
        private val deviceApiProvider: Provider<DeviceApi>) : AbstractGriffonController() {
    @set:[MVCMember Nonnull]
    lateinit var model: JSuplaGuiModel

    override fun mvcGroupInit(args: Map<String, Any>) {
        init()
        application.eventRouter.addEventListener("NewToken") { init() }
    }

    fun init() {
        val token = preferencesService.read(PreferencesKeys.token)
        if (token == null || token.isBlank()) {
            log.debug("Token is not yet stored")
            createMVCGroup("login")
            application.getWindowManager<Window>().show("loginWindow")
        } else {
            initServerInfo()
            initDevices()
            initThemeToggle()
        }
    }

    private fun initServerInfo() {
        log.trace("initServerInfo")
        val serverInfo = serverApiProvider.get().findServerInfo()
        model.address.value = serverInfo.address
        model.cloudVersion.value = serverInfo.cloudVersion
        model.apiVersion.value = serverInfo.apiVersion
        model.supportedApiVersions.value = serverInfo.supportedApiVersions.joinToString(", ", "[", "]")
    }

    private fun initDevices() {
        log.trace("initDevices")
        val devices = deviceApiProvider.get().findAllDevice()
        model.devices.addAll(devices)
    }

    private fun initThemeToggle() {
        model.darkTheme.addListener { _, _, darkTheme ->
            preferencesService.write(PreferencesKeys.theme, darkTheme)
            application.eventRouter.publishEvent("ThemeChanged")
        }
    }
}
