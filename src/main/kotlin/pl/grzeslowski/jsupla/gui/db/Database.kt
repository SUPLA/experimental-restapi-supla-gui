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

import kotlin.reflect.KClass

interface Database {
    fun save(collection: String, t: Any)

    fun saveAll(collection: String, t: Collection<Any>)

    fun clear(collection: String)

    fun <T : Any> load(collection: String, clazz: KClass<T>): T

    fun <T : Any> loadAll(collection: String, clazz: KClass<T>): List<T>
}