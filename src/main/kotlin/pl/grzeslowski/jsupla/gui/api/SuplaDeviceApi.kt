package pl.grzeslowski.jsupla.gui.api

import pl.grzeslowski.jsupla.api.Api
import pl.grzeslowski.jsupla.api.device.Device
import java.util.*
import javax.inject.Inject

internal class SuplaDeviceApi @Inject constructor(api: Api) : DeviceApi {
    private val deviceApi=api.deviceApi

    override fun findAllDevice(): SortedSet<Device> = deviceApi.findDevices()

    override fun findDevice(id: Int): Device = deviceApi.findDevice(id)
}