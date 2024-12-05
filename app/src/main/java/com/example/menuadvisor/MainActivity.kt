package com.example.menuadvisor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.navigation.compose.rememberNavController
import com.example.menuadvisor.data.UserPreferences
import com.example.menuadvisor.navigation.NavGraph
import com.example.menuadvisor.ui.theme.MenuAdvisorTheme
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MenuAdvisorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    var startDestination by remember {
                        mutableStateOf("onboarding")
                    }

                    LaunchedEffect(Unit) {
                        userPreferences.userToken.collect { token ->
                            startDestination = if (token.isNullOrEmpty()) {
                                "onboarding"
                            } else {
                                "home"
                            }
                        }
                    }
                    NavGraph(startDestination = startDestination)
                }
            }
        }
    }
}