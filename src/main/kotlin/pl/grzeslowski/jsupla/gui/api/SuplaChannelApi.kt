package pl.grzeslowski.jsupla.gui.api

import pl.grzeslowski.jsupla.api.generated.ApiClient
import pl.grzeslowski.jsupla.api.generated.api.ChannelsApi
import pl.grzeslowski.jsupla.api.generated.model.ChannelExecuteActionRequest
import javax.inject.Inject

internal class SuplaChannelApi @Inject constructor(apiClient: ApiClient) : ChannelApi {
    private val api: ChannelsApi = ChannelsApi(apiClient)

    override fun executeAction(id: Int, action: ChannelExecuteActionRequest) {
        api.executeAction(action, id)
    }
}