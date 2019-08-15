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
import griffon.util.AbstractMapResourceBundle

class Config : AbstractMapResourceBundle() {
    override fun initialize(entries: MutableMap<String, Any>) {
        entries.put("application", hashMapOf(
                "title" to "Supla",
                "startupGroups" to listOf("splashScreen", "jSuplaGui"),
                "autoshutdown" to true
        ))
        entries.put("mvcGroups", hashMapOf(
                "jSuplaGui" to hashMapOf(
                        "model" to "org.supla.gui.JSuplaGuiModel",
                        "view" to "org.supla.gui.JSuplaGuiView",
                        "controller" to "org.supla.gui.JSuplaGuiController"
                ),
                "splashScreen" to hashMapOf(
                        "model" to "org.supla.gui.SplashScreenModel",
                        "view" to "org.supla.gui.SplashScreenView",
                        "controller" to "org.supla.gui.SplashScreenController")
        ))
    }
}