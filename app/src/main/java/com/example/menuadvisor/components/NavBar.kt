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
fun CustomNavigationBar(
    navController: NavController,
    selectedTab: Int
) {
    BottomAppBar(
        containerColor = Color(0xFFF9F9F9),
        contentPadding = PaddingValues(0.dp)
    ) {
        NavigationBarItem(
            label = { Text("Home") },
            selected = selectedTab == 0,
            onClick = {
                navController.navigate("home")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (selectedTab == 0) Color(0xFFFFFFFF) else Color.Black
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFFFFFF),
                selectedTextColor = Color(0xFF1E90FF),
                indicatorColor = Color(0xFF1E90FF)
            )
        )
        NavigationBarItem(
            label = { Text("Find") },
            selected = selectedTab == 1,
            onClick = {
                navController.navigate("find")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Find",
                    tint = if (selectedTab == 1) Color(0xFFFFFFFF) else Color.Black
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFFFFFF),
                selectedTextColor = Color(0xFF1E90FF),
                indicatorColor = Color(0xFF1E90FF)
            )
        )
        NavigationBarItem(
            label = { Text("Favorites") },
            selected = selectedTab == 2,
            onClick = {
                navController.navigate("favorites")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorites",
                    tint = if (selectedTab == 2) Color(0xFFFFFFFF) else Color.Black
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFFFFFF),
                selectedTextColor = Color(0xFF1E90FF),
                indicatorColor = Color(0xFF1E90FF)
            )
        )
        NavigationBarItem(
            label = { Text("Profile") },
            selected = selectedTab == 3,
            onClick = {
                navController.navigate("profile")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = if (selectedTab == 3) Color(0xFFFFFFFF) else Color.Black
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFFFFFFF),
                selectedTextColor = Color(0xFF1E90FF),
                indicatorColor = Color(0xFF1E90FF)
            )
        )
    }
}

