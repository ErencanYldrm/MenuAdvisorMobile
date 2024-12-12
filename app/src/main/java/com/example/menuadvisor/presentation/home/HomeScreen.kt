package com.example.menuadvisor.presentation.home_screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.menuadvisor.R
import com.example.menuadvisor.components.CustomNavigationBar
import com.example.menuadvisor.components.ProductItem
import com.example.menuadvisor.components.SearchButton
import com.example.menuadvisor.model.PlaceData
import com.example.menuadvisor.model.ProductData
import com.example.menuadvisor.presentation.favorite.FavoritesViewModel
import com.example.menuadvisor.presentation.favorite.ProductFavoriteViewModel
import com.example.menuadvisor.presentation.home.HomeViewModel
import com.example.menuadvisor.utils.RequestLocationPermission
import com.example.menuadvisor.utils.LocationTest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    navController: NavController,
    context: Context = LocalContext.current
) {
    var selectedTab by remember { mutableStateOf("Cafes") }

    val token = viewModel.token.observeAsState()
    val places by viewModel.allPlaces.collectAsState()
    val products by viewModel.allProducts.collectAsState()
    val placeNames by viewModel.placeNames.collectAsState()
    val favorites by favoritesViewModel.favorites.observeAsState(emptyList())
    val placeDistances by viewModel.placeDistances.collectAsState()

    LaunchedEffect(Unit) {
        LocationTest.getEmulatorLocation(context)
    }

    var locationPermissionRequested by remember { mutableStateOf(false) }

    if (!locationPermissionRequested) {
        RequestLocationPermission { granted ->
            locationPermissionRequested = true
            if (granted) {
                viewModel.updateUserLocation(context)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(26.dp))
        SearchButton(navController)
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
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

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            item {
                Image(
                    painter = painterResource(id = R.drawable.ads_img),
                    contentDescription = "Announcement",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            item {
                when (selectedTab) {
                    "Cafes" -> {
                        RestaurantSection(
                            navController = navController,
                            restaurants = places,
                            favorites = favorites,
                            favoritesViewModel = favoritesViewModel,
                            viewModel = viewModel
                        )
                    }
                    "Products" -> {
                        ProductSection(
                            navController = navController,
                            products = products,
                            placeNames = placeNames,
                            favorites = favorites,
                            favoritesViewModel = favoritesViewModel,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
        
        CustomNavigationBar(navController = navController, 0)
    }
}

@Composable
fun RestaurantSection(
    navController: NavController,
    restaurants: List<PlaceData>,
    favorites: List<PlaceData>?,
    favoritesViewModel: FavoritesViewModel,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val reviewCounts by viewModel.placeReviewCounts.collectAsState()
    val placeDistances by viewModel.placeDistances.collectAsState()
    
    restaurants.forEach { place ->
        place.name?.let { name ->
            place.rating?.let { rating ->
                val isFavorited = favorites?.any { it.id == place.id } == true
                val reviewCount = place.id?.let { reviewCounts[it] } ?: 0
                val distance = place.id?.let { placeDistances[it] }
                
                if (rating != "-1") {  // rating -1 değilse göster
                    ProductItem(
                        title = name,
                        image = "",
                        placeNameOrDistance = if (distance != null) String.format("%.1f km", distance) else "Mesafe hesaplanıyor...",
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

@Composable
fun ProductSection(
    navController: NavController,
    products: List<ProductData>,
    placeNames: Map<Int, String>,
    favorites: List<PlaceData>?,
    favoritesViewModel: FavoritesViewModel,
    viewModel: HomeViewModel = hiltViewModel(),
    productFavoriteViewModel: ProductFavoriteViewModel = hiltViewModel()
) {
    val reviewCounts by viewModel.productReviewCounts.collectAsState()
    val userId = viewModel.token.value ?: return
    
    products.forEach { product ->
        val placeName = product.placeId?.let { placeNames[it] } ?: ""
        val productId = product.id ?: return@forEach
        val isFavorited by productFavoriteViewModel.isFavorite(userId, productId).collectAsState(initial = false)
        val reviewCount = product.id?.let { reviewCounts[it] } ?: 0
        
        ProductItem(
            title = product.name ?: "",
            image = product.image ?: "",
            placeNameOrDistance = placeName,
            rate = product.rate?.toString() ?: "0.0",
            reviewCount = reviewCount,
            isFavorited = isFavorited,
            onClick = {
                product.id?.let { id ->
                    navController.navigate("productDetailScreen/$id")
                }
            },
            onFavoriteClick = {
                if (isFavorited) {
                    productFavoriteViewModel.removeFavorite(userId, productId)
                } else {
                    productFavoriteViewModel.addFavorite(userId, productId)
                }
            }
        )
    }
}