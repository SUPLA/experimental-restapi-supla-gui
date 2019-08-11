package pl.grzeslowski.jsupla.gui.db

import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.stream.Collectors
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.reflect.KClass

class InMemoryDatabase : Database {
    private val collections: MutableMap<String, MutableList<Any>> = HashMap()
    private val collectionLock = ReentrantReadWriteLock()

    override fun save(collection: String, t: Any) {
        collectionLock.write {
            val list = collections.computeIfAbsent(collection) { ArrayList() }
            list.add(t)
        }
    }

    override fun saveAll(collection: String, t: Collection<Any>) {
        collectionLock.write {
            val list = collections.computeIfAbsent(collection) { ArrayList() }
            list.addAll(t)
        }
    }

    override fun clear(collection: String) {
        collectionLock.write {
            collections.put(collection, ArrayList())
        }
    }

    override fun <T : Any> load(collection: String, clazz: KClass<T>): T {
        val all = loadAll(collection, clazz)
        if (all.size > 1) {
            throw IllegalStateException("There are ${all.size} elements in collection `$collection`! Should be only one!")
        }
        if (all.size == 0) {
            throw IllegalStateException("There are no elements in collection `$collection`!")
        }
        return all[0]
    }

    override fun <T : Any> loadAll(collection: String, clazz: KClass<T>): List<T> {
        return collectionLock.read {
            collections.getOrDefault(collection, ArrayList())
                    .stream()
                    .map { clazz.java.cast(it) }
                    .collect(Collectors.toList())

        }
    }
}