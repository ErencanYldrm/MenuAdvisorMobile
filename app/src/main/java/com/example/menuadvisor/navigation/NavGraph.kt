package com.example.menuadvisor.navigation

import PlaceDetailScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.menuadvisor.presentation.auth.OnboardingScreen
import com.example.menuadvisor.presentation.auth_screens.LoginScreen
import com.example.menuadvisor.presentation.auth_screens.SignUpScreen
import com.example.menuadvisor.presentation.detail.CreateCommentScreen
import com.example.menuadvisor.presentation.detail.ProductDetailScreen
import com.example.menuadvisor.presentation.favorite.FavoritesScreen
import com.example.menuadvisor.presentation.find.FindScreen
import com.example.menuadvisor.presentation.home_screen.HomeScreen
import com.example.menuadvisor.presentation.profile.ProfileScreen
import com.example.menuadvisor.presentation.profile.UserReviewsScreen
import com.example.menuadvisor.presentation.search.SearchScreen

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
        composable("find") {
            FindScreen(navController = navController)
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
        composable("userReviews") {
            UserReviewsScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }
        composable("productDetailScreen/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toInt()
            ProductDetailScreen(productId = productId, navController = navController)
        }
        composable(
            route = "createcomment/{rating}/{productId}?reviewId={reviewId}&initialComment={initialComment}&isEdit={isEdit}",
            arguments = listOf(
                navArgument("rating") { type = NavType.IntType },
                navArgument("productId") { type = NavType.IntType },
                navArgument("reviewId") { 
                    type = NavType.StringType
                    defaultValue = null
                    nullable = true 
                },
                navArgument("initialComment") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("isEdit") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val rating = backStackEntry.arguments?.getInt("rating") ?: 0
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            val reviewId = backStackEntry.arguments?.getString("reviewId")?.toIntOrNull()
            val initialComment = backStackEntry.arguments?.getString("initialComment") ?: ""
            val isEdit = backStackEntry.arguments?.getBoolean("isEdit") ?: false

            CreateCommentScreen(
                navController = navController,
                initialRating = rating,
                productId = productId,
                reviewId = reviewId,
                initialComment = initialComment,
                isEdit = isEdit
            )
        }
        composable(
            route = "search"
        ) {
            SearchScreen(navController = navController)
        }
    }
}
