package pl.grzeslowski.jsupla.gui

import griffon.core.artifact.GriffonController
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.stage.Window
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import org.slf4j.LoggerFactory
import pl.grzeslowski.jsupla.api.ApiException
import pl.grzeslowski.jsupla.api.device.Device
import pl.grzeslowski.jsupla.api.serverinfo.ServerInfo
import pl.grzeslowski.jsupla.gui.api.DeviceApi
import pl.grzeslowski.jsupla.gui.db.Database
import pl.grzeslowski.jsupla.gui.preferences.PreferencesKeys
import pl.grzeslowski.jsupla.gui.preferences.PreferencesService
import pl.grzeslowski.jsupla.gui.thread.ThreadService
import pl.grzeslowski.jsupla.gui.uidevice.*
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.stream.Collectors
import javax.annotation.Nonnull
import javax.annotation.PreDestroy
import javax.inject.Inject
import javax.inject.Provider

@ArtifactProviderFor(GriffonController::class)
class JSuplaGuiController @Inject constructor(
        private val preferencesService: PreferencesService,
        private val threadService: ThreadService,
        private val deviceApiProvider: Provider<DeviceApi>,
        private val database: Database,
        private val actionExecutor: ActionExecutor) : AbstractGriffonController(), AutoCloseable {
    private val logger = LoggerFactory.getLogger(JSuplaGuiController::class.java)
    private var updateDeviceFuture: ScheduledFuture<*>? = null
    private val updateDeviceLock = AtomicBoolean(false)

    @set:[MVCMember Nonnull]
    lateinit var model: JSuplaGuiModel

    override fun mvcGroupInit(args: Map<String, Any>) {
        application.eventRouter.addEventListener("WindowShown", { args2 ->
            val windowName = if (args2.isNotEmpty()) {
                args2[0]?.toString()
            } else {
                null
            }
            if (windowName != null && windowName == "mainWindow") {
                init()
            }
        })
        application.eventRouter.addEventListener("WindowHidden", { args2 ->
            val windowName = if (args2.isNotEmpty()) {
                args2[0]?.toString()
            } else {
                null
            }
            if (windowName != null && windowName == "mainWindow") {
                close()
            }
        })
    }

    private fun init() {
        runOutsideUIAsync {
            initServerInfo()
            initDevices()
        }
    }

    private fun initServerInfo() {
        log.trace("initServerInfo")
        val serverInfo = database.load("serverInfos", ServerInfo::class)
        runInsideUIAsync {
            model.address.value = serverInfo.address
            model.cloudVersion.value = serverInfo.cloudVersion
            model.apiVersion.value = serverInfo.apiVersion
            model.supportedApiVersions.value = serverInfo.supportedVersions.joinToString(", ", "[", "]")
        }
    }

    private fun initDevices() {
        log.trace("initDevices")
        val devices = database.loadAll("devices", Device::class)
                .stream()
                .map { device -> UiDevice(device) }
                .collect(Collectors.toList())
        model.devices.clear()
        model.devices.addAll(devices)

        val uiStates: Map<UiChannel, UiState> = devices.stream()
                .map { it.channels }
                .flatMap { it.stream() }
                .collect(Collectors.toMap({ it }, { it.state }))
        actionExecutor.listenOnStates(uiStates)

        val period = preferencesService.readLongWithDefault(PreferencesKeys.refreshTime, 30)
        updateDeviceFuture = threadService.scheduleEvery(this::updateDevices, period, period, TimeUnit.SECONDS)
        model.listenOnRefresh { updateDevices() }
    }

    private fun updateDevices() {
        if (updateDeviceLock.getAndSet(true)) {
            logger.debug("Devices are already updating ; abort")
            return
        }
        try {
            logger.debug("Updating all devices")
            val deviceApi = deviceApiProvider.get()
            val devices = database.loadAll("devices", Device::class)
            for (device in devices) {
                for (uiDevice in model.devices) {
                    if (device.id == uiDevice.id) {
                        val refreshedDevice = deviceApi.findDevice(device.id)
                        runInsideUISync {
                            uiDevice.name.value = refreshedDevice.name
                            uiDevice.comment.value = refreshedDevice.comment
                        }
                        for (channel in refreshedDevice.channels) {
                            for (uiChannel in uiDevice.channels) {
                                if (channel.id == uiChannel.id) {
                                    runInsideUISync {
                                        uiChannel.caption.value = channel.caption
                                        uiChannel.connected.value = channel.isConnected
                                        uiChannel.state.updateByApi {
                                            updateState(uiChannel.state, channel.state)
                                        }
                                    }
                                    break
                                }
                            }
                        }
                        break
                    }
                }
            }
        } catch (e: ApiException) {
            logger.error("Error while updating devices", e)
            openSplashScreen()
        } catch (e: java.lang.Exception) {
            logger.error("Error while updating devices", e)
        } finally {
            updateDeviceLock.set(false)
        }
    }

    private fun openSplashScreen() {
        runInsideUIAsync {
            application.getWindowManager<Window>().hide("mainWindow")
            application.getWindowManager<Window>().show("splashScreenWindow")
        }
    }

    @PreDestroy
    override fun close() {
        try {
            logger.debug("Closing JSuplaController")
            model.clearOnRefresh()
            updateDeviceFuture?.cancel(false)
            updateDeviceFuture = null
        } catch (ex: Exception) {
            logger.error("Cannot close jSuplaGuiController!", ex)
        }
    }
}
