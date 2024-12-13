package com.example.menuadvisor.presentation.auth_screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.menuadvisor.R
import com.example.menuadvisor.components.AuthAppBar
import com.example.menuadvisor.presentation.auth.RegisterViewModel
import com.example.menuadvisor.presentation.auth.RegistrationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    
    val registrationState by viewModel.registrationState.collectAsState()
    val context = LocalContext.current
    
    LaunchedEffect(registrationState) {
        when (registrationState) {
            is RegistrationState.Success -> {
                Toast.makeText(context, "Kayıt başarılı! Ana sayfaya yönlendiriliyorsunuz.", Toast.LENGTH_SHORT).show()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
            is RegistrationState.Error -> {
                Toast.makeText(
                    context,
                    (registrationState as RegistrationState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthAppBar(
            title = "Hesap Oluştur",
            message = "Hoş geldiniz! Yeni bir hesap oluşturmak için aşağıdaki bilgileri doldurun.",
            onBackClick = { navController.navigateUp() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Ad") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFFFF9F1)
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Soyad") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFFFF9F1)
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFFFF9F1)
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Kullanıcı Adı") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFFFF9F1)
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        painter = painterResource(
                            if (showPassword) R.drawable.hide_ic else R.drawable.show_ic
                        ),
                        contentDescription = if (showPassword) "Şifreyi Gizle" else "Şifreyi Göster",
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFFFF9F1)
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Şifre Tekrar") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        painter = painterResource(
                            if (showPassword) R.drawable.hide_ic else R.drawable.show_ic
                        ),
                        contentDescription = if (showPassword) "Şifreyi Gizle" else "Şifreyi Göster",
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFFFF9F1)
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                viewModel.register(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    userName = userName,
                    password = password,
                    confirmPassword = confirmPassword
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            enabled = registrationState !is RegistrationState.Loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFD02B),
                contentColor = Color.Black
            )
        ) {
            if (registrationState is RegistrationState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text("Kayıt Ol")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
