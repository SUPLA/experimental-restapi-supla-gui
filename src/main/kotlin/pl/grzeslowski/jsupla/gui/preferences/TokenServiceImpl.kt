package pl.grzeslowski.jsupla.gui.preferences

import javax.inject.Inject

internal class TokenServiceImpl @Inject constructor(private val preferencesService: PreferencesService) : TokenService {
    private val tokenKey = "token"

    override fun read(): String? = preferencesService.read(tokenKey)

    override fun write(token: String) {
        preferencesService.write(tokenKey, token)
    }
}