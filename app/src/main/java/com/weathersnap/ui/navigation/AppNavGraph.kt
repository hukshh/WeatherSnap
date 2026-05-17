package com.weathersnap.ui.navigation

import android.net.Uri
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.weathersnap.ui.camera.CameraScreen
import com.weathersnap.ui.createreport.CreateReportScreen
import com.weathersnap.ui.reports.ReportsScreen
import com.weathersnap.ui.weather.WeatherScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Weather.route,
        enterTransition = { slideInHorizontally { it } + fadeIn() },
        exitTransition = { slideOutHorizontally { -it } + fadeOut() },
        popEnterTransition = { slideInHorizontally { -it } + fadeIn() },
        popExitTransition = { slideOutHorizontally { it } + fadeOut() }
    ) {
        composable(Screen.Weather.route) {
            WeatherScreen(
                onNavigateToCreateReport = { weather ->
                    val weatherJson = Uri.encode(Gson().toJson(weather))
                    navController.navigate(Screen.CreateReport.createRoute(weatherJson))
                },
                onNavigateToReports = {
                    navController.navigate(Screen.SavedReports.route)
                }
            )
        }

        composable(
            route = Screen.CreateReport.route,
            arguments = listOf(navArgument(Screen.CreateReport.ARG_WEATHER_JSON) { type = NavType.StringType })
        ) {
            CreateReportScreen(
                onNavigateToCamera = {
                    navController.navigate(Screen.Camera.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onReportSaved = {
                    navController.navigate(Screen.SavedReports.route) {
                        popUpTo(Screen.Weather.route) { inclusive = false }
                    }
                },
                navController = navController
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                onImageCaptured = { originalPath, compressedPath, original, compressed ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("originalPath", originalPath)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("imagePath", compressedPath)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("originalSize", original)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("compressedSize", compressed)
                    navController.popBackStack()
                },
                onClose = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.SavedReports.route) {
            ReportsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
