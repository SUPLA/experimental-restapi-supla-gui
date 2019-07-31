package pl.grzeslowski.jsupla.gui

import griffon.core.test.GriffonUnitRule
import griffon.core.test.TestFor
import griffon.core.test.TestModuleOverrides
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.embed.swing.JFXPanel
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import org.awaitility.Awaitility
import org.codehaus.griffon.runtime.core.injection.AbstractTestingModule
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.isEmptyString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import pl.grzeslowski.jsupla.api.serverinfo.ServerInfo
import pl.grzeslowski.jsupla.gui.api.DeviceApi
import pl.grzeslowski.jsupla.gui.api.ServerApi
import pl.grzeslowski.jsupla.gui.preferences.PreferencesService
import pl.grzeslowski.jsupla.gui.preferences.TokenService
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
    val properToken = "Some token"
    @Inject
    lateinit var serverApi: ServerApi
    @Inject
    lateinit var deviceApi: DeviceApi

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
        given(serverInfo.supportedVersions).willReturn(asList("3.0", "3.1", "3.2"))
        given(serverApi.findServerInfo()).willReturn(serverInfo)
    }

    @Test
    fun shouldShowInputForTokenIfTokenIsMissing() {
        // given:
        val token = ""
        given(tokenService.read()).willReturn(token)

        // when:
        controller.mvcGroupInit(HashMap())

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
        given(serverInfo.supportedVersions).willReturn(asList("3.0", "3.1", "3.2"))
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
            }
        }
    }
}