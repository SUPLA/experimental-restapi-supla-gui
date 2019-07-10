package pl.grzeslowski.jsupla.gui

import com.jfoenix.controls.JFXMasonryPane
import com.jfoenix.controls.JFXScrollPane
import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.collections.SetChangeListener
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.StackPane
import org.slf4j.LoggerFactory
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
    private lateinit var deviceList: JFXMasonryPane
    @FXML
    private lateinit var scroll: JFXScrollPane

    override fun internalInit(): Scene {
        val node: Parent = loadParentFxml()
        initServerInfoLabels()
        initDeviceList()
        initScroll()
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

    private fun initScroll() {
        val title = Label("Supla GUI")
        scroll.getBottomBar().getChildren().add(title)
        title.style = "-fx-text-fill:WHITE; -fx-font-size: 40;"
        JFXScrollPane.smoothScrolling(scroll.getChildren().get(0) as ScrollPane)

        StackPane.setMargin(title, Insets(0.0, 0.0, 0.0, 80.0))
        StackPane.setAlignment(title, Pos.CENTER_LEFT)
    }
}
