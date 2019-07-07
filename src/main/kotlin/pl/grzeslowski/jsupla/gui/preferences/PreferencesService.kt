package pl.grzeslowski.jsupla.gui.preferences

interface PreferencesService {
    fun write(key: String, value: Any)

    fun read(key: String): String?

    fun readBool(key: String): Boolean?

    fun readBoolWithDefault(key: String, default: Boolean): Boolean

    fun readInt(key: String): Int?

    fun readIntWithDefault(key: String, default: Int): Int

    fun readLong(key: String): Long?

    fun readLongWithDefault(key: String, default: Long): Long
}