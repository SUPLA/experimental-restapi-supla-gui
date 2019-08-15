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
package pl.grzeslowski.jsupla.gui.thread

import org.slf4j.LoggerFactory
import pl.grzeslowski.jsupla.gui.preferences.PreferencesService
import java.util.concurrent.Executors.newScheduledThreadPool
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import javax.annotation.PreDestroy
import javax.inject.Inject
import pl.grzeslowski.jsupla.gui.preferences.PreferencesKeys.threadScheduleThreadPoolSize as size

internal class ThreadServiceImpl @Inject constructor(preferencesService: PreferencesService) : ThreadService, AutoCloseable {
    private val logger = LoggerFactory.getLogger(ThreadServiceImpl::class.java)
    private val scheduledThreadPool = newScheduledThreadPool(preferencesService.readIntWithDefault(size, 3))

    override fun scheduleEvery(command: Runnable, initialDelay: Long, period: Long, unit: TimeUnit): ScheduledFuture<*> {
        return scheduledThreadPool.scheduleWithFixedDelay(command, initialDelay, period, unit)
    }

    @PreDestroy
    override fun close() {
        try {
            logger.debug("Closing `scheduledThreadPool`")
            scheduledThreadPool.shutdownNow()
        } catch (ex: Exception) {
            logger.error("Cannot close `scheduledThreadPool`!", ex)
        }
    }
}
