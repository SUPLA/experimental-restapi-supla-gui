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
package org.supla.gui.api

import org.slf4j.LoggerFactory
import org.supla.gui.preferences.PreferencesKeys
import org.supla.gui.preferences.PreferencesService
import org.supla.gui.preferences.TokenService
import pl.grzeslowski.jsupla.api.Api
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class ApiProvider @Inject constructor(
        private val tokenService: TokenService,
        private val preferencesService: PreferencesService) : Provider<Api> {
    private val logger = LoggerFactory.getLogger(ApiProvider::class.java)

    override fun get(): Api {
        val omitHttps = preferencesService.readBoolWithDefault(PreferencesKeys.omitHttps, false)
        val token = tokenService.read()!!
        logger.trace("New api client for token SHA `{}`", token.hashCode())
        return if (omitHttps) {
            logger.warn("Property `omit_https` is enabled! will change HTTPS connection to HTTP! For your own safety disable this function in properties!")
            Api.getInstance(token, buildUrl(token))
        } else {
            Api.getInstance(token)
        }
    }

    private fun buildUrl(token: String): String {
        val split = token
                .split("\\.".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
        if (split.size < 2) {
            throw IllegalArgumentException("OAuth token does not contain '.' (dot)!")
        } else if (split.size > 2) {
            throw IllegalArgumentException("OAuth token has too many '.' (dot) " + split.size + "!")
        }
        val urlBase64 = split[1]
        val urlWithHttps = String(Base64.getDecoder().decode(urlBase64))
        val urlWithoutHttps = urlWithHttps.replace("https", "http", true)
        logger.debug("Changed URL `$urlWithHttps` to `$urlWithoutHttps`.")
        return urlWithoutHttps
    }
}