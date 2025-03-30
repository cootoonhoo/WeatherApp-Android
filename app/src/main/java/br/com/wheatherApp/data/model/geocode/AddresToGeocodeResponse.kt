package br.com.wheatherApp.data.model.geocode

import kotlinx.serialization.Serializable

@Serializable
data class AddresToGeocodeResponse(
    val results: List<PlaceResult>,
    val status: String
)