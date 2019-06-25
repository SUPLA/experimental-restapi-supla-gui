package pl.grzeslowski.jsupla.gui.api

import pl.grzeslowski.jsupla.api.device.Device
import java.util.*


interface DeviceApi {
    fun findAllDevice(): SortedSet<Device>

    fun findDevice(id: Int): Device
}