package com.example.menuadvisor.navigation

import PlaceDetailScreen
import android.provider.ContactsContract.Profile
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
import com.example.menuadvisor.presentation.detail.CreateCommentScreen
import com.example.menuadvisor.presentation.detail.ProductDetailScreen
import com.example.menuadvisor.presentation.favorite.FavoritesScreen
import com.example.menuadvisor.presentation.home_screen.HomeScreen
import com.example.menuadvisor.presentation.profile.ProfileScreen

@Composable
fun NavGraph(startDestination: String = "login") {
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
        composable("favorites") {
            FavoritesScreen(navController = navController)
        }
        composable("placeDetailScreen/{placeId}") { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString("placeId")?.toInt()
            PlaceDetailScreen(placeId = placeId, navController = navController)
        }
        composable("profile") {
            ProfileScreen(navController = navController)
        }
        composable("productDetailScreen/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toInt()
            ProductDetailScreen(productId = productId, navController = navController)
        }
        composable("createcomment/{rating}/{productId}") { backStackEntry ->
            val rating = backStackEntry.arguments?.getString("rating")?.toInt() ?: 0
            val productId = backStackEntry.arguments?.getString("productId")?.toInt() ?: 0
            CreateCommentScreen(
                navController = navController,
                initialRating = rating,
                productId = productId
            )
        }
    }
}


