package com.example.go2office.presentation.navigation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.go2office.domain.usecase.GetOfficeSettingsUseCase
import com.example.go2office.presentation.autodetection.AutoDetectionScreen
import com.example.go2office.presentation.calendar.AnnualCalendarScreen
import com.example.go2office.presentation.dashboard.DashboardScreen
import com.example.go2office.presentation.dayentry.DayEntryScreen
import com.example.go2office.presentation.history.MonthlyHistoryScreen
import com.example.go2office.presentation.map.MapLocationPickerScreen
import com.example.go2office.presentation.onboarding.OnboardingScreen
import com.example.go2office.presentation.permissions.PermissionsSetupScreen
import com.example.go2office.presentation.settings.SettingsScreen
import com.example.go2office.presentation.splash.SplashScreen
import java.time.LocalDate
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    onboardingComplete: Boolean = false
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    val destination = if (onboardingComplete) {
                        Screen.Dashboard.route
                    } else {
                        startDestination
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Onboarding.route) { backStackEntry ->
            val savedStateHandle = backStackEntry.savedStateHandle
            val selectedLat = savedStateHandle.get<Double>("selected_lat")
            val selectedLon = savedStateHandle.get<Double>("selected_lon")
            val selectedName = savedStateHandle.get<String>("selected_name")

            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onNavigateToPermissions = {
                    navController.navigate(Screen.PermissionsSetup.route)
                },
                onNavigateToMapPicker = { lat, lon ->
                    navController.navigate(Screen.MapLocationPicker.createRoute(lat, lon))
                },
                selectedMapLocation = if (selectedLat != null && selectedLon != null) {
                    Triple(selectedLat, selectedLon, selectedName ?: "Selected Location")
                } else null,
                onMapLocationConsumed = {
                    savedStateHandle.remove<Double>("selected_lat")
                    savedStateHandle.remove<Double>("selected_lon")
                    savedStateHandle.remove<String>("selected_name")
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToDayEntry = { date ->
                    navController.navigate(Screen.DayEntry.createRoute(date))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.MonthlyHistory.route)
                }
            )
        }
        composable(
            route = Screen.DayEntry.route,
            arguments = listOf(
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dateString = backStackEntry.arguments?.getString("date")
            val date = dateString?.let { LocalDate.parse(it) } ?: LocalDate.now()
            DayEntryScreen(
                date = date,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAutoDetection = {
                    navController.navigate(Screen.AutoDetection.route)
                },
                onNavigateToAnnualCalendar = {
                    navController.navigate(Screen.AnnualCalendar.route)
                },
                onNavigateToPermissions = {
                    navController.navigate(Screen.PermissionsSetup.route)
                }
            )
        }
        composable(Screen.AutoDetection.route) { backStackEntry ->
            val savedStateHandle = backStackEntry.savedStateHandle
            val selectedLat = savedStateHandle.get<Double>("selected_lat")
            val selectedLon = savedStateHandle.get<Double>("selected_lon")
            val selectedName = savedStateHandle.get<String>("selected_name")

            AutoDetectionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToPermissions = {
                    navController.navigate(Screen.PermissionsSetup.route)
                },
                onNavigateToMapPicker = { lat, lon ->
                    navController.navigate(Screen.MapLocationPicker.createRoute(lat, lon))
                },
                selectedMapLocation = if (selectedLat != null && selectedLon != null) {
                    Triple(selectedLat, selectedLon, selectedName ?: "Selected Location")
                } else null,
                onMapLocationConsumed = {
                    savedStateHandle.remove<Double>("selected_lat")
                    savedStateHandle.remove<Double>("selected_lon")
                    savedStateHandle.remove<String>("selected_name")
                }
            )
        }
        composable(
            route = Screen.MapLocationPicker.route,
            arguments = listOf(
                navArgument("lat") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("lon") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
            val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull()
            MapLocationPickerScreen(
                initialLatitude = lat,
                initialLongitude = lon,
                onLocationSelected = { latitude, longitude, name ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("selected_lat", latitude)
                    navController.previousBackStackEntry?.savedStateHandle?.set("selected_lon", longitude)
                    navController.previousBackStackEntry?.savedStateHandle?.set("selected_name", name)
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.AnnualCalendar.route) {
            AnnualCalendarScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.PermissionsSetup.route) {
            PermissionsSetupScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAllPermissionsGranted = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.MonthlyHistory.route) {
            MonthlyHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDayEntry = { date ->
                    navController.navigate(Screen.DayEntry.createRoute(date))
                }
            )
        }
    }
}
@Composable
fun getStartDestination(getSettings: GetOfficeSettingsUseCase): String {
    val settings by getSettings().collectAsState(initial = null)
    return if (settings == null) Screen.Onboarding.route else Screen.Dashboard.route
}
