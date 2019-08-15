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
package pl.grzeslowski.jsupla.gui.i18n

import griffon.core.GriffonApplication
import javax.inject.Inject

class InternationalizationServiceImpl @Inject constructor(private val application: GriffonApplication) : InternationalizationService {
    override fun findMessage(key: String): String = application.messageSource.getMessage(key)

    override fun findMessage(key: String, params: List<String>): String = application.messageSource.getMessage(key, params)
}