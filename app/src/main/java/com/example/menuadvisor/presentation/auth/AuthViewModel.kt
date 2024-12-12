package com.example.menuadvisor.presentation.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuadvisor.data.UserPreferences
import com.example.menuadvisor.model.ApiResponse
import com.example.menuadvisor.model.AuthData
import com.example.menuadvisor.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val token = MutableLiveData<String?>()
    val userId = MutableLiveData<String?>()
    val userName = MutableLiveData<String?>()

    val loginResponse = MutableLiveData<ApiResponse<AuthData>?>()
    val registerResponse = MutableLiveData<ApiResponse<String>?>()
    val confirmResponse = MutableLiveData<ApiResponse<String>?>()
    val resetResponse = MutableLiveData<ApiResponse<String>?>()
    var isLoading = MutableLiveData<Boolean>()

    init {
        viewModelScope.launch {
            userPreferences.userToken.collect { savedToken ->
                Log.d("AuthDebug", "Saved token: $savedToken")
                token.postValue(savedToken)
            }
        }
        viewModelScope.launch {
            userPreferences.userId.collect { savedUserId ->
                Log.d("AuthDebug", "Saved userId: $savedUserId")
                userId.postValue(savedUserId)
            }
        }
        viewModelScope.launch {
            userPreferences.userName.collect { savedUserName ->
                Log.d("AuthDebug", "Saved userName: $savedUserName")
                userName.postValue(savedUserName)
            }
        }
    }

    suspend fun login(email: String, password: String) {
        isLoading.postValue(true)
        authRepository.authenticate(email, password).let {
            if (it.isSuccessful) {
                Log.d("AuthDebug", "Login successful: ${it.body()}")
                loginResponse.postValue(it.body())
                it.body()?.data?.let { authData ->
                    viewModelScope.launch {
                        userPreferences.saveUserData(authData as AuthData)
                    }
                }
            } else {
                Log.d("AuthDebug", "Login failed: ${it.errorBody()?.string()}")
                loginResponse.postValue(
                    ApiResponse(
                        data = null,
                        errors = null,
                        message = it.errorBody()?.string(),
                        succeeded = false
                    )
                )
            }
        }
        isLoading.postValue(false)
    }

    suspend fun clearUserData() {
        viewModelScope.launch {
            userPreferences.clearUserData()
        }
    }

    suspend fun signUp(
        firstName: String,
        lastName: String,
        email: String,
        userName: String,
        password: String,
        confirmPassword: String
    ) {
        isLoading.postValue(true)
        authRepository.register(firstName, lastName, email, userName, password, confirmPassword)
            .let {
                if (it.isSuccessful) {
                    registerResponse.postValue(it.body())
                } else {
                    registerResponse.postValue(
                        ApiResponse(
                            data = null,
                            errors = null,
                            message = it.errorBody()?.string(),
                            succeeded = false
                        )
                    )
                }
            }
        isLoading.postValue(false)
    }

//    suspend fun confirmEmail(userId: String, code: String) {
//        isLoading.postValue(true)
//        authRepository.confirmEmail(userId, code).let {
//            Log.d("yuci", "viewModel  $it")
//            if (it.isSuccessful) {
//                confirmResponse.postValue(it.body())
//            } else {
//                Log.d("yuci", "viewModel1  $it")
//                confirmResponse.postValue(
//                    ApiResponse(
//                        data = null,
//                        errors = null,
//                        message = it.errorBody()?.string(),
//                        succeeded = false
//                    )
//                )
//            }
//        }
//        isLoading.postValue(false)
//    }

//    suspend fun forgotPassword(email: String) {
//        isLoading.postValue(true)
//        authRepository.forgotPassword(email)
//        isLoading.postValue(false)
//    }
//
//    suspend fun resetPassword(email: String, token: String, password: String, confirmPassword: String) {
//        isLoading.postValue(true)
//        authRepository.resetPassword(email, token, password, confirmPassword).let {
//            if (it.isSuccessful) {
//                resetResponse.postValue(it.body())
//            } else {
//                resetResponse.postValue(
//                    ApiResponse(
//                        data = null,
//                        errors = null,
//                        message = it.errorBody()?.string(),
//                        succeeded = false
//                    )
//                )
//            }
//        }
//        isLoading.postValue(false)
//    }

    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}\$")
        return email.matches(emailRegex)
    }

    fun isValidPassword(password: String): Boolean {
        val passwordRegex =
            Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/~\\\\]).{6,}\$")
        return password.matches(passwordRegex)
    }

    suspend fun getUserName(): Flow<String?> {
        return userPreferences.userName
    }
}