package pl.grzeslowski.jsupla.gui.thread

import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

interface ThreadService {
    fun scheduleEvery(command: Runnable, initialDelay: Long, period: Long, unit: TimeUnit): ScheduledFuture<*>
}