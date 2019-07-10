package pl.grzeslowski.jsupla.gui

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXTextField
import griffon.core.artifact.GriffonController
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.text.TextAlignment
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid
import org.kordamp.ikonli.javafx.FontIcon
import org.slf4j.LoggerFactory
import pl.grzeslowski.jsupla.gui.api.DeviceApi
import pl.grzeslowski.jsupla.gui.api.ServerApi
import pl.grzeslowski.jsupla.gui.preferences.TokenService
import pl.grzeslowski.jsupla.gui.validator.TokenValidator
import javax.annotation.Nonnull
import javax.inject.Inject
import javax.inject.Provider

@ArtifactProviderFor(GriffonController::class)
class SplashScreenController @Inject constructor(
        private val tokenService: TokenService,
        private val serverApiProvider: Provider<ServerApi>,
        private val deviceApiProvider: Provider<DeviceApi>) : AbstractGriffonController() {
    private val logger = LoggerFactory.getLogger(SplashScreenController::class.java)
    @set:[MVCMember Nonnull]
    lateinit var model: SplashScreenModel

    override fun mvcGroupInit(args: Map<String, Any>) {
        init()
    }

    private fun init() {
        model.centerBoxChildren.clear()
        model.centerBoxChildren.add(model.progressBar)
        updateLoadingInfo("splashScreen.loading")
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
                    init()
                }
            }
            model.centerBoxChildren.clear()
            model.centerBoxChildren.addAll(
                    addLabel,
                    addTextField,
                    addButton
            )
            return;
        }
        runOutsideUIAsync {
            checkServerInfo()
        }
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
            runInsideUIAsync {
                addMainLabel("splashScreen.notSupportedVersion")
            }
        } else {
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
            runInsideUIAsync {
                refresh.onAction = EventHandler { init() }
                model.centerBoxChildren.addAll(refresh)
            }
        } else {
            updateLoadingInfo("splashScreen.go")
        }
    }

    private fun updateLoadingInfo(key: String, vararg params: String) {
        runInsideUISync {
            model.loadingInfo.value = application.messageSource.getMessage(key, params.asList())
        }
    }

    private fun findMessage(key: String): String = application.messageSource.getMessage(key)

    private fun addMainLabel(messageKey: String) {
        runInsideUIAsync {
            val notSupportedLabel = Label(findMessage(messageKey))
            notSupportedLabel.isWrapText = true
            notSupportedLabel.textAlignment = TextAlignment.CENTER
            model.centerBoxChildren.clear()
            model.centerBoxChildren.addAll(notSupportedLabel)
        }
    }

}
