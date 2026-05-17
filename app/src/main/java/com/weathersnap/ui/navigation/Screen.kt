package com.weathersnap.ui.navigation

sealed class Screen(val route: String) {
    object Weather : Screen("weather")
    object CreateReport : Screen("create_report/{weatherJson}") {
        const val ARG_WEATHER_JSON = "weatherJson"
        fun createRoute(weatherJson: String) = "create_report/$weatherJson"
    }
    object Camera : Screen("camera")
    object SavedReports : Screen("saved_reports")
}
