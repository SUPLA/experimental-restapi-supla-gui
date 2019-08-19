package org.supla.gui.api

import org.assertj.core.api.AbstractStringAssert
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import org.supla.gui.preferences.PreferencesKeys
import org.supla.gui.preferences.PreferencesService
import org.supla.gui.preferences.TokenService
import pl.grzeslowski.jsupla.api.Api

@Suppress("MemberVisibilityCanBePrivate")
@RunWith(MockitoJUnitRunner::class)
class ApiProviderTest {
    @InjectMocks
    lateinit var apiProvider: ApiProvider

    @Mock
    lateinit var tokenService: TokenService
    @Mock
    lateinit var preferencesService: PreferencesService

    val token = "MzFhYTNiZTAwODg5M2E0NDE3OGUwNWE5ZjYzZWQ2YzllZGFiYWRmNDQwNDBlNmZhZGEzN2I3NTJiOWM2ZWEyZg.aHR0cHM6Ly9sb2NhbGhvc3Q6OTA5MA=="

    @Before
    fun setUp() {
        given(preferencesService.readBoolWithDefault(PreferencesKeys.omitHttps, false)).willReturn(false)
        given(tokenService.read()).willReturn(token)
    }

    @Test
    fun shouldCreateApiWithNormalToken() {
        // when
        val api = apiProvider.get()

        // then
        basePath(api).startsWith("https://localhost:9090")
    }

    @Test
    fun shouldCreateApiWithoutHttps() {
        // given
        given(preferencesService.readBoolWithDefault(PreferencesKeys.omitHttps, false)).willReturn(true)

        // when
        val api = apiProvider.get()

        // then
        basePath(api).startsWith("http://localhost:9090")
    }

    private fun basePath(api: Api): AbstractStringAssert<*> {
        val apiClient = api::class.java.declaredFields
                .map { field -> field.isAccessible = true; field }
                .findLast { field -> field.name == "apiClient" }!!
                .get(api) as io.swagger.client.ApiClient
        return assertThat(apiClient.basePath)
    }
}