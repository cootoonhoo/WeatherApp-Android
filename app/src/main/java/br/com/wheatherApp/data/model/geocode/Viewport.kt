package br.com.wheatherApp.data.model.geocode

import br.com.wheatherApp.data.model.Location
import kotlinx.serialization.Serializable

@Serializable
data class Viewport(
    val northeast: Location,
    val southwest: Location
)