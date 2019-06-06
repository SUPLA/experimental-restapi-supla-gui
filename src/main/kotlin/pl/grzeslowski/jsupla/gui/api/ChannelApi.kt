package pl.grzeslowski.jsupla.gui.api

import pl.grzeslowski.jsupla.api.generated.model.ChannelExecuteActionRequest

interface ChannelApi {
    fun executeAction(id: Int, action: ChannelExecuteActionRequest)
}