package pl.grzeslowski.jsupla.gui.preferences

import org.slf4j.LoggerFactory
import javax.inject.Inject

internal class TokenServiceImpl @Inject constructor(private val preferencesService: PreferencesService) : TokenService {
    private val logger = LoggerFactory.getLogger(TokenServiceImpl::class.java)
    private val tokenKey = "token"

    override fun read(): String? = preferencesService.read(tokenKey)

    override fun write(token: String) {
        logger.info("Saving new token, SHA {}", token.hashCode())
        preferencesService.write(tokenKey, token)
    }
}