package com.example.menuadvisor.presentation.detail

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.menuadvisor.components.Base64Image
import com.example.menuadvisor.components.CustomNavigationBar
import com.example.menuadvisor.components.DetailAppBar
import com.example.menuadvisor.components.DetailAppBar2
import com.example.menuadvisor.components.PlaceLogo
import com.example.menuadvisor.components.RateCard
import com.example.menuadvisor.model.ProductData
import com.example.menuadvisor.model.ReviewData
import com.example.menuadvisor.presentation.favorite.FavoritesViewModel

@Composable
fun ProductDetailScreen(
    productId: Int? = null,
    navController: NavController? = null,
    viewModel: ProductDetailViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
) {
    val scrollState = rememberLazyListState()
    val product by viewModel.product.observeAsState()
    val reviewList by viewModel.commentList.observeAsState(emptyList())
    var selectedRating by remember { mutableStateOf(0) }
    var isFavorite by remember { mutableStateOf(false) }
    val favorites by favoritesViewModel.favorites.observeAsState(emptyList())
    
    // Favori durumunu kontrol et
    LaunchedEffect(favorites) {
        isFavorite = favorites?.any { it.id == productId } == true
    }

    // Yorum eklendiğinde sayfayı yenile
    LaunchedEffect(Unit) {
        navController?.currentBackStackEntry?.savedStateHandle?.get<Boolean>("refresh")?.let {
            if (it) {
                viewModel.getCommentsByProductId()
                navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("refresh")
            }
        }
    }

    LaunchedEffect(productId) {
        viewModel.productId.value = productId
        viewModel.getProduct()
        viewModel.getCommentsByProductId()
        favoritesViewModel.getFavorites()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .clickable { navController?.popBackStack() }
                )
                Text(
                    text = product?.name ?: "",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.Gray,
                    modifier = Modifier.clickable { 
                        if (isFavorite) {
                            // Favoriden çıkar
                            favorites?.find { it.id == productId }?.id?.let { favId ->
                                favoritesViewModel.removeFavorite(favId)
                            }
                        } else {
                            // Favoriye ekle
                            productId?.let { id ->
                                favoritesViewModel.addFavorite(id)
                            }
                        }
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = scrollState
        ) {
            // Ürün Görseli
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!product?.image.isNullOrBlank()) {
                        Base64Image(
                            base64String = product?.image ?: "",
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        PlaceLogo(
                            title = product?.name ?: "",
                            rate = "",
                            size = 200,
                            showRate = false
                        )
                    }
                }
            }

            // Rating Bölümü
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = String.format("%.2f", product?.rate ?: 0.0),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Row {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = null,
                                    tint = if (index < selectedRating) Color(0xFFFFD02B) else Color.LightGray,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable {
                                            selectedRating = index + 1
                                            navController?.navigate("createcomment/${index + 1}/${productId}")
                                        }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(${reviewList.size})",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        text = "Tap to Rate and Review",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Yorumlar
            items(reviewList) { review ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "User",
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = review.created?.substring(0, 10) ?: "",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = null,
                                tint = if (index < (review.rate ?: 0)) Color(0xFFFFD02B) else Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    if (!review.description.isNullOrEmpty()) {
                        Text(
                            text = review.description,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    review.image?.takeIf { it.isNotBlank() }?.let { imageBase64 ->
                        Base64Image(
                            base64String = imageBase64,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .padding(vertical = 4.dp)
                        )
                    }
                    Divider(
                        modifier = Modifier.padding(top = 8.dp),
                        color = Color.LightGray,
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
fun ProductListItem(
    title: String,
    placeName: String,
    rating: Double,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlaceLogo(
                title = title,
                rate = rating.toString(),
                size = 50,
                showRate = false
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = placeName,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD02B),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = rating.toString(),
                        fontSize = 14.sp,
                        color = Color(0xFFFFD02B)
                    )
                }
            }
        }
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (isFavorite) Color.Red else Color.Gray,
            modifier = Modifier.clickable(onClick = onFavoriteClick)
        )
    }
}

@Composable
@Preview
fun PlaceDetailScreenPreview() {
}
