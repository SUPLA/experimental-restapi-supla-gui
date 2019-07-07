package pl.grzeslowski.jsupla.gui.api

import org.slf4j.LoggerFactory
import pl.grzeslowski.jsupla.api.Api
import pl.grzeslowski.jsupla.gui.preferences.TokenService
import javax.inject.Inject
import javax.inject.Provider

internal class ApiProvider @Inject constructor(private val tokenService: TokenService) : Provider<Api> {
    private val logger = LoggerFactory.getLogger(ApiProvider::class.java)

    override fun get(): Api {
        val token = tokenService.read()
        logger.trace("New api client for token SHA `{}`", token.hashCode())
        return Api.getInstance(token)
    }
}