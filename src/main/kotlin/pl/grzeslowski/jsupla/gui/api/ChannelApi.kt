package pl.grzeslowski.jsupla.gui.api

import pl.grzeslowski.jsupla.api.channel.Channel
import pl.grzeslowski.jsupla.api.channel.action.Action

interface ChannelApi {
    fun executeAction(channel: Channel, action: Action)
}