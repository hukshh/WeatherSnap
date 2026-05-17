package com.weathersnap.data.remote.api

import com.weathersnap.data.remote.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,wind_speed_10m,surface_pressure,weathercode",
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}
