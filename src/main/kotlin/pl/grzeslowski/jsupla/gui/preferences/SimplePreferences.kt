// Copyright (C) AC SOFTWARE SP. Z O.O.
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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

    override fun readInt(key: String): Int? = properties.getProperty(key)?.toInt()

    override fun readIntWithDefault(key: String, default: Int): Int = readInt(key) ?: default

    override fun readLong(key: String): Long? = properties.getProperty(key)?.toLong()

    override fun readLongWithDefault(key: String, default: Long): Long = readLong(key) ?: default

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
