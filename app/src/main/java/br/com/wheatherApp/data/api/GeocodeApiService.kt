package br.com.wheatherApp.data.api

import City
import br.com.wheatherApp.data.model.Location
import br.com.wheatherApp.data.model.geocode.AddresToGeocodeResponse
import br.com.wheatherApp.data.util.Constants
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*


suspend fun main()
{
    val city = getCityInfo("Av João XXIII,231 - Uberlândia, MG")
    println(city)
}

suspend fun getCityInfo(address: String) : City? {
    val formatAddress = address.replace(' ','+');
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get(Constants.GEOCODING_API_URL) {
        url {
            parameters.append("key",Constants.GEOCODING_API_KEY)
            parameters.append("address",formatAddress)
        }
    }
    if(response.status.value != 200)
        return  null;

    val result  = json.decodeFromString<AddresToGeocodeResponse>(response.bodyAsText()).results[0]

    val cityComponent = result.addressComponents.find {
        it.types.contains("administrative_area_level_2") && it.types.contains("political")
    }

    val countryComponent = result.addressComponents.find {
        it.types.contains("country") && it.types.contains("political")
    }

    val location = result.geometry.location

    if (cityComponent != null && countryComponent != null) {
        return City(
            cityName = cityComponent.longName,
            countryCode = countryComponent.shortName,
            location = Location(location.latitude, location.longitude),
        )
    } else {
        return null
    }
}
