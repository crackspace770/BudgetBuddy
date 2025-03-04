package com.android.budgetbuddy.data.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.android.budgetbuddy.data.model.UserModel
import com.android.budgetbuddy.data.model.UserToken

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class UserPreference private constructor(context: Context,private val dataStore: DataStore<Preferences>) {

    private val preferenceName = "UserPresence"
    private val sharedPref: SharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)

    fun getPreferenceBoolean(key_name: String): Boolean {
        return sharedPref.getBoolean(key_name, false)
    }

    fun saveBoolean(key_name: String, value: Boolean) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(key_name, value)
        editor.apply()
    }

    fun getUser(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[NAME_KEY] ?: "",
                preferences[EMAIL_KEY] ?: "",
                preferences[PASSWORD_KEY] ?: "",
                preferences[STATE_KEY] ?: false
            )
        }
    }

    suspend fun saveUserData(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.username
            preferences[EMAIL_KEY] = user.email
            preferences[PASSWORD_KEY] = user.password
            preferences[STATE_KEY] = user.isLogin
        }
    }

    suspend fun login() {
        dataStore.edit { preference ->
            preference[STATE_KEY] = true
        }
    }

    suspend fun logout() {
        dataStore.edit { preference ->
            preference[STATE_KEY] = false
        }
    }

    fun getUserData(): Flow<UserToken> = dataStore.data.map { preference ->
        UserToken(preference[EMAIL_KEY] ?: "")
    }

    suspend fun saveUserData(UserToken: UserToken) {
        dataStore.edit { preference ->
            preference[EMAIL_KEY] = UserToken.token
        }
    }

    suspend fun deleteToken() {
        dataStore.edit { preference ->
            preference[NAME_KEY] = ""
            preference[EMAIL_KEY] = ""
            preference[PASSWORD_KEY] = ""
        }
    }


    private val NOTIFICATION_KEY = booleanPreferencesKey("notification_setting")

    fun getNotificationSetting(): Flow<Boolean> { // New method for notification
        return dataStore.data.map { preferences ->
            preferences[NOTIFICATION_KEY] ?: true // Default to true if not set
        }
    }

    suspend fun saveNotificationSetting(isNotificationActive: Boolean) { // New method for notification
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_KEY] = isNotificationActive
        }
    }



    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val PASSWORD_KEY = stringPreferencesKey("password")
        private val STATE_KEY = booleanPreferencesKey("state")

        fun getInstance(context: Context, dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(context,dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

}