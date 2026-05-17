package com.weathersnap.domain.model

data class Report(
    val id: Int = 0,
    val cityName: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double,
    val imagePath: String,
    val originalSizeKb: Long,
    val compressedSizeKb: Long,
    val notes: String,
    val timestamp: Long
)
