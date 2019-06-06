package pl.grzeslowski.jsupla.gui.api

import pl.grzeslowski.jsupla.api.generated.model.ServerInfo

interface ServerApi {
    fun findServerInfo(): ServerInfo
}