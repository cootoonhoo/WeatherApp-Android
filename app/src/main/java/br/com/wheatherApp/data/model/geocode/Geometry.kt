package br.com.wheatherApp.data.model.geocode

import br.com.wheatherApp.data.model.Location
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Geometry(
    val bounds: Bounds,
    val location: Location,
    @SerialName("location_type")
    val locationType: String,
    val viewport: Viewport
)