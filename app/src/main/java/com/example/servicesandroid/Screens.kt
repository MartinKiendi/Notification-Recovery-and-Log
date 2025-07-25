package com.example.servicesandroid

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.annotation.IntegerRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.servicesandroid.datastore.AppLanguage
import com.example.servicesandroid.datastore.SortOrder
import com.example.servicesandroid.datastore.Theme
import com.example.servicesandroid.datastore.UserPreferences
import com.example.servicesandroid.room.Notification
import com.example.servicesandroid.room.stringToBitmap
import com.example.servicesandroid.ui.theme.NotificationLogAndroidTheme
import com.google.android.gms.ads.nativead.NativeAd
import java.io.File
import java.time.LocalDate


@Composable
fun TextRow(title: String, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.wrapContentHeight(
            align = Alignment.Top
        )
    ) {
        Text(
            text = title,
            modifier = Modifier.width(48.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 10.sp,
            lineHeight = 8.sp,
            letterSpacing = 2.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun NotificationCard(
    onClick: (Notification) -> Unit,
    notification: Notification,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = {
            onClick(notification)
        },
        modifier = modifier.fillMaxWidth(),
    ) {

        val appIcon = getAppIcon(notification)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (appIcon != null) {
                Image(
                    bitmap = appIcon.toBitmap(config = Bitmap.Config.ARGB_8888).asImageBitmap(),
                    contentDescription = notification.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                        .clip(CircleShape)
                )
            } else {
                stringToBitmap(notification.smallIcon)?.asImageBitmap()?.let {
                    Image(
                        bitmap = it, contentDescription = notification.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(8.dp)
                            .clip(CircleShape)
                    )
                }
            }


            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f)
            ) {
                Text(
                    text = notification.postTime,
                    fontSize = 8.sp,
                    modifier = Modifier
                        .align(Alignment.End)
                        .wrapContentSize()
                )
                Text(
                    text = notification.title,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(horizontal = 4.dp)
                )
                Text(
                    text = notification.text,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(horizontal = 4.dp)
                )

            }
        }
    }
}


@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Read action on swipe from start to end
        AnimatedVisibility(
            visible = dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd,
            enter = fadeIn()
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.delete_notification)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Delete action on swipe from end to start
        AnimatedVisibility(
            visible = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart,
            enter = fadeIn()
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete_notification)
            )
        }
    }
}

@Composable
fun NotificationDetailsDialog(
    onDismissRequest: (Boolean) -> Unit,
    notification: Notification,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AlertDialog(
        icon = {
            val appIcon = AppIcon.compute(notification.packageName)

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (appIcon != null) {
                    Image(
                        bitmap = appIcon.toBitmap(config = Bitmap.Config.ARGB_8888).asImageBitmap(),
                        contentDescription = notification.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(8.dp)
                            .clip(CircleShape)
                    )
                } else {
                    stringToBitmap(notification.smallIcon)?.asImageBitmap()?.let {
                        Image(
                            bitmap = it, contentDescription = notification.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(8.dp)
                                .clip(CircleShape)
                        )
                    }
                }
                Text(
                    text = notification.title,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        title = {
            Spacer(Modifier.height(2.dp))
        },
        text = {
            Column {
                TextRow(
                    title = stringResource(R.string.date),
                    text = notification.postTime
                )
                HorizontalDivider()
                TextRow(
                    title = stringResource(R.string.name),
                    text = getAppName(notification.packageName, context.packageManager)
                )
                HorizontalDivider()
                TextRow(
                    title = stringResource(R.string.text),
                    text = notification.text
                )
                HorizontalDivider()
                AnimatedVisibility(!notification.titleBig.isNullOrBlank()) {
                    TextRow(
                        title = stringResource(R.string.extended_text),
                        text = notification.titleBig.toString()
                    )
                }

            }
        },
        onDismissRequest = {
            onDismissRequest(false)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    openApp(notification.packageName, context)
                    onDismissRequest(false)
                }
            ) {
                Text(
                    text = stringResource(R.string.show_app)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest(false)
                }
            ) {
                Text(
                    text = stringResource(R.string.back)
                )
            }
        },

        modifier = modifier
    )
}

@Composable
fun NotificationItem(
    notification: Notification,
    modifier: Modifier = Modifier,
    onClick: (Notification) -> Unit,
    deleteNotification: (Notification) -> Unit
) {
    val context = LocalContext.current
    val dismissState = rememberSwipeToDismissBoxState()
    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> {
                deleteNotification(notification)
                dismissState.reset()
                Toast.makeText(
                    context,
                    context.getString(R.string.notification_deleted),
                    Toast.LENGTH_SHORT
                ).show()
            }

            SwipeToDismissBoxValue.EndToStart -> {
                deleteNotification(notification)
                dismissState.reset()
                Toast.makeText(
                    context,
                    context.getString(R.string.notification_deleted),
                    Toast.LENGTH_SHORT
                ).show()
            }

            SwipeToDismissBoxValue.Settled -> {

            }
        }
    }
    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = { DismissBackground(dismissState) },
        content = {
            NotificationCard(
                onClick = { thisNotification ->
                    onClick(thisNotification)
                },
                notification = notification
            )
        })
}

@Composable
fun NotificationList(
    notificationList: List<Notification>,
    onClick: (Notification) -> Unit,
    deleteNotification: (Notification) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(notificationList) { notification ->
            NotificationItem(
                notification = notification,
                onClick = {
                    onClick(notification)
                },
                deleteNotification = {
                    deleteNotification(notification)
                }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    notificationList: List<Notification>,
    showNotification: (Notification) -> Unit,
    onDismissRequest: (Boolean) -> Unit,
    updateSearchTerm: (String) -> Unit,
    uiState: UiState,
    goBack: () -> Unit,
    deleteNotification: (Notification) -> Unit,
    title: String,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = goBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(innerPadding)
        ) {
            OutlinedTextField(
                value = uiState.searchTerm,
                onValueChange = { newSearchTerm ->
                    updateSearchTerm(newSearchTerm)
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.search)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search)
                    )
                },
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, top = 2.dp, bottom = 8.dp)
                    .fillMaxWidth()
            )
            AdBanner()
            NotificationList(
                notificationList = notificationList,
                onClick = { notificationEntity ->
                    showNotification(notificationEntity)
                },
                deleteNotification = { thisNotification ->
                    deleteNotification(thisNotification)
                },
                modifier = Modifier.weight(1f)
            )
            AdBanner()
            if (uiState.shouldShowNotification) {
                uiState.currentNotification?.let {
                    NotificationDetailsDialog(
                        onDismissRequest = {
                            onDismissRequest(false)
                        },
                        notification = it
                    )
                }
            }
        }
        BackHandler {
            goBack()
        }
    }
}

@Composable
fun HomeItem(
    onClick: (String) -> Unit,
    title: String, iconName: String,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = {
            onClick(title)
        },
        modifier = modifier.width(168.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(16.dp)
                .wrapContentHeight()
        ) {
            Icon(
                imageVector = iconByName(iconName),
                contentDescription = title,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeApp(
    listOfAds: List<NativeAd>,
    notificationViewModel: NotificationViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val appNotificationList by notificationViewModel.appNotifications.collectAsState()
    val uiState by notificationViewModel.uiState.collectAsState()
    val userPreferences by notificationViewModel.userPreferencesFlow.collectAsState()
    val context = LocalContext.current
    val activity = LocalActivity.current


    val notifications = when(userPreferences.sortOrder){
        SortOrder.LATEST.name  -> appNotificationList.notifications.reversed()  // Latest
        SortOrder.OLDEST.name -> appNotificationList.notifications // Oldest
        SortOrder.ASCENDING.name -> appNotificationList.notifications.sortedWith(
            compareBy(String.CASE_INSENSITIVE_ORDER) { it.title }
        ) // Ascending
        SortOrder.DESCENDING.name -> appNotificationList.notifications.sortedByDescending{it.title} // Descending
        else -> appNotificationList.notifications.reversed()  // Latest
    }

    NotificationLogAndroidTheme(
        darkTheme = when (userPreferences.appTheme) {
            Theme.LIGHT.name -> false
            Theme.DARK.name -> true
            Theme.SYSTEM.name -> isSystemInDarkTheme()
            else -> isSystemInDarkTheme()
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Route.Home.name,
            modifier = Modifier
        ) {

            composable(Route.Home.name) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = stringResource(R.string.app_name)
                                )
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        navController.navigate(Route.Settings.name)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = stringResource(R.string.settings)
                                    )
                                }
                            }
                        )
                    },
                ) { innerPadding ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        HomeItem(
                            title = stringResource(R.string.all_notifications),
                            iconName = "Home",
                            onClick = {
                                if (!userPreferences.toGroupNotifications) {
                                    navController.navigate(Route.AllNotifications.name)
                                } else {
                                    navController.navigate(Route.GroupAllNotifications.name)
                                }

                            }
                        )
                        Spacer(Modifier.height(16.dp))
                        AnimatedVisibility(listOfAds.isNotEmpty()) {
                            NativeAdJetPack(
                                nativeAd = listOfAds.random(),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(
                                        horizontal = 12.dp, vertical = 8.dp
                                    )
                            )
                        }
                    }
                }
            }
            composable(Route.AllNotifications.name) {
                NotificationScreen(
                    notificationList = notifications.filter {
                        it.isTheOne(uiState.searchTerm)
                    },
                    showNotification = { notificationEntity ->
                        notificationViewModel.updateShouldShowNotification(true)
                        notificationViewModel.updateCurrentNotification(notificationEntity)
                    },
                    onDismissRequest = { boolean ->
                        notificationViewModel.updateShouldShowNotification(boolean)
                        notificationViewModel.updateCurrentNotification(null)
                    },
                    updateSearchTerm = { searchTerm ->
                        notificationViewModel.updateSearchTerm(searchTerm)
                    },
                    uiState = uiState,
                    deleteNotification = { thisNotification ->
                        notificationViewModel.deleteNotification(thisNotification)
                    },
                    goBack = {
                        showInterstitialAd(context, activity) {
                            notificationViewModel.updateSearchTerm("")
                            navController.navigateUp()
                        }
                    },
                    title = stringResource(Route.AllNotifications.title),
                )
            }
            composable(Route.GroupAllNotifications.name) {
                GroupedNotificationsPage(
                    title = stringResource(R.string.group_notifications),
                    goBack = {
                        showInterstitialAd(context, activity) {
                            notificationViewModel.updateSearchTerm("")
                            navController.navigateUp()
                        }
                    },
                    goToNextPage = { packageName, notifications ->
                        notificationViewModel.updateNotificationsByPackageName(
                            Pair(packageName, notifications)
                        )
                        navController.navigate(Route.NotificationByPackageName.name)
                    },
                    map = notifications.filter {
                        it.isTheOne(uiState.searchTerm)
                    }.groupBy {
                        it.packageName
                    }
                )
            }
            composable(Route.NotificationByPackageName.name) {
                NotificationScreen(
                    notificationList = uiState.notificationsByPackageName.second.filter {
                        it.isTheOne(uiState.searchTerm)
                    },
                    showNotification = { notificationEntity ->
                        notificationViewModel.updateShouldShowNotification(true)
                        notificationViewModel.updateCurrentNotification(notificationEntity)
                    },
                    onDismissRequest = { boolean ->
                        notificationViewModel.updateShouldShowNotification(boolean)
                        notificationViewModel.updateCurrentNotification(null)
                    },
                    updateSearchTerm = { searchTerm ->
                        notificationViewModel.updateSearchTerm(searchTerm)
                    },
                    uiState = uiState,
                    deleteNotification = { thisNotification ->
                        notificationViewModel.deleteNotification(thisNotification)
                    },
                    goBack = {
                        notificationViewModel.updateSearchTerm("")
                        navController.navigateUp()
                    },
                    title = getAppName(
                        uiState.notificationsByPackageName.first,
                        context.packageManager
                    )
                )
            }
            composable(Route.Settings.name) {
                SettingsPage(
                    goBack = {
                        showInterstitialAd(context, activity) {
                            notificationViewModel.updateSearchTerm("")
                            navController.navigateUp()
                        }
                    },
                    title = stringResource(Route.Settings.title),
                    userPreferences = userPreferences,
                    uiState = uiState,
                    clearAllData = {
                        notificationViewModel.clearAllNotifications()
                    },
                    setAppLanguage = { lang ->
                        notificationViewModel.setAppLanguage(getLanguageFromCode(lang).name)
                    },
                    setGroupNotifications = { toGroup ->
                        notificationViewModel.setToGroupNotifications(toGroup)
                    },
                    updateShowDialog = { showChangeThemeDialog ->
                        notificationViewModel.updateShowDialog(showChangeThemeDialog)
                    },
                    setIfNotificationListenerEnabled = {
                    },
                    changeAppTheme = { theme ->
                        notificationViewModel.setAppTheme(getThemeForSettings(context,theme).name)
                    },
                    notifications = notifications,
                    setSortOrder = { sortOrder ->
                        val sortOrder = getSortOrderForSettings(context,sortOrder)
                        //Toast.makeText(context, sortOrder.name, Toast.LENGTH_SHORT).show()
                        notificationViewModel.setSortOrder(sortOrder.name)
                    }
                )
            }

        }
    }
}

@Composable
fun AppCheckbox(
    title: String,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var checked by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = {
                checked = it
                onCheckedChange(it)
            }
        )
        Text(text = title)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupedNotificationsPage(
    title: String,
    goBack: () -> Unit,
    goToNextPage: (String, List<Notification>) -> Unit,
    map: Map<String,
            List<Notification>>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = goBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        val context = LocalContext.current
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(map.entries.toList()) { (packageName, notifications) ->
                val notification = notifications[0]
                val appIcon = getAppIcon(notification)
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                            .clickable {
                                goToNextPage(packageName, notifications)
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (appIcon != null) {
                            Image(
                                bitmap = appIcon.toBitmap(config = Bitmap.Config.ARGB_8888)
                                    .asImageBitmap(),
                                contentDescription = packageName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(8.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            stringToBitmap(notification.smallIcon)?.asImageBitmap()?.let {
                                Image(
                                    bitmap = it,
                                    contentDescription = packageName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .padding(8.dp)
                                        .clip(CircleShape)
                                )
                            }
                        }
                        Text(
                            text = getAppName(notification.packageName, context.packageManager),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "(${notifications.size})",
                            fontSize = 10.sp,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }

            }
        }
    }

    BackHandler {
        goBack()
    }
}

@Composable
fun TextColumn(
    title: String, text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 14.sp,

            )
        Text(
            text = text,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            modifier = Modifier
                .padding(2.dp)
                .wrapContentHeight()
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    goBack: () -> Unit,
    title: String,
    userPreferences: UserPreferences,
    uiState: UiState,
    clearAllData: () -> Unit,
    setAppLanguage: (String) -> Unit,
    setSortOrder: (String) -> Unit,
    changeAppTheme: (String) -> Unit,
    setGroupNotifications: (Boolean) -> Unit,
    updateShowDialog: (Pair<Boolean, String>) -> Unit,
    setIfNotificationListenerEnabled: (Boolean) -> Unit,
    notifications: List<Notification>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = goBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                }

            )
        },
    ) { innerPadding ->
        val context = LocalContext.current
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(innerPadding)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            SettingsSection(
                title = stringResource(R.string.notifications_recovery)
            ) {
                Column {
                    TextColumn(
                        title = stringResource(R.string.notification_recovery_service),
                        text = if (userPreferences.isNotificationListenerEnabled) stringResource(R.string.enabled) else stringResource(
                            R.string.disabled
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextColumn(
                            title = stringResource(R.string.enable_notification_recording),
                            text = stringResource(R.string.recording_warning),
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = userPreferences.isNotificationListenerEnabled,
                            onCheckedChange = {
                                setIfNotificationListenerEnabled(it)
                            },)
                    }

                }
            }
            HorizontalDivider()
            SettingsSection(title = stringResource(R.string.app_theme)) {
                TextColumn(
                    title = stringResource(R.string.change_app_theme),
                    text = getThemeText(context, userPreferences.appTheme),
                    modifier = Modifier.clickable {
                        updateShowDialog(Pair(true, "Theme"))
                    }
                )
                if (uiState.showDialog.first == true && uiState.showDialog.second == "Theme") {
                    ChangeSettingsDialog(
                        title = stringResource(R.string.change_app_theme),
                        name = R.array.theme_array,
                        selectedOption = getThemeText(context, userPreferences.appTheme),
                        changeShouldShowDialog = {
                            updateShowDialog(it)
                        },
                        changeOneSetting = {
                            changeAppTheme(it.first)
                        }
                    )
                }
            }
            HorizontalDivider()
            SettingsSection(
                title = stringResource(R.string.app_language)
            ) {
                val context = LocalContext.current
                TextColumn(
                    title = stringResource(R.string.change_app_language),
                    text = getLanguageText(context, userPreferences.appLanguage),
                    modifier = Modifier.clickable {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS)
                            val uri = Uri.fromParts("package", context.packageName, null)
                            intent.data = uri
                            context.startActivity(intent)
                        } else {
                            updateShowDialog(Pair(true, "Language"))
                        }
                    }
                )
                if (uiState.showDialog.first == true
                    && uiState.showDialog.second == "Language"
                ) {
                    val localeOptions = listOf<Pair<String, String>>(
                        "en" to "English",
                        "de" to "Deutsch",
                        "fr" to "Français",
                        "it" to "Italiano",
                        "es" to "Español"
                    )
                    ChangeSettingsDialog(
                        title = stringResource(R.string.change_app_language),
                        name = R.array.language_array,
                        selectedOption = getLanguageText(context,userPreferences.appLanguage),
                        changeShouldShowDialog = {
                            updateShowDialog(it)
                        },
                        changeOneSetting = { setting ->
                            if (setting.second == "Language") {
                                val selectedLocale = setting.first // Full name of language
                                val lang = localeOptions.find {
                                    it.second == selectedLocale
                                }?.first // Get language code for setting language
                                setAppLanguage(lang.toString()) // Save language in preferences

                                AppCompatDelegate.setApplicationLocales(
                                    LocaleListCompat.forLanguageTags(
                                        lang.toString()
                                    )
                                )
                            }
                        }
                    )
                }
            }
            HorizontalDivider()
            SettingsSection(
                title = stringResource(R.string.group_notifications)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextColumn(
                        title = stringResource(R.string.group_notification_explanation),
                        text = if (userPreferences.toGroupNotifications) stringResource(R.string.enabled) else stringResource(
                            R.string.disabled
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = userPreferences.toGroupNotifications,
                        onCheckedChange = {
                            setGroupNotifications(it)
                        })
                }

            }
            HorizontalDivider()
            SettingsSection(
                title = stringResource(R.string.sort_order)
            ) {
                TextColumn(
                    title = stringResource(R.string.set_sort_order),
                    text = getSortOrderText(context,userPreferences.sortOrder),
                    modifier = Modifier.clickable {
                        updateShowDialog(Pair(true, "Sort_Order"))
                    }
                )
                if (uiState.showDialog.first == true
                    && uiState.showDialog.second == "Sort_Order"
                ) {
                    ChangeSettingsDialog(
                        title = stringResource(R.string.set_sort_order),
                        name = R.array.sort_array,
                        selectedOption = getSortOrderText(context,userPreferences.sortOrder),
                        changeShouldShowDialog = {
                            updateShowDialog(it)
                        },
                        changeOneSetting = { setting ->
                            if (setting.second == "Sort_Order") {
                                //Toast.makeText(context, setting.first, Toast.LENGTH_SHORT).show()
                                setSortOrder(setting.first)
                            }
                        }
                    )
                }
            }
            HorizontalDivider()
            SettingsSection(
                title = stringResource(R.string.app_data)
            ) {
                var showDialog by remember { mutableStateOf(false) }
                Text(
                    text = stringResource(R.string.app_data_warning),
                    modifier = Modifier.clickable {
                        showDialog = true
                    }
                )
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                            TextButton(onClick = {
                                clearAllData
                                showDialog = false
                            }) {
                                Text(text = stringResource(R.string.delete))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text(text = stringResource(R.string.cancel))
                            }
                        },
                        title = {
                            Text(
                                text = stringResource(R.string.delete_all_notifications_warning)
                            )
                        },
                    )
                }
            }
            HorizontalDivider()
            SettingsSection(
                title = stringResource(R.string.share_notifications)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        updateShowDialog(Pair(true, "Apps"))
                    }
                ) {
                    Text(
                        text = stringResource(R.string.share_notifications_warning),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share_notifications)
                    )
                }
                if (uiState.showDialog.first == true && uiState.showDialog.second == "Apps") {
                    val packages = notifications.groupBy {
                        it.packageName
                    }
                    val listOfPackageNames = mutableListOf<String>()
                    for (packageName in packages) {
                        listOfPackageNames.add(packageName.key)
                    }
                    val packageNames = mutableListOf<String>()
                    AlertDialog(
                        onDismissRequest = {
                            updateShowDialog(Pair(false, ""))
                        },
                        title = {
                            Text(
                                text = stringResource(R.string.choose_app_to_share_noti)
                            )
                        },
                        text = {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.wrapContentHeight()
                            ) {
                                items(listOfPackageNames) { appName ->
                                    AppCheckbox(
                                        title = getAppName(appName, context.packageManager),
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                packageNames.add(appName)
                                            }
                                        }
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val notificationsJoinedToText =
                                        notifications.filter {
                                            it.packageName in packageNames
                                        }.joinToString {
                                            "Notification : ${
                                                getAppName(
                                                    it.packageName,
                                                    context.packageManager
                                                )
                                            } " +
                                                    "$it\n\n"
                                        }

                                    val fileName =
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            "notificationsat${LocalDate.now()}.txt"
                                        } else {
                                            "notifications.txt"
                                        }
                                    writeToFile(
                                        context = context,
                                        filename = fileName,
                                        fileText = notificationsJoinedToText
                                    )
                                    val file = File(context.filesDir, fileName)
                                    shareFile(context, file)
                                    listOfPackageNames.clear()
                                    packageNames.clear()
                                    updateShowDialog(Pair(false, ""))
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.ok_1)
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    updateShowDialog(Pair(false, ""))
                                    listOfPackageNames.clear()
                                    packageNames.clear()
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.cancel)
                                )
                            }
                        },
                    )
                }
            }
            HorizontalDivider()
            SettingsSection(
                title = stringResource(R.string.about_app)
            ) {
                val context = LocalContext.current
                val versionInfo = getAppVersion(context)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val context = LocalContext.current
                    val baseUrl = "https://play.google.com/store/apps/details?id="
                    Text(
                        text = stringResource(R.string.rate_app),
                        modifier = Modifier.clickable {
                            try {
                                val rateIntent = Intent(Intent.ACTION_VIEW)
                                rateIntent.data = "$baseUrl${context.packageName}".toUri()
                                rateIntent.`package` = "com.android.vending"
                                context.startActivity(rateIntent)

                            } catch (e: ActivityNotFoundException) {
                                Log.e("error", e.message.toString())
                                val rateIntent = Intent(Intent.ACTION_VIEW)
                                rateIntent.data = "$baseUrl${context.packageName}".toUri()
                                context.startActivity(rateIntent)
                            }
                        }
                    )
                    Text(
                        text = stringResource(R.string.github),
                        modifier = Modifier.clickable {
                            val url =
                                "https://github.com/MartinKiendi/Notification-Recovery-and-Log"
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = url.toUri()
                            context.startActivity(i)
                        }
                    )
                    Text(
                        text = stringResource(R.string.privacy_policy),
                        modifier = Modifier.clickable {
                            val url = "https://martinkiendi.github.io/NotificationRecoveryandLog"
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = url.toUri()
                            context.startActivity(i)
                        }
                    )
                    Text(
                        text = stringResource(R.string.version, versionInfo)
                    )
                }
            }
            HorizontalDivider()
        }
        BackHandler {
            goBack()
        }
    }
}

@Composable
fun RadioButtonSingleSelection(
    name: Int,
    onOptionSelected: (Pair<String, String>) -> Unit,
    selectedOption: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val type = when (name) {
        R.array.theme_array -> "Theme"
        R.array.language_array -> "Language"
        R.array.sort_array -> "Sort_Order"
        else -> ""
    }
    val listOfOptions = context.resources.getStringArray(name)
    // Note that Modifier.selectableGroup() is essential
    // to ensure correct accessibility behavior
    Column(modifier.selectableGroup()) {
        listOfOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = {
                            //Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                            onOptionSelected(Pair(text, type))
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null // null recommended for accessibility with screen readers
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
fun ChangeSettingsDialog(
    title: String,
    name: Int,
    selectedOption: String,
    changeShouldShowDialog: (Pair<Boolean, String>) -> Unit,
    changeOneSetting: (Pair<String, Any>) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        title = {
            Text(title)
        },
        text = {
            RadioButtonSingleSelection(
                name = name,
                onOptionSelected = { setting ->
                    changeOneSetting(setting)
                    changeShouldShowDialog(Pair(false, ""))
                },
                selectedOption = selectedOption
            )
        },
        onDismissRequest = {
            changeShouldShowDialog(Pair(false, ""))
        },
        confirmButton = {
        },
        dismissButton = {
        },
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun SettingsPagePreview() {
    NotificationLogAndroidTheme(darkTheme = false) {
        SettingsPage(
            title = "Settings",
            goBack = {},
            userPreferences = UserPreferences(
                appLanguage = AppLanguage.ENGLISH.name,
                toGroupNotifications = false,
                appTheme = Theme.SYSTEM.name,
                isNotificationListenerEnabled = false,
                sortOrder = SortOrder.ASCENDING.name
            ),
            clearAllData = {},
            setAppLanguage = {},
            setGroupNotifications = {},
            setIfNotificationListenerEnabled = {},
            uiState = UiState(),
            updateShowDialog = {},
            changeAppTheme = {},
            notifications = listOf(),
            setSortOrder = {}
        )
    }
}
