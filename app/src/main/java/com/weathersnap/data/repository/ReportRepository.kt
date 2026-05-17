package com.weathersnap.data.repository

import com.weathersnap.data.local.dao.ReportDao
import com.weathersnap.data.local.entity.ReportEntity
import com.weathersnap.domain.model.Report
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val reportDao: ReportDao
) {
    fun getAllReports(): Flow<List<Report>> {
        return reportDao.getAllReports().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun saveReport(report: Report) = withContext(Dispatchers.IO) {
        reportDao.insertReport(report.toEntity())
    }

    private fun ReportEntity.toDomain(): Report = Report(
        id = id,
        cityName = cityName,
        temperature = temperature,
        condition = condition,
        humidity = humidity,
        windSpeed = windSpeed,
        pressure = pressure,
        imagePath = imagePath,
        originalSizeKb = originalSizeKb,
        compressedSizeKb = compressedSizeKb,
        notes = notes,
        timestamp = timestamp
    )

    private fun Report.toEntity(): ReportEntity = ReportEntity(
        cityName = cityName,
        temperature = temperature,
        condition = condition,
        humidity = humidity,
        windSpeed = windSpeed,
        pressure = pressure,
        imagePath = imagePath,
        originalSizeKb = originalSizeKb,
        compressedSizeKb = compressedSizeKb,
        notes = notes,
        timestamp = timestamp
    )
}
