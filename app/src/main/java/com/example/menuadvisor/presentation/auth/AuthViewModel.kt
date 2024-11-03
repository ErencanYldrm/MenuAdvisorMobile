package com.example.menuadvisor.presentation.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.menuadvisor.model.ApiResponse
import com.example.menuadvisor.model.AuthData
import com.example.menuadvisor.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val email =MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val token = MutableLiveData<String>()
    val id = MutableLiveData<String>()


    val loginResponse = MutableLiveData<ApiResponse<AuthData>?>()
    val registerResponse = MutableLiveData<ApiResponse<String>?>()
    val confirmResponse = MutableLiveData<ApiResponse<String>?>()
    val resetResponse = MutableLiveData<ApiResponse<String>?>()
    var isLoading = MutableLiveData<Boolean>()

    suspend fun info() {
        isLoading.postValue(true)
        authRepository.info().let {
            if (it.isSuccessful) {
                loginResponse.postValue(it.body())
            } else {
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

    suspend fun login(
        email: String,
        password: String
    ) {
        isLoading.postValue(true)
        authRepository.authenticate(email, password).let {
            if (it.isSuccessful) {
                loginResponse.postValue(it.body())
            } else {
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
}