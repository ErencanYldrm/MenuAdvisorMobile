package com.example.menuadvisor.presentation.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.menuadvisor.components.CustomNavigationBar
import com.example.menuadvisor.presentation.auth.AuthViewModel
import com.example.menuadvisor.presentation.favorite.FavoritesViewModel
import com.example.menuadvisor.presentation.favorite.ProductFavoriteViewModel
import com.example.menuadvisor.presentation.home.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    productFavoriteViewModel: ProductFavoriteViewModel = hiltViewModel()
) {
    var userNameState by remember { mutableStateOf<String?>(null) }
    val userName by viewModel.userName.observeAsState()
    val userId = homeViewModel.token.value ?: return
    val commentCount by profileViewModel.commentCount.collectAsState()
    val placeFavorites by favoritesViewModel.favorites.observeAsState(emptyList())
    val productFavorites by productFavoriteViewModel.getFavorites(userId).collectAsState(initial = emptyList())
    
    // Toplam favori sayısı
    val totalFavorites = (placeFavorites?.size ?: 0) + productFavorites.size

    LaunchedEffect(Unit) {
        viewModel.getUserName().collect { savedUserName ->
            Log.d("ProfileDebug", "UserPreferences userName: $savedUserName")
            userNameState = savedUserName
        }
        profileViewModel.getUserCommentCount()
    }

    Scaffold(
        bottomBar = {
            CustomNavigationBar(navController = navController, 3)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = userNameState ?: userName ?: "Kullanıcı",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 62.dp, bottom = 16.dp)
                )

                Spacer(modifier = Modifier.size(16.dp))

                ProfileButton(text = "Comments ($commentCount)") {
                    navController.navigate("userReviews")
                }
                Spacer(modifier = Modifier.size(16.dp))

                ProfileButton(text = "Favorites ($totalFavorites)") {
                    navController.navigate("favorites")
                }
            }

            // Çıkış Yap Butonu
            Button(
                onClick = {
                    viewModel.viewModelScope.launch {
                        viewModel.clearUserData()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF4B4B)
                ),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 80.dp)
            ) {
                Text(
                    text = "Çıkış Yap",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun ProfileButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFCEAD7)),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(6.dp)
        )
    }
}
