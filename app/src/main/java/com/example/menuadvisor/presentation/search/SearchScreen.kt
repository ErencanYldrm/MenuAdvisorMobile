package com.example.menuadvisor.presentation.search

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.menuadvisor.components.CustomNavigationBar
import com.example.menuadvisor.components.ProductItem
import com.example.menuadvisor.model.PlaceData
import com.example.menuadvisor.model.ProductData
import com.example.menuadvisor.presentation.favorite.FavoritesViewModel
import com.example.menuadvisor.presentation.favorite.ProductFavoriteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    context: Context = LocalContext.current
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("Cafes") }
    val places by viewModel.places.collectAsState()
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val placeDistances by viewModel.placeDistances.collectAsState()
    val placeReviewCounts by viewModel.placeReviewCounts.collectAsState()
    val productReviewCounts by viewModel.productReviewCounts.collectAsState()
    val favorites by favoritesViewModel.favorites.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.updateUserLocation(context)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar with Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.search(it)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                placeholder = { Text("Search...") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Blue,
                    unfocusedBorderColor = Color.Gray
                )
            )
        }

        // Tabs
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

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                item {
                    when (selectedTab) {
                        "Cafes" -> {
                            places.forEach { place ->
                                place.name?.let { name ->
                                    place.rating?.let { rating ->
                                        val isFavorited = favorites?.any { it.id == place.id } == true
                                        val distance = place.id?.let { placeDistances[it] }

                                        if (rating != "-1") {
                                            ProductItem(
                                                title = name,
                                                image = "",
                                                placeNameOrDistance = if (distance != null) String.format("%.1f km", distance) else "Mesafe hesaplanıyor...",
                                                rate = rating,
                                                reviewCount = place.id?.let { placeReviewCounts[it] } ?: 0,
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
                        "Products" -> {
                            products.forEach { product ->
                                product.name?.let { name ->
                                    product.rate?.let { rating ->
                                        val isFavorited = favorites?.any { it.id == product.id } == true

                                        if (rating != -1.0) {
                                            ProductItem(
                                                title = name,
                                                image = product.image ?: "",
                                                placeNameOrDistance = product.name ?: "",
                                                rate = rating.toString(),
                                                reviewCount = product.id?.let { productReviewCounts[it] } ?: 0,
                                                isFavorited = isFavorited,
                                                onClick = {
                                                    product.id?.let { id ->
                                                        navController.navigate("productDetailScreen/$id")
                                                    }
                                                },
                                                onFavoriteClick = {
                                                    product.id?.let { productId ->
                                                        if (isFavorited) {
                                                            favorites?.find { it.id == productId }?.id?.let { favId ->
                                                                favoritesViewModel.removeFavorite(favId)
                                                            }
                                                        } else {
                                                            favoritesViewModel.addFavorite(productId)
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
        }

        CustomNavigationBar(navController = navController, 1)
    }
}
