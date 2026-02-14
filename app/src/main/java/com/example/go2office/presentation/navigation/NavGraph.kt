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
import com.example.go2office.presentation.onboarding.OnboardingScreen
import com.example.go2office.presentation.permissions.PermissionsSetupScreen
import com.example.go2office.presentation.settings.SettingsScreen
import java.time.LocalDate

/**
 * Navigation graph for the app.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onNavigateToPermissions = {
                    navController.navigate(Screen.PermissionsSetup.route)
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
                }
            )
        }

        composable(Screen.AutoDetection.route) {
            AutoDetectionScreen(
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
    }
}

/**
 * Determine the start destination based on whether settings are configured.
 */
@Composable
fun getStartDestination(getSettings: GetOfficeSettingsUseCase): String {
    val settings by getSettings().collectAsState(initial = null)
    return if (settings == null) Screen.Onboarding.route else Screen.Dashboard.route
}

