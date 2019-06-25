package pl.grzeslowski.jsupla.gui.api

import pl.grzeslowski.jsupla.api.Api
import pl.grzeslowski.jsupla.api.location.Location
import java.util.*
import javax.inject.Inject

internal class SuplaLocationApi @Inject constructor(api: Api) : LocationApi {
    private val locationApi = api.locationApi

    override fun findDevicesInLocations(): SortedSet<Location> = locationApi.findLocations()
}