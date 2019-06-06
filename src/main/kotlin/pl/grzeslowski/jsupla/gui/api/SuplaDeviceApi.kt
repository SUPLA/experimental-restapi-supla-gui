package pl.grzeslowski.jsupla.gui.api

import pl.grzeslowski.jsupla.api.generated.ApiClient
import pl.grzeslowski.jsupla.api.generated.api.IoDevicesApi
import pl.grzeslowski.jsupla.api.generated.model.Device
import javax.inject.Inject

internal class SuplaDeviceApi @Inject constructor(apiClient: ApiClient) : DeviceApi {
    private val defaultInclude = listOf("channels", "location", "originalLocation", "connected", "schedules", "accessids")
    private val api: IoDevicesApi = IoDevicesApi(apiClient)

    override fun findAllDevice(): List<Device> = api.getIoDevices(defaultInclude)

    override fun findDevice(id: Int): Device = api.getIoDevice(id, defaultInclude)
}