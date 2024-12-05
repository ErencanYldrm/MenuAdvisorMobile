// File: app/src/main/java/com/example/menuadvisor/data/UserPreferences.kt
package com.example.menuadvisor.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.menuadvisor.model.AuthData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

class UserPreferences @Inject constructor (private val context: Context) {

    private val Context.dataStore by preferencesDataStore(name = "user_prefs")

    private val dataStore = context.dataStore

    companion object {
        val TOKEN_KEY = stringPreferencesKey("user_token")
        val USER_ID_KEY = stringPreferencesKey("user_id")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    }

    val userToken: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[TOKEN_KEY]
        }

    val userId: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[USER_ID_KEY]
        }

    val userName: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[USER_NAME_KEY]
        }

    val userEmail: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[USER_EMAIL_KEY]
        }

    suspend fun saveUserData(authData: AuthData) {
        Log.d("UserPrefsDebug", "Saving user data: $authData")
        dataStore.edit { preferences ->
            authData.jwToken?.let { preferences[TOKEN_KEY] = it }
            authData.id?.let { preferences[USER_ID_KEY] = it }
            authData.userName?.let { 
                Log.d("UserPrefsDebug", "Saving userName: $it")
                preferences[USER_NAME_KEY] = it 
            }
            authData.email?.let { preferences[USER_EMAIL_KEY] = it }
        }
    }

    suspend fun saveUserToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_NAME_KEY)
            preferences.remove(USER_EMAIL_KEY)
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideUserPreferences(context: Context): UserPreferences {
        return UserPreferences(context)
    }
}
