package com.example.go2office
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.go2office.domain.usecase.GetOfficeSettingsUseCase
import com.example.go2office.presentation.navigation.NavGraph
import com.example.go2office.presentation.navigation.Screen
import com.example.go2office.ui.theme.Go2OfficeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var getSettings: GetOfficeSettingsUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Go2OfficeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Track if we've determined the initial destination
                    var hasCheckedSettings by remember { mutableStateOf(false) }
                    var onboardingComplete by remember { mutableStateOf(false) }

                    // Check settings once on startup
                    LaunchedEffect(Unit) {
                        val settings = getSettings.once()
                        onboardingComplete = settings != null
                        hasCheckedSettings = true
                    }

                    val startDestination = if (onboardingComplete) {
                        Screen.Dashboard.route
                    } else {
                        Screen.Onboarding.route
                    }

                    // Only show NavGraph after we've checked settings
                    if (hasCheckedSettings) {
                        NavGraph(
                            navController = navController,
                            startDestination = startDestination,
                            onboardingComplete = onboardingComplete
                        )
                    }
                }
            }
        }
    }
}
