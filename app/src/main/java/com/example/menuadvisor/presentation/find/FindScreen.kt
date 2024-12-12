package com.example.menuadvisor.presentation.find

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.google.android.gms.maps.model.MapStyleOptions

@Composable
fun FindScreen(
    viewModel: FindViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val places by viewModel.places.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val favoritePlaceIds by viewModel.favoritePlaceIds.collectAsState()
    var selectedPlace by remember { mutableStateOf<Int?>(null) }

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
                    mapType = MapType.NORMAL,
                    mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                        context,
                        com.example.menuadvisor.R.raw.map_style
                    )
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
                        val markerColor = if (place.id in favoritePlaceIds) {
                            BitmapDescriptorFactory.HUE_YELLOW
                        } else {
                            BitmapDescriptorFactory.HUE_RED
                        }

                        Marker(
                            state = MarkerState(position = LatLng(lat, lon)),
                            title = place.name,
                            snippet = "⭐ ${place.rating}",
                            icon = BitmapDescriptorFactory.defaultMarker(markerColor),
                            onClick = { marker ->
                                selectedPlace = place.id
                                marker.showInfoWindow()
                                true
                            },
                            onInfoWindowClick = {
                                selectedPlace?.let { placeId ->
                                    navController.navigate("placeDetailScreen/$placeId") {
                                        popUpTo("find") {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
            CustomNavigationBar(navController = navController, selectedTab = 1)
        }
    }
}