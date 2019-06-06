package pl.grzeslowski.jsupla.gui.api

import pl.grzeslowski.jsupla.api.generated.ApiClient
import pl.grzeslowski.jsupla.api.generated.api.LocationsApi
import pl.grzeslowski.jsupla.api.generated.model.Device
import pl.grzeslowski.jsupla.api.generated.model.Location
import java.util.stream.Collectors
import javax.inject.Inject

internal class SuplaLocationApi @Inject constructor(apiClient: ApiClient) : LocationApi {
    private val defaultIncludes = listOf("channels", "iodevices", "accessids", "channelGroups", "password")
    private val locationApi = LocationsApi(apiClient)

    override fun findDevicesInLocations(): Map<Location, List<Device>> =
            locationApi.getLocations(defaultIncludes)
                    .stream()
                    .collect(Collectors.toMap(
                            { location -> location },
                            { location -> location.iodevices }
                    ))
}