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
