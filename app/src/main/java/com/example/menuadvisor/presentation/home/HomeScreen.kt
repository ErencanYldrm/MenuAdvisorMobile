package com.example.menuadvisor.presentation.home_screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavController
import com.example.menuadvisor.R
import com.example.menuadvisor.components.CustomNavigationBar
import com.example.menuadvisor.components.ProductItem
import com.example.menuadvisor.components.SearchButton

//experimental
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    context: Context = LocalContext.current
) {
    var selectedTab by remember { mutableStateOf("Restaurants") }
        LazyColumn (modifier = Modifier.fillMaxSize()) {


            item {
                SearchButton(navController)
            }
            item {
                // Campaign/Announcement Banner
                Image(
                    painter = painterResource(id = R.drawable.ads_img),
                    contentDescription = "Announcement",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }

            item {
                // Tabs for "Restaurants" and "Products"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Restaurants",
                        modifier = Modifier.clickable { selectedTab = "Restaurants" },
                        style = if (selectedTab == "Restaurants") TextStyle(
                            color = Color.Blue,
                            fontSize = 20.sp
                        ) else TextStyle(color = Color.Gray, fontSize = 20.sp)
                    )
                    Text(
                        text = "Products",
                        modifier = Modifier.clickable { selectedTab = "Products" },
                        style = if (selectedTab == "Products") TextStyle(
                            color = Color.Blue,
                            fontSize = 20.sp
                        ) else TextStyle(color = Color.Gray, fontSize = 20.sp)
                    )
                }
            }

            item {
                // Display content based on selected tab
                when (selectedTab) {
                    "Restaurants" -> {
                        RestaurantSection(navController)
                    }

                    "Products" -> {
                        ProductSection(navController)
                    }
                }
            }

        }
    CustomNavigationBar(navController = navController)
    }

@Composable
fun RestaurantSection(
    navController: NavController,
) {
    // Mock data for restaurants
    val restaurants = listOf(
        PlaceData("Starbucks - Kültür", "4.2"),
        PlaceData("McDonald's - Center", "3.9"),
        PlaceData("Pizza Hut - City Mall", "4.5"),
        PlaceData("Burger King - Antalya", "4.1"),
        PlaceData("Pizza Hut - City Mall", "4.5"),
        PlaceData("Burger King - Antalya", "4.1"),
        PlaceData("Pizza Hut - City Mall", "4.5"),
        PlaceData("Burger King - Antalya", "4.1"),
    )

    Column(
        modifier = Modifier
            .height(900.dp) // Sabit bir yükseklik belirleniyor
    ) {
        LazyColumn{
            items(restaurants.size) { index ->
                ProductItem(
                    title = restaurants[index].name,
                    image = "",
                    placeNameOrDistance = "1.2 km",
                    rate = restaurants[index].rating,
                    onClick = {}
                )
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
        PlaceData("Turkish Coffee", "4.6"),
        PlaceData("Latte", "4.3"),
        PlaceData("Cappuccino", "4.5")
    )
    Column(
        modifier = Modifier
            .height(600.dp) // Sabit bir yükseklik belirleniyor
    ) {

        LazyRow{
            items(products.size) { index ->
                ProductItem(
                    title = products[index].name,
                    image = "",
                    placeNameOrDistance = "1.2 km",
                    rate = products[index].rating,
                    onClick = {}
                )
            }
        }
    }
}

// Placeholder data class for Place items
data class PlaceData(
    val name: String,
    val rating: String
)
