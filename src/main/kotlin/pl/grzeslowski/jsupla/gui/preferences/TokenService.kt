package pl.grzeslowski.jsupla.gui.preferences

interface TokenService {
    fun read(): String?
    fun write(token: String)
}