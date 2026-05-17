package com.weathersnap.data.repository

import com.weathersnap.data.remote.api.GeocodingApi
import com.weathersnap.data.remote.api.WeatherApi
import com.weathersnap.domain.model.City
import com.weathersnap.domain.model.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val geocodingApi: GeocodingApi,
    private val weatherApi: WeatherApi
) {
    suspend fun searchCity(name: String): List<City> = withContext(Dispatchers.IO) {
        val response = geocodingApi.searchCity(name)
        response.results?.map {
            City(
                name = it.name,
                latitude = it.latitude,
                longitude = it.longitude,
                country = it.country,
                admin1 = it.admin1
            )
        } ?: emptyList()
    }

    suspend fun getWeather(cityName: String, lat: Double, lon: Double): Weather = withContext(Dispatchers.IO) {
        val response = weatherApi.getWeather(lat, lon)
        val current = response.current
        Weather(
            cityName = cityName,
            temperature = current.temperature,
            condition = mapWeatherCode(current.weatherCode),
            humidity = current.humidity,
            windSpeed = current.windSpeed,
            pressure = current.pressure
        )
    }

    private fun mapWeatherCode(code: Int): String {
        return when (code) {
            0 -> "Clear Sky"
            1, 2, 3 -> "Partly Cloudy"
            45, 48 -> "Foggy"
            in 51..67 -> "Rainy"
            in 71..77 -> "Snowy"
            80, 81, 82 -> "Showers"
            95 -> "Thunderstorm"
            else -> "Unknown"
        }
    }
}
