package pl.grzeslowski.jsupla.gui

import com.jfoenix.controls.JFXToggleButton
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.collections.SetChangeListener
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Pane
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(JSuplaGuiView::class.java)

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
    private lateinit var deviceList: Pane
    @FXML
    private lateinit var scroll: ScrollPane

    // misc
    @FXML
    private lateinit var themeToggle: JFXToggleButton


    override fun internalInit(): Scene {
        val node: Parent = loadParentFxml()
        initServerInfoLabels()
        initDeviceList()
        initThemeToggle()
        connectActions(node, controller)
        connectMessageSource(node)


//        node.widthProperty().addListener { _, _, _ ->print("node:width") }
//        node.heightProperty().addListener { _, _, _ ->print("node:height")}
//        scroll.widthProperty().addListener { _, _, _ ->print("scroll:width") }
//        scroll.heightProperty().addListener { _, _, _ ->print("scroll:height")}
//        deviceList.widthProperty().addListener { _, _, _ ->print("list:width") }
//        deviceList.heightProperty().addListener { _, _, _ ->print("list:height")}


//        scroll.prefWidthProperty().bind(node.widthProperty())
//        scroll.maxWidthProperty().bind(node.widthProperty())
//        deviceList.prefWidthProperty().bind(scroll.widthProperty())
//        deviceList.maxWidthProperty().bind(scroll.widthProperty())
//        deviceList.prefWrapLengthProperty().bind(scroll.widthProperty())
//        deviceList.prefHeightProperty().bind(scroll.heightProperty())

        return Scene(node)
    }

//    private fun print(event: String) {
//        logger.info("{}> node: {}x{}, scroll: {}x{}, list: {}x{}",
//                event,
//                node.width, node.height,
//                scroll.width, scroll.height,
//                deviceList.width, deviceList.height)
//    }

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
