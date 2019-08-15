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