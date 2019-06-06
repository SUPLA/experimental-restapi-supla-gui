package pl.grzeslowski.jsupla.gui.preferences

interface PreferencesService {
    fun write(key: String, value: String)

    fun read(key: String): String?
}