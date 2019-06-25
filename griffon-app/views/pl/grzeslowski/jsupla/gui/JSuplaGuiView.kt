package pl.grzeslowski.jsupla.gui

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.collections.SetChangeListener
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.FlowPane
import org.controlsfx.control.ToggleSwitch
import pl.grzeslowski.jsupla.gui.preferences.PreferencesKeys
import pl.grzeslowski.jsupla.gui.preferences.PreferencesService
import pl.grzeslowski.jsupla.gui.view.ViewBuilder
import java.util.stream.Collectors
import javax.annotation.Nonnull
import javax.inject.Inject


@ArtifactProviderFor(GriffonView::class)
class JSuplaGuiView @Inject constructor(
        preferencesService: PreferencesService,
        private val viewBuilder: ViewBuilder) : AbstractView(preferencesService) {
    @set:[MVCMember Nonnull]
    lateinit var model: JSuplaGuiModel
    @set:[MVCMember Nonnull]
    lateinit var controller: JSuplaGuiController

    // server info
    @FXML
    private lateinit var addressValueLabel: Label
    @FXML
    private lateinit var cloudVersionValueLabel: Label
    @FXML
    private lateinit var apiVersionValueLabel: Label
    @FXML
    private lateinit var supportedApiVersionsValueLabel: Label

    // devices
    @FXML
    private lateinit var deviceList: FlowPane

    // misc
    @FXML
    private lateinit var themeToggle: ToggleSwitch


    override fun internalInit(): Scene {
        val node: Parent = loadParentFxml()
        initServerInfoLabels()
        initDeviceList()
        initThemeToggle()
        connectActions(node, controller)
        connectMessageSource(node)
        return Scene(node)
    }

    override fun windowName() = "mainWindow"

    private fun initServerInfoLabels() {
        model.address.bindBidirectional(addressValueLabel.textProperty())
        model.cloudVersion.bindBidirectional(cloudVersionValueLabel.textProperty())
        model.apiVersion.bindBidirectional(apiVersionValueLabel.textProperty())
        model.supportedApiVersions.bindBidirectional(supportedApiVersionsValueLabel.textProperty())
    }

    private fun initDeviceList() {
        model.devices.addListener(SetChangeListener { change ->
            log.trace("Updating device set")
            deviceList.children.clear()
            val nodes = change.set
                    .stream()
                    .map { device -> viewBuilder.buildViewForDevice(device) }
                    .collect(Collectors.toList())
            deviceList.children.addAll(nodes)
        })
    }

    private fun initThemeToggle() {
        themeToggle.isSelected = preferencesService.readBoolWithDefault(PreferencesKeys.theme, false)
        model.darkTheme.bindBidirectional(themeToggle.selectedProperty())
    }
}
