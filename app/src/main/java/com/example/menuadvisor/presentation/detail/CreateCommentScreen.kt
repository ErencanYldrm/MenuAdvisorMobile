package com.example.menuadvisor.presentation.detail

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CreateCommentScreen(
    navController: NavController? = null,
    viewModel: CreateCommentViewModel = hiltViewModel(),
    initialRating: Int = 0,
    productId: Int = 0,
    reviewId: Int? = null,
    comment: String? = null,
    image: String? = null,
    isEdit: Boolean = false
) {
    var rating by remember { mutableStateOf(initialRating) }
    var commentText by remember { mutableStateOf(comment ?: "") }
    val isLoading by viewModel.isLoading.collectAsState()
    val reviewResponse by viewModel.reviewResponse.collectAsState()
    val selectedImage by viewModel.selectedImage.collectAsState()
    val initialImage by viewModel.initialImage.collectAsState()
    val context = LocalContext.current
    
    val permissionState = rememberPermissionState(
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setSelectedImage(it) }
    }

    LaunchedEffect(Unit) {
        viewModel.productId.value = productId
        // Eğer image varsa, ViewModel'e set et
        image?.let { viewModel.setInitialImage(it) }
    }

    LaunchedEffect(reviewResponse) {
        reviewResponse?.let { response ->
            if (response.succeeded == true) {
                Toast.makeText(
                    context,
                    if (isEdit) "Yorum başarıyla güncellendi" else "Yorum başarıyla gönderildi",
                    Toast.LENGTH_SHORT
                ).show()
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
                text = if (isEdit) "Yorumu Düzenle" else "Yorum Ekle",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Box(modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

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
            value = commentText,
            onValueChange = { commentText = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text("Deneyiminizi buraya yazın") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color(0xFFFFD02B)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Add Photo Button or Selected Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFF3E0))
                .clickable {
                    // Eğer seçili fotoğraf yoksa ve (yeni yorum oluşturuyorsak veya düzenleme modunda başlangıç fotoğrafı yoksa)
                    if (selectedImage == null && (!isEdit || (isEdit && (initialImage == null || initialImage!!.isEmpty())))) {
                        if (permissionState.status.isGranted) {
                            launcher.launch("image/*")
                        } else {
                            permissionState.launchPermissionRequest()
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            when {
                selectedImage != null -> {
                    // Yeni seçilen fotoğraf varsa onu göster
                    Box(contentAlignment = Alignment.TopEnd) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImage),
                            contentDescription = "Selected image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { viewModel.setSelectedImage(null) },
                            modifier = Modifier
                                .padding(4.dp)
                                .size(24.dp)
                                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove image",
                                tint = Color.Black
                            )
                        }
                    }
                }
                isEdit && initialImage != null && initialImage!!.isNotEmpty() -> {
                    // Düzenleme modunda ve başlangıç fotoğrafı varsa onu göster
                    Box(contentAlignment = Alignment.TopEnd) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = if (!initialImage!!.startsWith("data:image")) {
                                    "data:image/jpeg;base64,$initialImage"
                                } else {
                                    initialImage
                                }
                            ),
                            contentDescription = "Initial image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { viewModel.setInitialImage(null) },
                            modifier = Modifier
                                .padding(4.dp)
                                .size(24.dp)
                                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove image",
                                tint = Color.Black
                            )
                        }
                    }
                }
                else -> {
                    // Fotoğraf yoksa "Fotoğraf ekle" göster
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Photo",
                            tint = Color(0xFFFFD02B),
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Fotoğraf ekle",
                            color = Color(0xFFFFD02B),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Submit Button
        Button(
            onClick = {
                viewModel.submitReview(
                    rating = rating,
                    comment = commentText,
                    imageUri = selectedImage,
                    contentResolver = context.contentResolver,
                    reviewId = reviewId,
                    isEdit = isEdit
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFD02B)
            ),
            enabled = !isLoading && rating > 0 && commentText.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = if (isEdit) "Güncelle" else "Gönder",
                    color = Color.White
                )
            }
        }
    }
}
