package com.example.menuadvisor.presentation.auth
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun OnboardingScreen(navController: NavController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.BottomCenter
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Görsel için Image composable'ı
//            Image(
//                painter = painterResource(id = R.drawable.onboarding_image), // Görsel kaynağını buraya ekleyin
//                contentDescription = "Onboarding Image",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f)
//                    .padding(bottom = 16.dp),
//                contentScale = ContentScale.Crop
//            )

            // Açıklama metinleri
            Text(
                text = "Discover the delicious around you",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 35.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Discover the flavors around you with our mobile application designed for Coffee Lovers. Easily find Cafes and experience new tastes.",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Start Button
            Button(
                onClick = {
                    navController.navigate("login")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text(text = "Start", color = Color.White)
            }

            Spacer(modifier = Modifier.height(36.dp))

        }
    }
}

@Preview
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(navController = rememberNavController())
}

