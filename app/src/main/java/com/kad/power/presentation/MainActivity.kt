package com.kad.power.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kad.power.presentation.ui.screens.CalculatorScreen
import com.kad.power.presentation.ui.screens.CatalogScreen
import com.kad.power.presentation.ui.screens.ContactScreen
import com.kad.power.presentation.ui.screens.HomeScreen
import com.kad.power.presentation.ui.theme.KADPowerTheme
import com.kad.power.presentation.viewmodel.SolarViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KADPowerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: SolarViewModel = hiltViewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToCalculator = { navController.navigate("calculator") },
                                onNavigateToCatalog = { navController.navigate("catalog") },
                                onNavigateToContact = { navController.navigate("contact") }
                            )
                        }
                        composable("calculator") {
                            CalculatorScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("catalog") {
                            CatalogScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("contact") {
                            ContactScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
