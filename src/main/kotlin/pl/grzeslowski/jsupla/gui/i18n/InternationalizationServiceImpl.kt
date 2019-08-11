package pl.grzeslowski.jsupla.gui.i18n

import griffon.core.GriffonApplication
import javax.inject.Inject

class InternationalizationServiceImpl @Inject constructor(private val application: GriffonApplication) : InternationalizationService {
    override fun findMessage(key: String): String = application.messageSource.getMessage(key)

    override fun findMessage(key: String, params: List<String>): String = application.messageSource.getMessage(key, params)
}