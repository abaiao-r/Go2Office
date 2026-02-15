package com.example.go2office
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
                    val settings by getSettings().collectAsState(initial = null)

                    val startDestination = if (settings == null) {
                        Screen.Onboarding.route
                    } else {
                        Screen.Dashboard.route
                    }
                    NavGraph(
                        navController = navController,
                        startDestination = startDestination,
                        onboardingComplete = settings != null
                    )
                }
            }
        }
    }
}
