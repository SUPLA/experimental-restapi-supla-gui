package pl.grzeslowski.jsupla.gui

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.collections.SetChangeListener
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.controlsfx.control.ToggleSwitch
import pl.grzeslowski.jsupla.api.generated.model.Device
import pl.grzeslowski.jsupla.gui.preferences.PreferencesKeys
import pl.grzeslowski.jsupla.gui.preferences.PreferencesService
import java.util.stream.Collectors
import javax.annotation.Nonnull
import javax.inject.Inject


@ArtifactProviderFor(GriffonView::class)
class JSuplaGuiView @Inject constructor(preferencesService: PreferencesService) : AbstractView(preferencesService) {
    @set:[MVCMember Nonnull]
    lateinit var model: JSuplaGuiModel
    @set:[MVCMember Nonnull]
    lateinit var controller: JSuplaGuiController

    // server info
    lateinit private @FXML
    var addressValueLabel: Label
    lateinit private @FXML
    var cloudVersionValueLabel: Label
    lateinit private @FXML
    var apiVersionValueLabel: Label
    lateinit private @FXML
    var supportedApiVersionsValueLabel: Label

    // devices
    lateinit private @FXML
    var deviceList: VBox

    // misc
    lateinit private @FXML
    var themeToggle: ToggleSwitch


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
                    .map { device -> buildDeviceNode(device) }
                    .collect(Collectors.toList())
            deviceList.children.addAll(nodes)
        })
    }

    private fun buildDeviceNode(device: Device): Node {
        val node = HBox()
        node.children.add(Label(device.name))
        return node
    }

    private fun initThemeToggle() {
        themeToggle.isSelected = preferencesService.readBoolWithDefault(PreferencesKeys.theme, false)
        model.darkTheme.bindBidirectional(themeToggle.selectedProperty())
    }
}
