package com.example.menuadvisor.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SearchButton(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                navController.navigate("search")
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))

            // Placeholder text
            Text(text = "Search", modifier = Modifier.weight(1f)) // Fill remaining space()
        }
    }
}

@Composable
fun DetailAppBar(
    title: String,
    image: String,
    isFavorite: Boolean,
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF9F1))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onBackClick() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }

            Text(
                text = title,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            IconButton(
                onClick = { onFavoriteClick() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color.Black
                )
            }
        }


        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Base64Image(
                base64String = image, modifier = Modifier
                    .fillMaxWidth()
            )
            PlaceLogo(title = title, rate = "", size = 110, showRate = false) {

            }

        }


        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = Color.Black,
            indicator = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

        }

    }

}

@Composable
fun DetailAppBar2(
    title: String,
    image: String,
    isFavorite: Boolean,
    selectedTabIndex: Int,
    tabs: List<String>,
    scrollProgress: Float,
    onTabClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
) {
    val height = 190.dp * scrollProgress


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF9F1))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onBackClick() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }

            Text(
                text = title,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            IconButton(
                onClick = { onFavoriteClick() },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color.Black
                )
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if(image.isNotEmpty()) {
                Base64Image(
                    base64String = image, modifier = Modifier
                        .size(125.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                PlaceLogo(title = title, rate = "", size = 125, showRate = false) {

                }
            }
        }

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = Color.Black,
            indicator = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp * scrollProgress)
                .padding(8.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selectedContentColor = Color(0xFFFF8B00),
                    selected = selectedTabIndex == index,
                    onClick = { onTabClick(index) },
                    text = {
                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = title, style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        )
                    }
                )
            }
        }

    }

}



@Composable
fun AuthAppBar(
    title: String,
    message: String,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            IconButton(
                onClick = { onBackClick() }
            ) {
                Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Back")
            }
        }
        Text(
            text = title,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Text(
            text = message,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}