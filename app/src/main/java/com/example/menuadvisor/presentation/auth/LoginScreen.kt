package com.example.menuadvisor.presentation.auth_screens

import CustomOutlinedButton
import FilledButton
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.menuadvisor.R
import com.example.menuadvisor.presentation.auth.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel.loginResponse) {
        viewModel.loginResponse.observeForever { response ->
            if (response != null) {
                if (response.succeeded == true) {
                    // Navigate to the main screen after login success
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    Toast.makeText(
                        navController.context,
                        "Login failed: ${response.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    LaunchedEffect(viewModel.isLoading) {
        viewModel.isLoading.observeForever { loading ->
            isLoading = loading
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.menu_advisor_logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Email Input Field
        TextField(
            value = email,
            onValueChange = {
                email = it
                emailError = if (viewModel.isValidEmail(it)) null else "Invalid email format"
            },
            label = { Text(text = "Email") },
            isError = emailError != null,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFFFF9F1)
            )
        )
        if (emailError != null) {
            Text(
                text = emailError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Password Input Field
        TextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = if (viewModel.isValidPassword(it)) null
                else "At least 6 characters, 1 uppercase, 1 number, 1 special character"
            },
            label = { Text(text = "Password") },
            isError = passwordError != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        painter = if (showPassword) painterResource(id = R.drawable.hide_ic) else painterResource(
                            id = R.drawable.show_ic
                        ),
                        contentDescription = "Hide and Show",
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFFFF9F1)
            )
        )
        if (passwordError != null) {
            Text(
                text = passwordError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(32.dp))

        // Login Button
        FilledButton(
            text = "Login",
            enabled = emailError == null && passwordError == null,
            isLoading = isLoading,
            onClick = {
                scope.launch {
                    viewModel.login(email, password)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Up Link
        CustomOutlinedButton(
            text = "Sign Up",
            onClick = {
                scope.launch {
                    navController.navigate("signup")
                }
            },
        )
    }
}

