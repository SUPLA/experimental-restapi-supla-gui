package pl.grzeslowski.jsupla.gui.api

import pl.grzeslowski.jsupla.api.Api
import pl.grzeslowski.jsupla.api.channel.Channel
import pl.grzeslowski.jsupla.api.channel.action.Action
import javax.inject.Inject

internal class SuplaChannelApi @Inject constructor(api: Api) : ChannelApi {
    private val channelApi = api.channelApi

    override fun executeAction(channel: Channel, action: Action) {
        channelApi.updateState(channel, action)
    }
}
