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
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.StackPane
import org.slf4j.LoggerFactory
import pl.grzeslowski.jsupla.gui.view.ViewBuilder
import javax.annotation.Nonnull
import javax.inject.Inject


@ArtifactProviderFor(GriffonView::class)
class JSuplaGuiView @Inject constructor(private val viewBuilder: ViewBuilder) : AbstractView() {
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
        initScroll()
        initDeviceList()
        connectActions(node, controller)
        connectMessageSource(node)

        val scene = Scene(node)
        scene.addEventHandler(KeyEvent.KEY_PRESSED) { event ->
            if (event.code == KeyCode.F5 || event.code == KeyCode.R) {
                runOutsideUIAsync {
                    model.fireRefresh()
                }
            }
        }
        return scene
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
            logger.trace("Updating device set")
            val elementAdded = change.elementAdded
            if (elementAdded != null) {
                val node = viewBuilder.buildViewForDevice(elementAdded)
                deviceList.children.add(node)
            }
        })
    }

    private fun initScroll() {
        val title = Label("Supla GUI")
        scroll.bottomBar.children.add(title)
        title.style = "-fx-text-fill:WHITE; -fx-font-size: 40;"
        JFXScrollPane.smoothScrolling(scroll.children[0] as ScrollPane)

        StackPane.setMargin(title, Insets(0.0, 0.0, 0.0, 80.0))
        StackPane.setAlignment(title, Pos.CENTER_LEFT)
    }
}
