package pl.grzeslowski.jsupla.gui.db

import kotlin.reflect.KClass

interface Database {
    fun save(collection: String, t: Any)

    fun saveAll(collection: String, t: Collection<Any>)

    fun clear(collection: String)

    fun <T : Any> load(collection: String, clazz: KClass<T>): T

    fun <T : Any> loadAll(collection: String, clazz: KClass<T>): List<T>
}