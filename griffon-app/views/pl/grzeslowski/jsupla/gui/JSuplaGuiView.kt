package pl.grzeslowski.jsupla.gui

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javafx.collections.SetChangeListener
import javafx.fxml.FXML
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.Window
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView
import pl.grzeslowski.jsupla.api.generated.model.Device
import java.util.stream.Collectors
import javax.annotation.Nonnull


@ArtifactProviderFor(GriffonView::class)
class JSuplaGuiView : AbstractJavaFXGriffonView() {
    @set:[MVCMember Nonnull]
    lateinit var model: JSuplaGuiModel
    @set:[MVCMember Nonnull]
    lateinit var controller: JSuplaGuiController

    lateinit private @FXML
    var addressValueLabel: Label
    lateinit private @FXML
    var cloudVersionValueLabel: Label
    lateinit private @FXML
    var apiVersionValueLabel: Label
    lateinit private @FXML
    var supportedApiVersionsValueLabel: Label

    lateinit private @FXML
    var deviceList: VBox

    override fun initUI() {
        val stage: Stage = application.createApplicationContainer(mapOf()) as Stage
        stage.title = application.configuration.getAsString("application.title")
        stage.scene = _init()
        application.getWindowManager<Window>().attach("mainWindow", stage)
    }

    private fun _init(): Scene {
        val scene: Scene = Scene(Group())
        scene.fill = Color.WHITE

        val node = loadFromFXML()
        initServerInfoLabels()
        initDeviceList()
        (scene.root as Group).children.addAll(node)
        connectActions(node, controller)
        connectMessageSource(node)
        return scene
    }

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
}
