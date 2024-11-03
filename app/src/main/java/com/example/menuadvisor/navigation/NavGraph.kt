package com.example.menuadvisor.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.menuadvisor.presentation.auth.OnboardingScreen
import com.example.menuadvisor.presentation.auth_screens.LoginScreen
import com.example.menuadvisor.presentation.auth_screens.SignUpScreen
import com.example.menuadvisor.presentation.home_screen.HomeScreen

@Composable
fun NavGraph(startDestination: String = "onboarding") {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("onboarding") {
            OnboardingScreen(navController = navController)
        }
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("signup") {
            SignUpScreen(navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }


    }
}


