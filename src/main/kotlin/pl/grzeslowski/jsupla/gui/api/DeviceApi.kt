package pl.grzeslowski.jsupla.gui.api

import pl.grzeslowski.jsupla.api.generated.model.Device

interface DeviceApi {
    fun findAllDevice(): List<Device>

    fun findDevice(id: Int): Device
}