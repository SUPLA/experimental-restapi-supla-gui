package pl.grzeslowski.jsupla.gui.api

import pl.grzeslowski.jsupla.api.generated.ApiClient
import pl.grzeslowski.jsupla.api.generated.model.ServerInfo
import javax.inject.Inject

internal class SuplaServerApi @Inject constructor(apiClient: ApiClient) : ServerApi {
    private val api = pl.grzeslowski.jsupla.api.generated.api.ServerApi(apiClient)

    override fun findServerInfo(): ServerInfo = api.serverInfo
}