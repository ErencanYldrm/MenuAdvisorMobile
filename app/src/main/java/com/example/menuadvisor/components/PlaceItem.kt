package com.example.menuadvisor.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random


@Composable
fun ProductItem(
    title: String,
    image: String,
    placeNameOrDistance: String,
    rate: String,
    isFavorited: Boolean,
    onClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {}
) {
    Box(
        Modifier.clickable { onClick() }
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .height(136.dp)
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF1F1F1)
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                if (image.isNotEmpty()) {
                    Base64Image(
                        base64String = image,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    PlaceLogo(title = title, rate = rate, showRate = false)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Column for title, placeNameOrDistance, and rate
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Row for title and placeNameOrDistance
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            text = title,
                            color = Color.Black,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(top = 15.dp),
                            textAlign = TextAlign.Start,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = placeNameOrDistance,
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.padding(top = 4.dp),
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 2
                        )
                    }

                    // Row for RateCard
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RateCard(rate = rate.toDouble())
                    }
                }
            }
        }

        // Heart icon to indicate favorite status
        Icon(
            imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = if (isFavorited) "Unfavorite" else "Favorite",
            tint = if (isFavorited) Color.Black else Color.Gray,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(18.dp)
                .clickable { onFavoriteClick() }
        )
    }
}


@Composable
fun PlaceLogo(
    title: String,
    rate: String,
    size: Int = 120,
    showRate: Boolean = false,
    onClick: () -> Unit = {}
) {
    val randomColor = getRandomColor()
    val displayText = if (title.isNotEmpty()) title.first().toString() else "?"

    Column(
        modifier = Modifier
            .clickable { onClick() },
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .background(color = Color(randomColor), shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.End
            ) {
                if (showRate) {
                    RateCard(rate = rate.toDouble())
                }
            }
            Text(
                text = displayText,
                color = Color.White,
                fontSize = 40.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun getRandomColor(): Int {
    val random = Random.Default
    val red = random.nextInt(50, 125)
    val green = random.nextInt(50, 125)
    val blue = random.nextInt(50, 125)
    return Color(red, green, blue).toArgb()
}

@Composable
fun RateCard(rate: Double) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .height(28.dp)
            .width(274.dp),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F1F1)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(2.dp)
                .height(28.dp)
                .width(274.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End

        ) {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = Color(0xFFFFD02B),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "4.0",
                fontSize = 24.sp,
                color = Color(0xFFFFD02B)
            )
            Text(
                text = "(+100)",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Bottom)
            )
        }

    }
}

@Composable
fun Base64Image(base64String: String, modifier: Modifier = Modifier) {
    if (base64String.isBlank()) return

    val cleanBase64 = if (base64String.contains(",")) {
        base64String.split(",")[1]
    } else {
        base64String
    }
    
    val imageBitmap = remember(cleanBase64) {
        try {
            val imageBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }
}

@Preview
@Composable
fun ProductItemPreview() {
    ProductItem(
        title = "Cappuccino",
        image = "",
        placeNameOrDistance = "Starbucks - Kültür",
        rate = "4.2",
        isFavorited = false
    )
}