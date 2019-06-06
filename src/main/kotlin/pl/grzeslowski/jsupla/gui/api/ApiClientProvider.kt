package pl.grzeslowski.jsupla.gui.api

import org.slf4j.LoggerFactory
import pl.grzeslowski.jsupla.api.ApiClientFactory
import pl.grzeslowski.jsupla.api.generated.ApiClient
import pl.grzeslowski.jsupla.gui.preferences.PreferencesKeys
import pl.grzeslowski.jsupla.gui.preferences.PreferencesService
import javax.inject.Inject
import javax.inject.Provider

internal class ApiClientProvider @Inject constructor(private val preferencesService: PreferencesService) : Provider<ApiClient> {
    private val logger = LoggerFactory.getLogger(ApiClientProvider::class.java)

    override fun get(): ApiClient {
        val token = preferencesService.read(PreferencesKeys.token)
        logger.trace("New api client for token SHA `{}`", token.hashCode())
        return ApiClientFactory.INSTANCE.newApiClient(token)
    }
}