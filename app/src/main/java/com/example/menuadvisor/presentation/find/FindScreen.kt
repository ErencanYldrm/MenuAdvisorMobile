package com.example.menuadvisor.presentation.find

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.example.menuadvisor.components.CustomNavigationBar
import androidx.navigation.NavController
import com.example.menuadvisor.utils.RequestLocationPermission

@Composable
fun FindScreen(
    viewModel: FindViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val places by viewModel.places.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    // Antalya koordinatları (varsayılan konum)
    val defaultLocation = LatLng(36.8969, 30.7133)

    var hasLocationPermission by remember { mutableStateOf(false) }

    // Konum izni kontrolü
    RequestLocationPermission { granted ->
        hasLocationPermission = granted
        if (granted) {
            viewModel.updateUserLocation(context)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation ?: defaultLocation, 13f)
    }

    // Kullanıcı konumu değiştiğinde kamerayı güncelle
    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 13f)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.weight(1f),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = hasLocationPermission,
                    mapType = MapType.NORMAL
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = true,
                    mapToolbarEnabled = true
                )
            ) {
                // Mekanlar için marker'lar
                places.forEach { place ->
                    val lat = place.lat?.replace(",", ".")?.toDoubleOrNull()
                    val lon = place.lon?.replace(",", ".")?.toDoubleOrNull()

                    if (lat != null && lon != null) {
                        val rating = place.rating?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
                        val markerColor = if (rating >= 4.0) {
                            BitmapDescriptorFactory.HUE_GREEN
                        } else {
                            BitmapDescriptorFactory.HUE_RED
                        }

                        MarkerInfoWindow(
                            state = MarkerState(position = LatLng(lat, lon)),
                            icon = BitmapDescriptorFactory.defaultMarker(markerColor),
                            onClick = {
                                place.id?.let { placeId ->
                                    navController.navigate("placeDetailScreen/$placeId")
                                }
                                false // Info window'u gösterme
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color.White.copy(alpha = 0.9f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "${place.name}\n⭐ ${place.rating ?: "0.0"}",
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
            CustomNavigationBar(navController = navController, selectedTab = 1)
        }
    }

}