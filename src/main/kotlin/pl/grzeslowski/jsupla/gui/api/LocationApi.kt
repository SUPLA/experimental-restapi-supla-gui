package pl.grzeslowski.jsupla.gui.api

import pl.grzeslowski.jsupla.api.device.Device
import pl.grzeslowski.jsupla.api.location.Location
import java.util.*


interface LocationApi {
    fun findDevicesInLocations(): SortedSet<Location>
}