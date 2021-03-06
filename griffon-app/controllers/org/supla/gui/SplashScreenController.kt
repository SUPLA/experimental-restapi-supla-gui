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

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextField
import griffon.core.artifact.GriffonController
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.text.TextAlignment
import javafx.stage.Window
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon
import org.slf4j.LoggerFactory
import org.supla.gui.api.DeviceApi
import org.supla.gui.api.ServerApi
import org.supla.gui.db.Database
import org.supla.gui.preferences.TokenService
import org.supla.gui.validator.TokenValidator
import pl.grzeslowski.jsupla.api.ApiException
import java.util.stream.Stream
import javax.annotation.Nonnull
import javax.inject.Inject
import javax.inject.Provider

@ArtifactProviderFor(GriffonController::class)
class SplashScreenController @Inject constructor(
        private val tokenService: TokenService,
        private val serverApiProvider: Provider<ServerApi>,
        private val deviceApiProvider: Provider<DeviceApi>,
        private val database: Database) : AbstractController() {
    private val logger = LoggerFactory.getLogger(SplashScreenController::class.java)
    @set:[MVCMember Nonnull]
    lateinit var model: SplashScreenModel

    override fun windowName(): String = "splashScreenWindow"

    override fun initOutsideUi() {
        runOutsideUIAsync {
            runWithExceptionCheck { initNoExceptionControl() }
        }
    }

    private fun showError(msg: String, ex: Exception) {
        val loadingInfoMessage = Stream.of(ex.localizedMessage, ex.message, "Unknown")
                .filter { it != null && it.isBlank().not() }
                .findFirst()
                .get()
        updateLoadingInfoRaw(loadingInfoMessage)
        val addLabel = Label(findMessage(msg))
        val refreshButton = JFXButton(findMessage("splashScreen.errorRefreshButton"))
        refreshButton.onAction = EventHandler { initOutsideUi() }
        val closeButton = JFXButton(findMessage("splashScreen.errorCloseButton"))
        closeButton.onAction = EventHandler { application.shutdown() }
        val buttonLayout = HBox(3.0)
        buttonLayout.alignment = Pos.CENTER
        HBox.setHgrow(refreshButton, Priority.ALWAYS)
        HBox.setHgrow(closeButton, Priority.ALWAYS)
        buttonLayout.children.addAll(refreshButton, closeButton)
        runInsideUISync {
            model.centerBoxChildren.clear()
            model.centerBoxChildren.addAll(
                    addLabel,
                    buttonLayout
            )
        }
    }

    private fun runWithExceptionCheck(runnable: () -> Unit) {
        try {
            runnable()
        } catch (ex: ApiException) {
            logger.error("API exception occurred!", ex)
            showError("splashScreen.apiError", ex)
        } catch (ex: Exception) {
            logger.error("Generic exception occurred!", ex)
            showError("splashScreen.genericError", ex)
        }
    }

    private fun initNoExceptionControl() {
        runInsideUISync {
            model.centerBoxChildren.clear()
            model.centerBoxChildren.add(model.progressBar)
            updateLoadingInfo("splashScreen.loading")
        }
        val token = tokenService.read()
        if (token == null || token.isBlank()) {
            logger.trace("Missing token")
            updateLoadingInfo("splashScreen.missingToken")
            val addLabel = Label(findMessage("splashScreen.addLabel"))

            val addTextField = JFXTextField()
            val validator = TokenValidator()
            validator.icon = FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE)
            addTextField.validators.add(validator)
            addTextField.validators.add(validator)
            addTextField.focusedProperty().addListener { _, _, newVal ->
                if (newVal.not()) {
                    addTextField.validate()
                }
            }

            val addButton = JFXButton(findMessage("splashScreen.addButton"))
            addButton.onAction = EventHandler {
                if (addTextField.validate()) {
                    tokenService.write(addTextField.text)
                    initOutsideUi()
                }
            }
            runInsideUISync {
                model.centerBoxChildren.clear()
                model.centerBoxChildren.addAll(
                        addLabel,
                        addTextField,
                        addButton
                )
            }
            return
        }
        checkServerInfo()
    }

    private fun checkServerInfo() {
        updateLoadingInfo("splashScreen.checkingServer")
        val serverApi = serverApiProvider.get()
        val serverInfo = serverApi.findServerInfo()
        val apiVersion = serverInfo.apiVersion
        val cloudVersion = serverInfo.cloudVersion
        val supportedVersions = serverInfo.supportedVersions
        updateLoadingInfo("splashScreen.serverInfo",
                apiVersion,
                cloudVersion,
                supportedVersions.joinToString(", ", "[", "]"))
        if (supportedVersions.contains(apiVersion).not()) {
            addMainLabel("splashScreen.notSupportedVersion")
        } else {
            database.clear("serverInfos")
            database.save("serverInfos", serverInfo)
            downloadDevices()
        }
    }

    private fun downloadDevices() {
        updateLoadingInfo("splashScreen.downloadingDevices")
        val deviceApi = deviceApiProvider.get()
        val allDevice = deviceApi.findAllDevice()
        if (allDevice.isEmpty()) {
            updateLoadingInfo("splashScreen.downloadingDevicesFailed")
            addMainLabel("splashScreen.noDevices")
            val refresh = JFXButton(findMessage("splashScreen.noDevicesRefreshButton"))
            runInsideUISync {
                refresh.onAction = EventHandler { initOutsideUi() }
                model.centerBoxChildren.addAll(refresh)
            }
        } else {
            updateLoadingInfo("splashScreen.go")
            database.clear("devices")
            database.saveAll("devices", allDevice)
            startMainWindow()
        }
    }

    private fun updateLoadingInfo(key: String, vararg params: String) {
        updateLoadingInfoRaw(application.messageSource.getMessage(key, params.asList()))
    }

    private fun updateLoadingInfoRaw(msg: String) {
        runInsideUISync { model.loadingInfo.value = msg }
    }

    private fun startMainWindow() {
        runInsideUISync {
            application.getWindowManager<Window>().show("mainWindow")
            application.getWindowManager<Window>().hide("splashScreenWindow")
        }
    }

    private fun findMessage(key: String): String = application.messageSource.getMessage(key)

    private fun addMainLabel(messageKey: String) {
        runInsideUISync {
            val notSupportedLabel = Label(findMessage(messageKey))
            notSupportedLabel.isWrapText = true
            notSupportedLabel.textAlignment = TextAlignment.CENTER
            model.centerBoxChildren.clear()
            model.centerBoxChildren.addAll(notSupportedLabel)
        }
    }
}
