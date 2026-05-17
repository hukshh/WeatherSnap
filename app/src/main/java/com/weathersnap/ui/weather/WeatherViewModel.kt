package com.weathersnap.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.data.repository.WeatherRepository
import com.weathersnap.domain.model.City
import com.weathersnap.domain.model.Weather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WeatherUiState {
    object Idle : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val weather: Weather) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _suggestions = MutableStateFlow<List<City>>(emptyList())
    val suggestions: StateFlow<List<City>> = _suggestions.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val suggestionCache = HashMap<String, List<City>>()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.length > 2) {
            fetchSuggestions(query)
        } else {
            _suggestions.value = emptyList()
        }
    }

    private fun fetchSuggestions(query: String) {
        if (suggestionCache.containsKey(query)) {
            _suggestions.value = suggestionCache[query]!!
            return
        }

        viewModelScope.launch {
            try {
                val cities = repository.searchCity(query)
                suggestionCache[query] = cities
                _suggestions.value = cities
            } catch (e: Exception) {
                // Ignore suggestion errors for now
            }
        }
    }

    fun selectCity(city: City) {
        _searchQuery.value = city.name
        _suggestions.value = emptyList()
        fetchWeather(city)
    }

    private fun fetchWeather(city: City) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val weather = repository.getWeather(city.name, city.latitude, city.longitude)
                _uiState.value = WeatherUiState.Success(weather)
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun retry() {
        _uiState.value = WeatherUiState.Idle
        _searchQuery.value = ""
    }
}
