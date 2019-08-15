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

import griffon.core.env.ApplicationPhase
import griffon.core.mvc.MVCGroup
import griffon.core.test.GriffonUnitRule
import griffon.core.test.TestFor
import griffon.core.test.TestModuleOverrides
import griffon.core.view.WindowManager
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.embed.swing.JFXPanel
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.stage.Window
import org.awaitility.Awaitility
import org.codehaus.griffon.runtime.core.injection.AbstractTestingModule
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.isEmptyString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.doReturn
import org.mockito.BDDMockito.given
import org.mockito.Matchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.supla.gui.api.DeviceApi
import org.supla.gui.api.ServerApi
import org.supla.gui.db.Database
import org.supla.gui.preferences.PreferencesService
import org.supla.gui.preferences.TokenService
import pl.grzeslowski.jsupla.api.ApiException
import pl.grzeslowski.jsupla.api.device.Device
import pl.grzeslowski.jsupla.api.serverinfo.ServerInfo
import java.util.*
import java.util.Arrays.asList
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.HashMap

@Suppress("MemberVisibilityCanBePrivate")
@TestFor(SplashScreenController::class)
class SplashScreenTest {
    init {
        // force initialization JavaFX Toolkit
        JFXPanel()
    }

    lateinit var controller: SplashScreenController

    @Inject
    lateinit var tokenService: TokenService
    val properToken = "MzFhYTNiZTAwODg5M2E0NDE3OGUwNWE5ZjYzZWQ2YzllZGFiYWRmNDQwNDBlNmZhZGEzN2I3NTJiOWM2ZWEyZg.aHR0cDovL2xvY2FsaG9zdDo5MDkw"
    @Inject
    lateinit var serverApi: ServerApi
    @Inject
    lateinit var deviceApi: DeviceApi
    @Inject
    lateinit var database: Database

    // model
    lateinit var model: SplashScreenModel
    lateinit var centerBoxChildren: ObservableList<Node>
    lateinit var loadingInfo: SimpleStringProperty

    @Rule
    @JvmField
    val griffon: GriffonUnitRule = GriffonUnitRule()

    @Before
    fun setUpModel() {
        model = mock(SplashScreenModel::class.java)
        centerBoxChildren = FXCollections.observableArrayList()
        given(model.centerBoxChildren).willReturn(centerBoxChildren)
        loadingInfo = SimpleStringProperty("not set")
        given(model.loadingInfo).willReturn(loadingInfo)
        controller.model = model
    }

    @Before
    fun setUpToken() {
        given(tokenService.read()).willReturn(properToken)
    }

    @Before
    fun setUpServerApi() {
        val serverInfo = mock(ServerInfo::class.java)
        given(serverInfo.apiVersion).willReturn("3.1")
        given(serverInfo.cloudVersion).willReturn("3.0")
        given(serverInfo.supportedVersions).willReturn(mutableListOf("3.0", "3.1", "3.2"))
        given(serverApi.findServerInfo()).willReturn(serverInfo)
    }

    @Before
    fun setupDeviceApi() {
        val devices = TreeSet<Device>()
        devices.add(mock(Device::class.java))
        devices.add(mock(Device::class.java))
        devices.add(mock(Device::class.java))
        given(deviceApi.findAllDevice()).willReturn(devices)
    }

    @Test
    fun shouldShowInputForTokenIfTokenIsMissing() {
        // given:
        val token = ""
        given(tokenService.read()).willReturn(token)

        // when:
        controller.mvcGroupInit(HashMap())
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .until { centerBoxChildren.size > 0 && centerBoxChildren[0] != null }

        // then:
        assertThat(centerBoxChildren, Matchers.hasSize(3))
        val label = centerBoxChildren[0] as Label
        assertThat(label.text, equalTo("Please provide oAuth token"))
        val textField = centerBoxChildren[1] as TextField
        assertThat(textField.text, isEmptyString())
        val button = centerBoxChildren[2] as Button
        assertThat(button.text, equalTo("Go!"))
        assertThat(loadingInfo.value, equalTo("Missing Token"))
    }

    @Test
    fun shouldShowMessageIfServerDoNotSupportThisClient() {
        // given
        val serverInfo = mock(ServerInfo::class.java)
        given(serverInfo.apiVersion).willReturn("2.1")
        given(serverInfo.cloudVersion).willReturn("3.0")
        given(serverInfo.supportedVersions).willReturn(mutableListOf("3.0", "3.1", "3.2"))
        given(serverApi.findServerInfo()).willReturn(serverInfo)

        // when
        controller.mvcGroupInit(HashMap())
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .until { centerBoxChildren.size > 0 && centerBoxChildren[0] != null }

        // then
        assertThat(centerBoxChildren, Matchers.hasSize(1))
        val label = centerBoxChildren[0] as Label
        assertThat(label.text, equalTo("Your client is not supported by Supla Cloud!"))
        assertThat(loadingInfo.value, equalTo("API v2.1, Cloud v3.0, Supported [3.0, 3.1, 3.2]"))
    }

    @Test
    fun shouldShowInfoThatThereAreNoDevices() {
        // given
        given(deviceApi.findAllDevice()).willReturn(TreeSet())

        // when
        controller.mvcGroupInit(HashMap())
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .until { centerBoxChildren.size > 0 && centerBoxChildren[0] != null }

        // then
        assertThat(centerBoxChildren, Matchers.hasSize(2))
        val label = centerBoxChildren[0] as Label
        assertThat(label.text, equalTo("You have no devices in Supla Cloud"))
        val refreshButton = centerBoxChildren[1] as Button
        assertThat(refreshButton.text, equalTo("Refresh"))
        assertThat(loadingInfo.value, equalTo("Please add some devices"))
    }

    @Test
    fun shouldShowMainWindow() {
        // given
        controller.mvcGroup = mock(MVCGroup::class.java)

        // when
        controller.mvcGroupInit(HashMap())
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .until { loadingInfo.value == "Ready to go!" }

        // then
        verify(controller.mvcGroup).createMVCGroup("jSuplaGui")
        verify(database).clear("serverInfos")
        verify(database).save("serverInfos", serverApi.findServerInfo())
        verify(database).clear("devices")
        verify(database).saveAll("devices", deviceApi.findAllDevice())
        val windowManager = controller.application.getWindowManager<Window>()
        verify(windowManager).show("mainWindow")
        verify(windowManager).hide("splashScreenWindow")
    }

    @Test
    fun shouldShowMainWindowAfterFillingToken() {
        // given
        val token = ""
        given(tokenService.read()).willReturn(token)
        controller.mvcGroup = mock(MVCGroup::class.java)

        // when no Token
        controller.mvcGroupInit(HashMap())
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .until { centerBoxChildren.size > 0 && centerBoxChildren[0] != null }

        // fill token
        val textField = centerBoxChildren[1] as TextField
        textField.text = properToken
        given(tokenService.read()).willReturn(properToken)
        // fire button
        val button = centerBoxChildren[2] as Button
        button.fire()
        // w8
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .until { loadingInfo.value == "Ready to go!" }

        // then
        verify(tokenService).write(properToken)
        verify(controller.mvcGroup).createMVCGroup("jSuplaGui")
        val windowManager = controller.application.getWindowManager<Window>()
        verify(windowManager).show("mainWindow")
        verify(windowManager).hide("splashScreenWindow")
    }

    @Test
    fun shouldShowMessageWhenWasApiExceptionOnServerApi() {
        // given
        given(serverApi.findServerInfo()).willThrow(ApiException("test-path", io.swagger.client.ApiException("xxx")))

        // when
        controller.mvcGroupInit(HashMap())
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .until { centerBoxChildren.size > 0 && centerBoxChildren[0] != null }

        // then
        assertThat(centerBoxChildren, Matchers.hasSize(2))
        val label = centerBoxChildren[0] as Label
        assertThat(label.text, equalTo("API error occurred!"))
        val pane = centerBoxChildren[1] as Pane
        assertThat(pane.children, Matchers.hasSize(2))
        val refreshButton = pane.children[0] as Button
        assertThat(refreshButton.text, equalTo("Try again"))
        val closeButton = pane.children[1] as Button
        assertThat(closeButton.text, equalTo("Close"))
        assertThat(loadingInfo.value, equalTo("Got error while executing `test-path` API call!."))
    }

    @Test
    fun shouldShowMessageWhenWasApiExceptionOnDeviceApi() {
        // given
        given(deviceApi.findAllDevice()).willThrow(ApiException("test-path", io.swagger.client.ApiException("xxx")))

        // when
        controller.mvcGroupInit(HashMap())
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .until { centerBoxChildren.size > 0 && centerBoxChildren[0] != null }

        // then
        assertThat(centerBoxChildren, Matchers.hasSize(2))
        val label = centerBoxChildren[0] as Label
        assertThat(label.text, equalTo("API error occurred!"))
        val pane = centerBoxChildren[1] as Pane
        assertThat(pane.children, Matchers.hasSize(2))
        val refreshButton = pane.children[0] as Button
        assertThat(refreshButton.text, equalTo("Try again"))
        val closeButton = pane.children[1] as Button
        assertThat(closeButton.text, equalTo("Close"))
        assertThat(loadingInfo.value, equalTo("Got error while executing `test-path` API call!."))
    }

    @Test
    fun shouldShowGenericMessageWhenErrorOccur() {
        // given
        given(serverApi.findServerInfo()).willThrow(NullPointerException("xxx"))

        // when
        controller.mvcGroupInit(HashMap())
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .until { centerBoxChildren.size > 0 && centerBoxChildren[0] != null }

        // then
        assertThat(centerBoxChildren, Matchers.hasSize(2))
        val label = centerBoxChildren[0] as Label
        assertThat(label.text, equalTo("Generic error occurred!"))
        val pane = centerBoxChildren[1] as Pane
        assertThat(pane.children, Matchers.hasSize(2))
        val refreshButton = pane.children[0] as Button
        assertThat(refreshButton.text, equalTo("Try again"))
        val closeButton = pane.children[1] as Button
        assertThat(closeButton.text, equalTo("Close"))
        assertThat(loadingInfo.value, equalTo("xxx"))
    }


    @Test
    fun shouldCloseApplicationAfterErrorOccur() {
        // given
        given(controller.application.getWindowManager<Window>().canShutdown(any())).willReturn(true)
        given(serverApi.findServerInfo()).willThrow(ApiException("test-path", io.swagger.client.ApiException("xxx")))

        // when
        controller.mvcGroupInit(HashMap())
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .until { centerBoxChildren.size > 0 && centerBoxChildren[0] != null }
        val pane = centerBoxChildren[1] as Pane
        val closeButton = pane.children[1] as Button
        closeButton.fire()
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .until { controller.application.phase == ApplicationPhase.SHUTDOWN }

        // then
        assertThat(controller.application.phase, equalTo(ApplicationPhase.SHUTDOWN))
    }

    @Test
    fun shouldShowMainWindowAfterErrorAndReTry() {
        // given
        controller.mvcGroup = mock(MVCGroup::class.java)
        val serverInfo = serverApi.findServerInfo()
        given(serverApi.findServerInfo()).willThrow(ApiException("test-path", io.swagger.client.ApiException("xxx")))

        // when exception
        controller.mvcGroupInit(HashMap())
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .until { centerBoxChildren.size > 0 && centerBoxChildren[0] != null }
        val pane = centerBoxChildren[1] as Pane
        val refreshButton = pane.children[0] as Button
        // not throw error anymore
        doReturn(serverInfo).`when`(serverApi).findServerInfo()
        refreshButton.fire()

        // w8
        Awaitility.await()
                .atMost(10, TimeUnit.SECONDS)
                .until { loadingInfo.value == "Ready to go!" }

        // then
        verify(controller.mvcGroup).createMVCGroup("jSuplaGui")
        val windowManager = controller.application.getWindowManager<Window>()
        verify(windowManager).show("mainWindow")
        verify(windowManager).hide("splashScreenWindow")
    }

    @Suppress("unused") // Used by `@TestModuleOverrides`
    @TestModuleOverrides
    fun testModuleOverrides(): MutableList<griffon.core.injection.Module> {
        return asList(buildModule())
    }

    private fun buildModule(): griffon.core.injection.Module {
        return object : AbstractTestingModule() {
            override fun doConfigure() {
                bind(PreferencesService::class.java)
                        .toProvider { mock(PreferencesService::class.java) }
                        .asSingleton()
                bind(ServerApi::class.java)
                        .toProvider { mock(ServerApi::class.java) }
                        .asSingleton()
                bind(DeviceApi::class.java)
                        .toProvider { mock(DeviceApi::class.java) }
                        .asSingleton()
                bind(TokenService::class.java)
                        .toProvider { mock(TokenService::class.java) }
                        .asSingleton()
                bind(WindowManager::class.java)
                        .toProvider { mock(WindowManager::class.java) }
                        .asSingleton()
                bind(Database::class.java)
                        .toProvider { mock(Database::class.java) }
                        .asSingleton()
            }
        }
    }
}