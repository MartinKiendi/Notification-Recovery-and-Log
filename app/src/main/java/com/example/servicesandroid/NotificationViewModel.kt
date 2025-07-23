package com.example.servicesandroid

import androidx.lifecycle.ViewModel
import com.example.servicesandroid.room.NotificationRepository
import androidx.lifecycle.viewModelScope
import com.example.servicesandroid.datastore.PreferencesRepository
import com.example.servicesandroid.datastore.UserPreferences
import com.example.servicesandroid.room.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val userPreferencesRepository: PreferencesRepository
) : ViewModel()
{

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = UiState()
    )
    private val _allNotifications = notificationRepository.getAllNotifications()
    val appNotifications: StateFlow<AppNotifications> =
        _allNotifications.map { notificationList ->
            AppNotifications(
                notifications = notificationList
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = AppNotifications()
        )

    fun deleteNotification(notification: Notification) = viewModelScope.launch(Dispatchers.IO) {
        notificationRepository.delete(notification)
    }

    fun clearAllNotifications() = viewModelScope.launch(Dispatchers.IO) {
        notificationRepository.clearAllNotification()
    }

    fun updateSearchTerm(searchTerm: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchTerm = searchTerm
            )
        }
    }

    fun updateShouldShowNotification(shouldShowNotification: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                shouldShowNotification = shouldShowNotification
            )
        }
    }

    fun updateCurrentPage(route: Route) {
        _uiState.update { currentState ->
            currentState.copy(
                currentRoute = route.title
            )
        }
    }
    fun updateCurrentNotification(notification: Notification?) {
        _uiState.update { currentState ->
            currentState.copy(
                currentNotification = notification
            )
        }
    }
    fun updateShowDialog(showChangeThemeDialog: Pair<Boolean, String>) {
        _uiState.update { currentState ->
            currentState.copy(
                showDialog = showChangeThemeDialog
            )
        }
    }

    fun updateNotificationsByPackageName(pair: Pair<String, List<Notification>>) {
        _uiState.update { currentState ->
            currentState.copy(
                notificationsByPackageName = pair
            )
        }
    }


    val userPreferencesFlow: StateFlow<UserPreferences> =
        userPreferencesRepository.userPreferencesFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = UserPreferences(
                    appLanguage = "English",
                    toGroupNotifications = false,
                    appTheme = "System",
                    isNotificationListenerEnabled = false
                )
            )

    fun setAppLanguage(lang: String) = viewModelScope.launch(Dispatchers.IO) {
        userPreferencesRepository.setAppLanguage(lang)
    }

    fun setAppTheme(theme: String) = viewModelScope.launch(Dispatchers.IO) {
        userPreferencesRepository.setAppTheme(theme)
    }

    fun setToGroupNotifications(toGroup: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        userPreferencesRepository.setToGroupNotifications(toGroup)
    }

    fun setIfNotificationListenerEnabled(enabled: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        userPreferencesRepository.setIfNotificationListenerEnabled(enabled)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

enum class Route(val title: Int) {
    AllNotifications(R.string.all_notifications),
    DeletedNotifications(R.string.deleted_notifications),
    Home(R.string.home),
    Settings(R.string.settings),
    GroupAllNotifications(R.string.grouped_all_notifications),
    GroupDeletedNotifications(R.string.grouped_deleted_notifications),
    NotificationByPackageName(R.string.app_name)
}

data class AppNotifications(
    val notifications: List<Notification> = listOf()
)

data class UiState(
    val shouldShowNotification: Boolean = false,
    val currentNotification: Notification? = null,
    val showDialog: Pair<Boolean, String> = Pair(false, ""),
    val searchTerm: String = "",
    val currentRoute: Int = Route.Home.title,
    val notificationsByPackageName: Pair<String,List<Notification>> = Pair("", listOf())
)
