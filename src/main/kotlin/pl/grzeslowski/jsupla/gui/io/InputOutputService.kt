package pl.grzeslowski.jsupla.gui.io

import java.io.ByteArrayOutputStream

interface InputOutputService {
    fun writeToFile(filename: String, text: String)

    fun appendToFile(filename: String, text: String)

    fun readFile(filename: String): String?

    fun writeToFile(filename: String, text: ByteArrayOutputStream);
}