package pl.grzeslowski.jsupla.gui.db

interface Database {
    fun <T> save(collection: String, t: T)
}