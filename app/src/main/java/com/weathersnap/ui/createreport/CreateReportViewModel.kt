package com.weathersnap.ui.createreport

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.weathersnap.data.repository.ReportRepository
import com.weathersnap.domain.model.Report
import com.weathersnap.domain.model.Weather
import com.weathersnap.ui.navigation.Screen
import com.weathersnap.util.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateReportViewModel @Inject constructor(
    private val repository: ReportRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val weatherJson: String = checkNotNull(savedStateHandle[Screen.CreateReport.ARG_WEATHER_JSON])
    val weather: Weather = Gson().fromJson(weatherJson, Weather::class.java)

    val notes = savedStateHandle.getStateFlow("notes", "")
    val imagePath = savedStateHandle.getStateFlow<String?>("imagePath", null)
    val originalPath = savedStateHandle.getStateFlow<String?>("originalPath", null)
    val originalSize = savedStateHandle.getStateFlow("originalSize", 0L)
    val compressedSize = savedStateHandle.getStateFlow("compressedSize", 0L)

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    fun onNotesChange(newNotes: String) {
        savedStateHandle["notes"] = newNotes
    }

    fun onImageCaptured(origPath: String, compPath: String, original: Long, compressed: Long) {
        savedStateHandle["originalPath"] = origPath
        savedStateHandle["imagePath"] = compPath
        savedStateHandle["originalSize"] = original
        savedStateHandle["compressedSize"] = compressed
    }

    fun saveReport(onSuccess: () -> Unit) {
        val currentImagePath = imagePath.value ?: return
        viewModelScope.launch {
            val report = Report(
                cityName = weather.cityName,
                temperature = weather.temperature,
                condition = weather.condition,
                humidity = weather.humidity,
                windSpeed = weather.windSpeed,
                pressure = weather.pressure,
                imagePath = currentImagePath,
                originalSizeKb = originalSize.value,
                compressedSizeKb = compressedSize.value,
                notes = notes.value,
                timestamp = System.currentTimeMillis()
            )
            repository.saveReport(report)
            _isSaved.value = true
            
            // Delete original uncompressed file
            originalPath.value?.let { FileUtils.deleteFile(it) }
            
            onSuccess()
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (!_isSaved.value) {
            imagePath.value?.let { FileUtils.deleteFile(it) }
            originalPath.value?.let { FileUtils.deleteFile(it) }
        }
    }
}
