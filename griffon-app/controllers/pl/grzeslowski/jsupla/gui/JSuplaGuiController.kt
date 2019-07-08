package pl.grzeslowski.jsupla.gui

import griffon.core.artifact.GriffonController
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.stage.Window
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import org.slf4j.LoggerFactory
import pl.grzeslowski.jsupla.gui.api.DeviceApi
import pl.grzeslowski.jsupla.gui.api.ServerApi
import pl.grzeslowski.jsupla.gui.preferences.PreferencesKeys
import pl.grzeslowski.jsupla.gui.preferences.PreferencesService
import pl.grzeslowski.jsupla.gui.preferences.TokenService
import pl.grzeslowski.jsupla.gui.thread.ThreadService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import javax.annotation.Nonnull
import javax.annotation.PreDestroy
import javax.inject.Inject
import javax.inject.Provider

@ArtifactProviderFor(GriffonController::class)
class JSuplaGuiController @Inject constructor(
        private val preferencesService: PreferencesService,
        private val tokenService: TokenService,
        private val threadService: ThreadService,
        private val serverApiProvider: Provider<ServerApi>,
        private val deviceApiProvider: Provider<DeviceApi>) : AbstractGriffonController(), AutoCloseable {
    private val logger = LoggerFactory.getLogger(JSuplaGuiController::class.java)
    private var updateDeviceFuture: ScheduledFuture<*>? = null

    @set:[MVCMember Nonnull]
    lateinit var model: JSuplaGuiModel

    override fun mvcGroupInit(args: Map<String, Any>) {
        init()
        application.eventRouter.addEventListener("NewToken") { init() }
    }

    private fun init() {
        val token = tokenService.read()
        if (token == null || token.isBlank()) {
            log.debug("Token is not yet stored")
            createMVCGroup("login")
            application.getWindowManager<Window>().show("loginWindow")
        } else {
            initServerInfo()
            initDevices()
        }
    }

    private fun initServerInfo() {
        log.trace("initServerInfo")
        val serverInfo = serverApiProvider.get().findServerInfo()
        model.address.value = serverInfo.address
        model.cloudVersion.value = serverInfo.cloudVersion
        model.apiVersion.value = serverInfo.apiVersion
        model.supportedApiVersions.value = serverInfo.supportedVersions.joinToString(", ", "[", "]")
    }

    private fun initDevices() {
        log.trace("initDevices")
        updateDeviceFuture = threadService.scheduleEvery(this::updateDevices, 0, preferencesService.readLongWithDefault(PreferencesKeys.refreshTime, 30), TimeUnit.SECONDS)
    }

    private fun updateDevices() {
        try {
            logger.debug("Updating all devices")
            val devices = deviceApiProvider.get().findAllDevice()
            runInsideUISync {
                model.devices.clear()
                model.devices.addAll(devices)
            }
        } catch (ex: Exception) {
            logger.error("Cannot fetch devices!", ex)
        }
    }

    @PreDestroy
    override fun close() {
        try {
            updateDeviceFuture?.cancel(false)
            updateDeviceFuture = null
        } catch (ex: Exception) {
            logger.error("Cannot close jSuplaGuiController!", ex)
        }
    }
}
