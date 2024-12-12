package com.example.menuadvisor.presentation.detail

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.menuadvisor.model.ReviewRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCommentScreen(
    navController: NavController? = null,
    viewModel: CreateCommentViewModel = hiltViewModel(),
    initialRating: Int = 0,
    productId: Int = 0
) {
    var rating by remember { mutableStateOf(initialRating) }
    var comment by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val reviewResponse by viewModel.reviewResponse.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.productId.value = productId
    }

    LaunchedEffect(reviewResponse) {
        reviewResponse?.let { response ->
            if (response.succeeded == true) {
                Toast.makeText(context, "Yorum başarıyla gönderildi", Toast.LENGTH_SHORT).show()
                navController?.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
                navController?.popBackStack()
            } else {
                Toast.makeText(context, response.message ?: "Bir hata oluştu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.clickable { navController?.popBackStack() }
            )
            Text(
                text = "Add Review",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            // Boş view for alignment
            Box(modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Enter Review",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Rating
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${rating}.0",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Row {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = if (index < rating) Color(0xFFFFD02B) else Color.LightGray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { rating = index + 1 }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Comment TextField
        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text("Write your personal experience here") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFFFFD02B)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Add Photo Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp))
                .clickable { /* TODO: Add photo */ },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Photo",
                    tint = Color(0xFFFFD02B),
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "Add a product photo",
                    color = Color(0xFFFFD02B),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }

        // Post Button
        Button(
            onClick = {
                if (comment.isBlank()) {
                    Toast.makeText(context, "Lütfen bir yorum yazın", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                viewModel.viewModelScope.launch {
                    val reviewRequest = ReviewRequest(
                        description = comment,
                        rate = rating,
                        image = viewModel.image.value,
                        productId = viewModel.productId.value ?: 0,
                        createdBy = viewModel.userId.value ?: ""
                    )
                    viewModel.postReview(reviewRequest)
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A55A7)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Post")
            }
        }
    }
}
