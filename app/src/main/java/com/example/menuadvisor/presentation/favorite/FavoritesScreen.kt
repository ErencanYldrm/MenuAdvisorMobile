package com.example.menuadvisor.presentation.favorite

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.menuadvisor.R
import com.example.menuadvisor.api.FavoritesService
import com.example.menuadvisor.components.CustomNavigationBar
import com.example.menuadvisor.components.ProductItem
import com.example.menuadvisor.components.SearchButton
import com.example.menuadvisor.model.PlaceData
import com.example.menuadvisor.presentation.auth.AuthViewModel
import com.example.menuadvisor.presentation.home.HomeViewModel
import com.example.menuadvisor.presentation.home_screen.ProductSection
import com.example.menuadvisor.presentation.home_screen.RestaurantSection
import com.example.menuadvisor.repository.FavoritesRepository

@Composable
fun FavoritesScreen(
    navController: NavController,
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    productFavoriteViewModel: ProductFavoriteViewModel = hiltViewModel()
) {
    val userId = homeViewModel.token.value ?: return
    val places by homeViewModel.allPlaces.collectAsState()
    val products by homeViewModel.allProducts.collectAsState()
    val placeNames by homeViewModel.placeNames.collectAsState()
    val favorites by favoritesViewModel.favorites.observeAsState(emptyList())
    val productFavorites by productFavoriteViewModel.getFavorites(userId).collectAsState(initial = emptyList())
    var selectedTab by remember { mutableStateOf("Cafes") }

    // Favori olan ürünleri filtrele
    val favoriteProducts = products.filter { product ->
        product.id?.let { productId ->
            productFavorites.any { it.productId == productId }
        } ?: false
    }

    Scaffold(
        bottomBar = {
            CustomNavigationBar(navController = navController, selectedTab = 2)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(top = 6.dp)
        ) {
            SearchButton(navController)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Favorites",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = "Cafes",
                            modifier = Modifier.clickable { selectedTab = "Cafes" },
                            style = if (selectedTab == "Cafes") TextStyle(
                                color = Color.Blue,
                                fontSize = 18.sp
                            ) else TextStyle(color = Color.Gray, fontSize = 18.sp)
                        )
                        Text(
                            text = "Products",
                            modifier = Modifier.clickable { selectedTab = "Products" },
                            style = if (selectedTab == "Products") TextStyle(
                                color = Color.Blue,
                                fontSize = 18.sp
                            ) else TextStyle(color = Color.Gray, fontSize = 18.sp)
                        )
                    }
                }

                item {
                    when (selectedTab) {
                        "Cafes" -> {
                            if (favorites.isNullOrEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No favorite restaurant found.",
                                        color = Color.Gray,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(top = 32.dp)
                                    )
                                }
                            } else {
                                FavoriteRestaurantSection(
                                    navController = navController,
                                    restaurants = favorites!!,
                                    favorites = favorites,
                                    favoritesViewModel = favoritesViewModel,
                                    viewModel = homeViewModel
                                )
                            }
                        }
                        "Products" -> {
                            if (favoriteProducts.isEmpty()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No favorite product found.",
                                        color = Color.Gray,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(top = 32.dp)
                                    )
                                }
                            } else {
                                ProductSection(
                                    navController = navController,
                                    products = favoriteProducts,
                                    placeNames = placeNames,
                                    favorites = favorites,
                                    favoritesViewModel = favoritesViewModel,
                                    viewModel = homeViewModel,
                                    productFavoriteViewModel = productFavoriteViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteRestaurantSection(
    navController: NavController,
    restaurants: List<PlaceData>,
    favorites: List<PlaceData>?,
    favoritesViewModel: FavoritesViewModel,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val reviewCounts by viewModel.placeReviewCounts.collectAsState()
    
    Column(
        modifier = Modifier.height(900.dp)
    ) {
        LazyColumn {
            items(restaurants.size) { index ->
                restaurants[index].let { place ->
                    place.name?.let { name ->
                        place.rating?.let { rating ->
                            val isFavorited = favorites?.any { it.id == place.id } == true
                            val reviewCount = place.id?.let { reviewCounts[it] } ?: 0
                            
                            if (rating != "-1") {  // rating -1 değilse göster
                                ProductItem(
                                    title = name,
                                    image = "",
                                    placeNameOrDistance = "",  // Mesafe bilgisini boş bırakıyoruz
                                    rate = rating,
                                    reviewCount = reviewCount,
                                    isFavorited = isFavorited,
                                    onClick = {
                                        place.id?.let { id ->
                                            navController.navigate("placeDetailScreen/$id")
                                        }
                                    },
                                    onFavoriteClick = {
                                        place.id?.let { placeId ->
                                            if (isFavorited) {
                                                favorites?.find { it.id == placeId }?.id?.let { favId ->
                                                    favoritesViewModel.removeFavorite(favId)
                                                }
                                            } else {
                                                favoritesViewModel.addFavorite(placeId)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}











