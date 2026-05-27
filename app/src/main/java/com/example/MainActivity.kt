package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.MainIdeScreen
import com.example.ui.OrinRoutes
import com.example.ui.OrinViewModel
import com.example.ui.SplashScreen
import com.example.ui.WelcomeScreen
import com.example.ui.theme.MyApplicationTheme

/**
 * Orin IDE Main Controller
 * Establishes EdgeToEdge graphics context and launches Compose navigation structures.
 * Designed exactly as a professional premium dark development console.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val viewModel: OrinViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = OrinRoutes.SPLASH,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. Splash cinematic entry
                    composable(OrinRoutes.SPLASH) {
                        SplashScreen(
                            onNavigateToWelcome = {
                                navController.navigate(OrinRoutes.WELCOME) {
                                    popUpTo(OrinRoutes.SPLASH) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 2. Developer Onboarding Hub welcome deck
                    composable(OrinRoutes.WELCOME) {
                        WelcomeScreen(
                            viewModel = viewModel,
                            onNavigateToMainIde = {
                                navController.navigate(OrinRoutes.MAIN_IDE)
                            }
                        )
                    }

                    // 3. Workstation Core Integrated view
                    composable(OrinRoutes.MAIN_IDE) {
                        MainIdeScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                navController.navigate(OrinRoutes.WELCOME) {
                                    popUpTo(OrinRoutes.MAIN_IDE) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
