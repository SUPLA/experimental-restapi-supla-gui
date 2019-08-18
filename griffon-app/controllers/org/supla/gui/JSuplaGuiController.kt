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

import griffon.core.artifact.GriffonController
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.stage.Window
import org.slf4j.LoggerFactory
import org.supla.gui.api.DeviceApi
import org.supla.gui.db.Database
import org.supla.gui.preferences.PreferencesKeys
import org.supla.gui.preferences.PreferencesService
import org.supla.gui.thread.ThreadService
import org.supla.gui.uidevice.*
import pl.grzeslowski.jsupla.api.ApiException
import pl.grzeslowski.jsupla.api.device.Device
import pl.grzeslowski.jsupla.api.serverinfo.ServerInfo
import java.util.*
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS
import java.util.concurrent.atomic.AtomicBoolean
import java.util.stream.Collectors
import javax.annotation.Nonnull
import javax.inject.Inject
import javax.inject.Provider

@ArtifactProviderFor(GriffonController::class)
class JSuplaGuiController @Inject constructor(
        private val threadService: ThreadService,
        private val deviceApiProvider: Provider<DeviceApi>,
        private val database: Database,
        private val actionExecutor: ActionExecutor,
        preferencesService: PreferencesService) : AbstractController(), AutoCloseable {
    private val logger = LoggerFactory.getLogger(JSuplaGuiController::class.java)
    private var updateDeviceFuture: ScheduledFuture<*>? = null
    private val updateDeviceLock = AtomicBoolean(false)
    private val updatePeriod = SECONDS.toMillis(preferencesService.readLongWithDefault(PreferencesKeys.refreshTime, 30))
    private val updateCheckPeriod = SECONDS.toMillis(preferencesService.readLongWithDefault(PreferencesKeys.refreshCheckTime, 10))
    private var lastUpdate: Long = 0

    @set:[MVCMember Nonnull]
    lateinit var model: JSuplaGuiModel

    override fun windowName(): String = "mainWindow"

    override fun initOutsideUi() {
        initServerInfo()
        initDevices()
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

        updateDeviceFuture = threadService.scheduleEvery(this::updateDevices, updateCheckPeriod, updateCheckPeriod, MILLISECONDS)
        model.listenOnRefresh {
            lastUpdate = 0
            updateDevices()
        }
    }

    private fun updateDevices() {
        if (now() < lastUpdate + updatePeriod) {
            logger.debug("Update period did not pass ; abort")
            logger.trace("{} < {} = {} + {}", now(), lastUpdate + updatePeriod, lastUpdate, updatePeriod)
            return
        }
        if (updateDeviceLock.getAndSet(true)) {
            logger.debug("Devices are already updating ; abort")
            return
        }
        try {
            logger.debug("Updating all devices")
            lastUpdate = now()
            val deviceApi = deviceApiProvider.get()
            val devices = database.loadAll("devices", Device::class)
            model.devices.forEach { it.updating.value = true }
            for (device in devices) {
                for (uiDevice in model.devices) {
                    try {
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
                    } finally {
                        uiDevice.updating.value = false
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
            model.devices.forEach { it.updating.value = false }
        }
    }

    private fun openSplashScreen() {
        runInsideUIAsync {
            application.getWindowManager<Window>().hide("mainWindow")
            application.getWindowManager<Window>().show("splashScreenWindow")
        }
    }

    private fun now() = Date().time

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
