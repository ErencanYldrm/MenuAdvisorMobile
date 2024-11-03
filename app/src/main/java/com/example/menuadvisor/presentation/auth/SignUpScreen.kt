package com.example.menuadvisor.presentation.auth_screens

import FilledButton
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.menuadvisor.R
import com.example.menuadvisor.components.AuthAppBar
import com.example.menuadvisor.model.ApiResponse
import com.example.menuadvisor.presentation.auth.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {

    var name by rememberSaveable { mutableStateOf("") }
    var surname by rememberSaveable { mutableStateOf("") }
    var userName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var registerResponse by remember { mutableStateOf<ApiResponse<String>?>(null) }
    val scope = rememberCoroutineScope()

    if (viewModel.email.value?.isNotEmpty() == true) {
        email = viewModel.email.value!!
    }
    if (viewModel.password.value?.isNotEmpty() == true) {
        password = viewModel.password.value!!
    }

    LaunchedEffect(viewModel.registerResponse) {
        viewModel.registerResponse.observeForever {
            if (it != null) {
                registerResponse = it
                if (it.succeeded == true) {
                    Log.d("yuci", " register data ${it.data.toString()}")
                    viewModel.id.value = it.data.toString()
                    navController.navigate("login")
                } else {
                    Log.d("yuci", "message: ${it.toString()}")
                }
            }
        }
    }

    LaunchedEffect(viewModel.isLoading) {
        viewModel.isLoading.observeForever {
            isLoading = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(enabled = true, state = rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AuthAppBar(
            title = "Create an Account",
            message = "Welcome! Fill out the information below to create a new account and get started right away.",
            onBackClick = { navController.popBackStack() }
        )

        if (isLoading) {
            CircularProgressIndicator()

        } else {
            TextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                label = { Text(text = "Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFFFF9F1)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = surname,
                onValueChange = { surname = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                label = { Text(text = "Surname") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFFFF9F1)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = userName,
                onValueChange = { userName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                label = { Text(text = "Username") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFFFF9F1)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.email.value = it
                    emailError =
                        if (viewModel.isValidEmail(it)) null else "Invalid email format"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                label = { Text(text = "Email") },
                singleLine = true,
                isError = emailError != null,
                supportingText = {
                    if (emailError != null) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = emailError.toString(),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFFFF9F1)
                )
            )
            TextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.password.value = it
                    passwordError =
                        if (viewModel.isValidPassword(it)) null else "at least 6 characters, 1 uppercase, 1 number, 1 special character"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                label = { Text(text = "Password") },
                singleLine = true,
                isError = passwordError != null,
                supportingText = {
                    if (passwordError != null) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = passwordError.toString(),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
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
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFFFF9F1)
                )
            )
            TextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    passwordError =
                        if (viewModel.isValidPassword(it)) null else "at least 6 characters, 1 uppercase, 1 number, 1 special character"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                label = { Text(text = "Confirm Password") },
                singleLine = true,
                isError = confirmPassword != password,
                supportingText = {
                    if (confirmPassword != password) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Password and Confirm Password are not equals",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
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
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFFFF9F1)
                )
            )
            Spacer(modifier = Modifier.height(32.dp))
            FilledButton(
                text = "Sign Up",
                onClick = {
                    if (emailError == null && passwordError == null && confirmPassword == password) {
                        scope.launch {
                            viewModel.signUp(
                                name,
                                surname,
                                email,
                                userName,
                                password,
                                confirmPassword
                            )
                        }
                    }
                },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }


    }
}

