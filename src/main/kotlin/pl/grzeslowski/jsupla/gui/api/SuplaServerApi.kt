package pl.grzeslowski.jsupla.gui.api

import pl.grzeslowski.jsupla.api.Api
import pl.grzeslowski.jsupla.api.serverinfo.ServerInfo
import javax.inject.Inject

internal class SuplaServerApi @Inject constructor(api: Api) : ServerApi {
    private val serverInfoApi = api.serverInfoApi

    override fun findServerInfo(): ServerInfo = serverInfoApi.findServerInfo()
}