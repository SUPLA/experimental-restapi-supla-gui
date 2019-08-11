package pl.grzeslowski.jsupla.gui.i18n

interface InternationalizationService {
    fun findMessage(key: String): String

    fun findMessage(key: String, params: List<String>): String
}