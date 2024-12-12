import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.menuadvisor.R
import com.example.menuadvisor.components.Base64Image
import com.example.menuadvisor.components.CustomNavigationBar
import com.example.menuadvisor.components.DetailAppBar
import com.example.menuadvisor.components.PlaceLogo
import com.example.menuadvisor.components.ProductItem
import com.example.menuadvisor.components.RateCard
import com.example.menuadvisor.model.PlaceData
import com.example.menuadvisor.model.ProductData
import com.example.menuadvisor.presentation.detail_screens.place.PlaceDetailViewModel
import com.example.menuadvisor.presentation.favorite.FavoritesViewModel
import com.example.menuadvisor.utils.RequestLocationPermission

@Composable
fun PlaceDetailScreen(
    placeId: Int? = null,
    navController: NavController? = null,
    viewModel: PlaceDetailViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val place by viewModel.place.observeAsState()
    val productList by viewModel.productList.observeAsState(emptyList())
    val reviewCount by viewModel.reviewCount.observeAsState(0)
    val distance by viewModel.distance.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    var isFavorite by remember { mutableStateOf(false) }

    var hasLocationPermission by remember { mutableStateOf(false) }

    // Konum izni kontrolü
    RequestLocationPermission { granted ->
        hasLocationPermission = granted
        if (granted) {
            viewModel.updateUserLocation(context)
        }
    }

    LaunchedEffect(placeId) {
        viewModel.placeId.value = placeId
        viewModel.getPlace()
        viewModel.getProductsByPlaceId()
        placeId?.let { id ->
            favoritesViewModel.isFavorite(id) { isFav ->
                isFavorite = isFav
            }
        }
    }

    Scaffold(
        topBar = {
            DetailAppBar(
                title = place?.name.toString(),
                image = place?.image.toString(),
                isFavorite = isFavorite,
                selectedTabIndex = selectedTabIndex,
                onTabClick = { selectedTabIndex = it },
                onBackClick = {
                    navController?.popBackStack()
                },
                onFavoriteClick = {
                    if (isFavorite) {
                        placeId?.let { favoritesViewModel.removeFavorite(it) }
                    } else {
                        placeId?.let { favoritesViewModel.addFavorite(it) }
                    }
                }
            )
        },
        bottomBar = {
            if (navController != null) {
                CustomNavigationBar(navController = navController,5)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            PlaceInfoContent(place = place, reviewCount = reviewCount, distance = distance)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Coffees",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            if(productList.isNullOrEmpty()) {
                Text(
                    text = "No products found",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            } else {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(productList!!.size) { index ->
                        val product = productList!![index]
                        ProductItem(
                            title = product.name.toString(),
                            image = product.image ?: "",
                            placeNameOrDistance = place?.name.toString(),
                            rate = product.rate.toString(),
                            isFavorited = false,
                            onClick = {
                                navController?.navigate("productDetailScreen/${product.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceInfoContent(place: PlaceData?, reviewCount: Int = 0, distance: Float? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF9F1))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Place Logo, displays default if base64 image is empty
            if (!place?.image.isNullOrEmpty()) {
                place?.image?.let {
                    Base64Image(
                        base64String = it,
                        modifier = Modifier.size(80.dp)
                    )
                }
            } else {
                PlaceLogo(
                    title = place?.name ?: "Place Name",
                    rate = place?.rating?.toString() ?: "0.0",
                    size = 80,
                    showRate = false
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location Icon",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (distance != null) String.format("%.1f km", distance) else "Mesafe hesaplanıyor..."
                        )
                    }
                    if (place?.rating != null && place.rating != "-1") {
                        Row(
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = place.rating,
                                fontSize = 25.sp,
                                color = Color(0xFFFFD02B),
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "(+$reviewCount)",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.clock_ic),
                        contentDescription = "Clock Icon",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = place?.workingHours ?: "Working Hours")
                }
            }
        }
    }
}


