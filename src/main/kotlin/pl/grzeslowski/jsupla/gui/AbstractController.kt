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
package pl.grzeslowski.jsupla.gui

import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController
import javax.annotation.PreDestroy

abstract class AbstractController : AbstractGriffonController(), AutoCloseable {
    override fun mvcGroupInit(args: MutableMap<String, Any>) {
        application.eventRouter.addEventListener("WindowShown") { eventArgs ->
            val windowName = findWindowNameInEventArgs(eventArgs)
            if (windowName() == windowName) {
                initOutsideUi()
            }
        }
        application.eventRouter.addEventListener("WindowHidden") { eventArgs ->
            val windowName = findWindowNameInEventArgs(eventArgs)
            if (windowName() == windowName) {
                close()
            }
        }
    }

    private fun findWindowNameInEventArgs(args2: Array<Any?>) =
            if (args2.isNotEmpty()) {
                args2[0]?.toString()
            } else {
                null
            }

    protected abstract fun windowName(): String

    protected abstract fun initOutsideUi()

    @PreDestroy
    override fun close() {
        // empty
    }
}