package pl.grzeslowski.jsupla.gui.preferences

object PreferencesKeys {
    /**
     * Set how often GUI should refresh devices with Supla Cloud. Default 30 s
     */
    const val refreshTime = "refresh_time_in_sec"
    /**
     * Set how often GUI should check if refresh of devices is valid. Default 10 s
     */
    const val refreshCheckTime = "refresh__check_time_in_sec"
    /**
     * Set how many thresds should be in thread pool. Default 3
     */
    const val threadScheduleThreadPoolSize = "thread.schedule_thread_pool_size"
}