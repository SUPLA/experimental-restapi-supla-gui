package pl.grzeslowski.jsupla.gui.api

import pl.grzeslowski.jsupla.api.serverinfo.ServerInfo

interface ServerApi {
    fun findServerInfo(): ServerInfo
}
