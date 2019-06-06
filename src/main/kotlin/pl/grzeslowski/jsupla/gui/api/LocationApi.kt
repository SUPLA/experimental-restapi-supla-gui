package pl.grzeslowski.jsupla.gui.api

import pl.grzeslowski.jsupla.api.generated.model.Device
import pl.grzeslowski.jsupla.api.generated.model.Location

interface LocationApi {
    fun findDevicesInLocations(): Map<Location, List<Device>>
}