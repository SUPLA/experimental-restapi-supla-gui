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
        deviceList.layoutMode = JFXMasonryPane.LayoutMode.BIN_PACKING
    }

    private fun initScroll() {
//        val button = JFXButton("")
//        val arrow = SVGGlyph(0,
//                "FULLSCREEN",
//                "M402.746 877.254l-320-320c-24.994-24.992-24.994-65.516 0-90.51l320-320c24.994-24.992 65.516-24.992 90.51 0 24.994 24.994 "
//                        + "24.994 65.516 0 90.51l-210.746 210.746h613.49c35.346 0 64 28.654 64 64s-28.654 64-64 64h-613.49l210.746 210.746c12.496 "
//                        + "12.496 18.744 28.876 18.744 45.254s-6.248 32.758-18.744 45.254c-24.994 24.994-65.516 24.994-90.51 0z",
//                Color.WHITE)
//        arrow.setSize(20.0, 16.0)
//        button.graphic = arrow
//        button.ripplerFill = Color.WHITE
//        scroll.getTopBar().getChildren().add(button)

        val title = Label("Supla GUI")
        scroll.getBottomBar().getChildren().add(title)
        title.style = "-fx-text-fill:WHITE; -fx-font-size: 40;"
        JFXScrollPane.smoothScrolling(scroll.getChildren().get(0) as ScrollPane)

        StackPane.setMargin(title, Insets(0.0, 0.0, 0.0, 80.0))
        StackPane.setAlignment(title, Pos.CENTER_LEFT)
//        StackPane.setAlignment(button, Pos.CENTER_LEFT)
//        StackPane.setMargin(button, Insets(0.0, 0.0, 0.0, 20.0))
    }
}
