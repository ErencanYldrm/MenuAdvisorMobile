package com.example.menuadvisor.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuadvisor.model.RegisterRequest
import com.example.menuadvisor.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Initial)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        userName: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            try {
                val request = RegisterRequest(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    userName = userName,
                    password = password,
                    confirmPassword = confirmPassword
                )
                val response = accountRepository.register(request)
                if (response.isSuccessful) {
                    response.body()?.let { registerResponse ->
                        if (registerResponse.succeeded) {
                            // Kayıt başarılı, userId ve code'u sakla
                            val userId = registerResponse.data
                            val code = registerResponse.message.substringAfter("code:  ").trim()
                            confirmEmail(userId, code)
                        } else {
                            _registrationState.value = RegistrationState.Error(registerResponse.message)
                        }
                    }
                } else {
                    _registrationState.value = RegistrationState.Error("Registration failed")
                }
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun confirmEmail(userId: String, code: String) {
        viewModelScope.launch {
            try {
                val response = accountRepository.confirmEmail(userId, code)
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        if (apiResponse.succeeded == true) {
                            _registrationState.value = RegistrationState.Success
                        } else {
                            _registrationState.value = apiResponse.message?.let {
                                RegistrationState.Error(
                                    it
                                )
                            }!!
                        }
                    }
                } else {
                    _registrationState.value = RegistrationState.Error("Email confirmation failed")
                }
            } catch (e: Exception) {
                _registrationState.value = RegistrationState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class RegistrationState {
    object Initial : RegistrationState()
    object Loading : RegistrationState()
    object Success : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}
