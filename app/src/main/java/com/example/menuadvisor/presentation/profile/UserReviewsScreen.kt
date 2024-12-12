package com.example.menuadvisor.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.menuadvisor.components.CustomNavigationBar
import com.example.menuadvisor.model.ReviewData
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserReviewsScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val userReviews by viewModel.userReviews.collectAsState()
    val productNames by viewModel.productNames.collectAsState()
    val placeNames by viewModel.placeNames.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yorumlarım") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        },
        bottomBar = {
            CustomNavigationBar(navController = navController, selectedTab = 3)
        }
    ) { paddingValues ->
        if (userReviews.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Henüz yorum yapmadınız",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(userReviews) { review ->
                    ReviewCard(
                        review = review,
                        productName = review.productId?.let { productNames[it] } ?: "Ürün bulunamadı",
                        placeName = review.productId?.let { placeNames[it] } ?: "Restoran bulunamadı",
                        onEditClick = {
                            review.productId?.let { productId ->
                                // Düzenleme için reviewId'yi de gönderiyoruz
                                navController.navigate("createcomment/${review.rate}/$productId?reviewId=${review.id}&initialComment=${review.description}&isEdit=true")
                            }
                        },
                        onDeleteClick = {
                            review.id?.let { reviewId ->
                                viewModel.deleteReview(reviewId)
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewCard(
    review: ReviewData,
    productName: String,
    placeName: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Ürün adı
            Text(
                text = productName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // Restoran adı
            Text(
                text = placeName,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Yorum tarihi
            review.created?.let { date ->
                val formattedDate = try {
                    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    formatter.format(parser.parse(date) ?: Date())
                } catch (e: Exception) {
                    date
                }
                Text(
                    text = formattedDate,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Yorum metni
            review.description?.let { description ->
                Text(
                    text = description,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sol tarafta puan ve fiyat
                Column {
                    // Puan
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Puan:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${review.rate ?: 0}",
                            fontSize = 14.sp
                        )
                    }

                    // Fiyat bilgisi varsa göster
                    review.price?.let { price ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Fiyat:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "$price ₺",
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Sağ tarafta düzenle ve sil butonları
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Düzenle",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Sil",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
} 