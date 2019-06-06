package pl.grzeslowski.jsupla.gui.preferences

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pl.grzeslowski.jsupla.gui.io.InputOutputService
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject

class SimplePreferences @Inject constructor(private val inputOutputService: InputOutputService) : PreferencesService, AutoCloseable {
    private val logger: Logger = LoggerFactory.getLogger(SimplePreferences::class.java)
    private val preferencesFileName = "preferences.properties"
    private val properties = Properties()

    override fun write(key: String, value: Any) {
        properties.setProperty(key, value.toString())
    }

    override fun read(key: String): String? = properties.getProperty(key)

    override fun readBool(key: String) = properties.getProperty(key)?.toBoolean()

    override fun readBoolWithDefault(key: String, default: Boolean) = readBool(key) ?: default

    @PostConstruct
    fun init() {
        logger.debug("Loading preferences from `{}`", preferencesFileName)
        val readFile = inputOutputService.readFile(preferencesFileName)
        if (readFile != null) {
            try {
                properties.load(readFile.byteInputStream(StandardCharsets.UTF_8))
            } catch (e: Exception) {
                logger.error("Cannot load properties from `{}`!\nFile: {}", preferencesFileName, readFile, e)
            }
        }
    }

    @PreDestroy
    override fun close() {
        logger.debug("Saving preferences to `{}`", preferencesFileName)
        try {
            ByteArrayOutputStream().use { stream ->
                properties.store(stream, null)
                inputOutputService.writeToFile(preferencesFileName, stream)
            }
        } catch (e: Exception) {
            logger.error("Cannot save properties to `{}`! Properties: {}", preferencesFileName, properties, e)
        }
    }
}
