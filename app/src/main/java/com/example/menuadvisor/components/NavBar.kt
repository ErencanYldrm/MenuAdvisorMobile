package com.example.menuadvisor.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun CustomNavigationBar(navController: NavController) {
    var selected by remember { mutableStateOf(0) }

    BottomAppBar(
        containerColor = Color(0xFFF9F9F9),
        contentPadding = PaddingValues(0.dp)
    ) {
        NavigationBarItem(
            label = { Text("Home") },
            selected = selected == 0,
            onClick = {
                selected = 0
                navController.navigate("home_route")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (selected == 0) Color(0xFFFFFFFF) else Color.Black
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color(0xFF1E90FF),
                indicatorColor = Color(0xFF1E90FF)
            )
        )
        NavigationBarItem(
            label = { Text("Find") },
            selected = selected == 1,
            onClick = {
                selected = 1
                navController.navigate("find_route")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Find",
                    tint = if (selected == 1) Color(0xFF1E90FF) else Color.Black
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color(0xFF1E90FF),
                indicatorColor = Color(0xFF1E90FF)
            )
        )
        NavigationBarItem(
            label = { Text("Favorites") },
            selected = selected == 2,
            onClick = {
                selected = 2
                navController.navigate("favorites_route")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorites",
                    tint = if (selected == 2) Color(0xFF1E90FF) else Color.Black
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color(0xFF1E90FF),
                indicatorColor = Color(0xFF1E90FF)
            )
        )
        NavigationBarItem(
            label = { Text("Profile") },
            selected = selected == 3,
            onClick = {
                selected = 3
                navController.navigate("profile_route")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = if (selected == 3) Color(0xFF1E90FF) else Color.Black
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color(0xFF1E90FF),
                indicatorColor = Color(0xFF1E90FF)
            )
        )
    }
}

@Preview
@Composable
fun CustomNavigationBarPreview() {
    CustomNavigationBar(navController = rememberNavController())
}
