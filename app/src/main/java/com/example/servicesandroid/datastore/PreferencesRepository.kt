package com.example.servicesandroid.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.servicesandroid.datastore.PreferencesRepositoryImpl.PreferencesKeys.APP_TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

data class UserPreferences(
    val appLanguage: String,
    val appTheme: String,
    val toGroupNotifications: Boolean,
    val isNotificationListenerEnabled: Boolean
)
interface PreferencesRepository {
    suspend fun setAppLanguage(lang: String)
    suspend fun setAppTheme(theme: String)
    suspend fun setToGroupNotifications(toGroup: Boolean)
    suspend fun setIfNotificationListenerEnabled(enabled: Boolean)
    val userPreferencesFlow : Flow<UserPreferences>
}

class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): PreferencesRepository{
    override suspend fun setAppLanguage(lang: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_LANGUAGE] = lang
        }
    }

    override suspend fun setAppTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_THEME] = theme
        }
    }

    override suspend fun setToGroupNotifications(toGroup: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GROUP_NOTIFICATIONS] = toGroup
        }
    }

    override suspend fun setIfNotificationListenerEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_LISTENER] = enabled
        }
    }

    override val userPreferencesFlow: Flow<UserPreferences> = dataStore.data.catch { exception ->
        // dataStore.data throws an IOException when an error is encountered when reading data
        if (exception is IOException) {
            Log.e(APP_TAG, "Error reading preferences.", exception)
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        val appLanguage = preferences[PreferencesKeys.APP_LANGUAGE] ?: "English"
        val groupNotifications = preferences[PreferencesKeys.GROUP_NOTIFICATIONS] ?: true
        val appTheme = preferences[PreferencesKeys.APP_THEME] ?: "System"
        val isNotificationListenerEnabled = preferences[PreferencesKeys.NOTIFICATION_LISTENER] ?: false

        UserPreferences(
            appLanguage = appLanguage,
            toGroupNotifications = groupNotifications,
            appTheme = appTheme,
            isNotificationListenerEnabled = isNotificationListenerEnabled
        )
    }
    private object PreferencesKeys {
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val GROUP_NOTIFICATIONS = booleanPreferencesKey("group_notifications")
        val APP_THEME = stringPreferencesKey("app_theme")
        val NOTIFICATION_LISTENER = booleanPreferencesKey("notification_listener")
        const val APP_TAG = "UserPreferencesRepo"
    }

}