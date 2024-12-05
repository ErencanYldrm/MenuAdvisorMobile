package com.example.menuadvisor.presentation.profile

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.navigation.NavController
import com.example.menuadvisor.components.CustomNavigationBar
import com.example.menuadvisor.presentation.auth.AuthViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var userNameState by remember { mutableStateOf<String?>(null) }
    val userName by viewModel.userName.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.getUserName().collect { savedUserName ->
            Log.d("ProfileDebug", "UserPreferences userName: $savedUserName")
            userNameState = savedUserName
        }
    }

    Scaffold(
        bottomBar = {
            CustomNavigationBar(navController = navController,3)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.White),
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

            ProfileButton(text = "Comments (2)") {
                // Handle Comments button click here
            }
            Spacer(modifier = Modifier.size(16.dp))

            ProfileButton(text = "Favorites (1)") {
                // Handle Favorites button click here
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
