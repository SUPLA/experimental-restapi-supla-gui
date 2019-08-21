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
package org.supla.gui.preferences

object PreferencesKeys {
    /**
     * Set how often GUI should refresh devices with Supla Cloud. Default 30 s
     */
    const val refreshTime = "refresh_time_in_sec"
    /**
     * Set how often GUI should check if refresh of devices is valid. Default 10 s
     */
    const val refreshCheckTime = "refresh_check_time_in_sec"
    /**
     * Set how many thresds should be in thread pool. Default 3
     */
    const val threadScheduleThreadPoolSize = "thread.schedule_thread_pool_size"

    /**
     *
     */
    const val omitHttps = "dangerous.omit_https"

    /**
     * Sets the debug mode. Default false
     */
    const val debugMode = "debug_mode"
}