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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.menuadvisor.R
import com.example.menuadvisor.components.CustomNavigationBar
import com.example.menuadvisor.components.ProductItem
import com.example.menuadvisor.components.SearchButton
import com.example.menuadvisor.data.UserPreferences
import com.example.menuadvisor.presentation.home.HomeViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.example.menuadvisor.components.ProductItemPreview
import javax.inject.Inject
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
    context: Context = LocalContext.current
) {
    var selectedTab by remember { mutableStateOf("Cafes") }


    val token = viewModel.token.observeAsState()

    Log.d("HomeScreen", "Token: $token")

    val places by viewModel.allPlaces.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.height(26.dp))
            SearchButton(navController)
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    Image(
                        painter = painterResource(id = R.drawable.ads_img),
                        contentDescription = "Announcement",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }

                stickyHeader {
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
                                modifier = Modifier.clickable { selectedTab = "Restaurants" },
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

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Around You",
                                color = Color.Gray,
                                modifier = Modifier.clickable { /* Around You action */ }
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.filter),
                                contentDescription = "Filter Icon",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { /* Filter action */ }
                            )
                        }
                    }
                }

                item {
                    when (selectedTab) {
                        "Cafes" -> {
                            RestaurantSection(navController,places)
                        }

                        "Products" -> {
                            ProductItem(
                                title = "Latte",
                                image = "",
                                placeNameOrDistance = "Mado Beach Park",
                                rate = "4.6",
                                isFavorited = true,
                                onClick = {}
                            )
                            ProductItem(
                                title = "Cappuccino",
                                image = "",
                                placeNameOrDistance = "Mado Beach Park",
                                rate = "4.6",
                                isFavorited = false,
                                onClick = {}
                            )
                            ProductItem(
                                title = "Espresso",
                                image = "",
                                placeNameOrDistance = "Mado Beach Park",
                                rate = "4.6",
                                isFavorited = false,
                                onClick = {}
                            )
                            ProductItem(
                                title = "Mocha",
                                image = "",
                                placeNameOrDistance = "Mado Beach Park",
                                rate = "4.6",
                                isFavorited = false,
                                onClick = {}
                            )
                            ProductItem(
                                title = "Americano",
                                image = "",
                                placeNameOrDistance = "Mado Beach Park",
                                rate = "4.6",
                                isFavorited = false,
                                onClick = {}
                            )
                            ProductItem(
                                title = "Macchiato",
                                image = "",
                                placeNameOrDistance = "Mado Beach Park",
                                rate = "4.6",
                                isFavorited = false,
                                onClick = {}
                            )

                        }
                    }
                }
            }
            CustomNavigationBar(navController = navController,0)
        }
    }
}





@Composable
fun RestaurantSection(
    navController: NavController,
    restaurants: List<com.example.menuadvisor.model.PlaceData>
) {


    Column(
        modifier = Modifier
            .height(900.dp) // Sabit bir yükseklik belirleniyor
    ) {
        LazyColumn{
            items(restaurants.size) { index ->
                restaurants[index].name?.let {
                    restaurants[index].rating?.let { it1 ->
                        ProductItem(
                            title = it,
                            image = "",
                            placeNameOrDistance = "1.2 km",
                            rate = it1,
                            isFavorited = true,
                                onClick = {
                                navController.navigate("placeDetailScreen/${restaurants[index].id}")
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ProductSection(
    navController: NavController,
) {
    // Mock data for products
    val products = listOf(
        PlaceData("Latte", "4.6")
    )

    Column(
        modifier = Modifier
            .height(900.dp) // Sabit bir yükseklik belirleniyor
    ) {

        LazyColumn {
            items(products.size) { index ->
                products[index].name?.let {
                    products[index].rating?.let { it1 ->
                        ProductItem(
                            title = it,
                            image = "",
                            placeNameOrDistance = "Mado Beach Park",
                            rate = it1,
                            isFavorited = true,
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}

// Placeholder data class for Place items
data class PlaceData(
    val name: String,
    val rating: String
)

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371 // Radius of the earth in km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * acos(Math.sqrt(a))
    return earthRadius * c
}


fun getSortedPlaces(userLat: Double, userLon: Double, places: List<com.example.menuadvisor.model.PlaceData>): List<com.example.menuadvisor.model.PlaceData> {
    return places.sortedBy { place ->
        place.lat?.let { place.lon?.let { it1 -> calculateDistance(userLat, userLon, it.toDouble(), it1.toDouble()) } }
    }
}