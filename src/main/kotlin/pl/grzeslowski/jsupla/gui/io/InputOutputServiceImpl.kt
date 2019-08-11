package pl.grzeslowski.jsupla.gui.io

import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.StandardCharsets.UTF_8

class InputOutputServiceImpl : InputOutputService {
    private val logger = LoggerFactory.getLogger(InputOutputServiceImpl::class.java)
    private val path = System.getProperty("user.home") + "/.supla/"

    init {
        val file = File(path)
        logger.info("Supla home dir is `{}`", file.absolutePath)
        if (file.exists().not()) {
            logger.info("Creating directory `{}`", path)
            val createdDirectory = file.mkdirs()
            if (createdDirectory.not()) {
                throw IllegalStateException("Cannot create direcory `$path`!")
            }
        } else {
            if (file.isDirectory.not()) {
                throw IllegalStateException("File `$path` is not a directory!")
            }
        }
    }

    override fun appendToFile(filename: String, text: String) {
        val pathname = path + filename
        logger.trace("appendToFile({})", pathname)
        File(pathname).appendText(text)
    }

    override fun readFile(filename: String): String? {
        val pathname = path + filename
        logger.trace("readFile({})", pathname)
        val file = File(pathname)
        return if (file.exists()) {
            file.readText()
        } else {
            null
        }
    }

    override fun writeToFile(filename: String, text: String) {
        val pathname = path + filename
        logger.trace("writeToFile({})", pathname)
        File(pathname).writeText(text)
    }

    override fun writeToFile(filename: String, text: ByteArrayOutputStream) {
        writeToFile(filename, String(text.toByteArray(), UTF_8))
    }
}