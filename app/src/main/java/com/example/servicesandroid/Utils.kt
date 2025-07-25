package com.example.servicesandroid

import android.app.LocaleManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.FileProvider
import com.example.servicesandroid.datastore.AppLanguage
import com.example.servicesandroid.datastore.SortOrder
import com.example.servicesandroid.datastore.Theme
import com.example.servicesandroid.room.Notification
import java.io.File


fun openApp(pkgName: String, context: Context) {
    val launchIntent = ServiceApplication.myPackageManager.getLaunchIntentForPackage(pkgName)
    if (launchIntent != null) {
        context.startActivity(launchIntent)
    }
}
fun iconByName(name: String): ImageVector {
    val cl = Class.forName("androidx.compose.material.icons.filled.${name}Kt")
    val method = cl.declaredMethods.first()
    return method.invoke(null, Icons.Filled) as ImageVector
}
fun isNotificationServiceEnabled(
    context: Context,
    componentName: ComponentName
): Boolean {
    val enabledListeners =
        Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
    return enabledListeners?.contains(componentName.flattenToString()) == true
}

fun getThemeText(context: Context, theme: String): String {
    return when(theme){
        Theme.LIGHT.name -> context.getString(R.string.light)
        Theme.DARK.name -> context.getString(R.string.dark)
        Theme.SYSTEM.name -> context.getString(R.string.system)
        else -> context.getString(R.string.system)
    }
}
fun getSortOrderText(context: Context, sortOrder:String): String {
    return when(sortOrder){
        SortOrder.LATEST.name -> context.getString(R.string.latest)
        SortOrder.OLDEST.name -> context.getString(R.string.oldest)
        SortOrder.ASCENDING.name -> context.getString(R.string.ascending)
        SortOrder.DESCENDING.name -> context.getString(R.string.descending)
        else -> context.getString(R.string.latest)
    }
}
fun getLanguageText(context: Context, language: String): String {
    return when(language){
        AppLanguage.ENGLISH.name -> context.getString(R.string.en)
        AppLanguage.DEUTSCH.name -> context.getString(R.string.de)
        AppLanguage.FRENCH.name -> context.getString(R.string.fr)
        AppLanguage.ITALIAN.name -> context.getString(R.string.it)
        AppLanguage.ESPANOL.name -> context.getString(R.string.es)
        else -> context.getString(R.string.en)
    }
}

fun getThemeForSettings(context: Context, string: String): Theme {
    return when (string) {
        context.getString(R.string.light) -> Theme.LIGHT
        context.getString(R.string.dark) -> Theme.DARK
        context.getString(R.string.system) -> Theme.SYSTEM
        else -> Theme.SYSTEM
    }
}
fun getSortOrderForSettings(context: Context,string: String): SortOrder {
    return when(string){
        context.getString(R.string.latest) -> SortOrder.LATEST
        context.getString(R.string.oldest) -> SortOrder.OLDEST
        context.getString(R.string.ascending) -> SortOrder.ASCENDING
        context.getString(R.string.descending) -> SortOrder.DESCENDING
        else -> SortOrder.LATEST
    }
}
fun getLanguageFromCode(code: String): AppLanguage{
    return when(code){
        "en" -> AppLanguage.ENGLISH
        "de" -> AppLanguage.DEUTSCH
        "fr" -> AppLanguage.FRENCH
        "it" -> AppLanguage.ITALIAN
        "es" -> AppLanguage.ESPANOL
        else -> AppLanguage.ENGLISH
    }
}

fun checkForInternet(context: Context): Boolean {

    // register activity with the connectivity manager service
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as
            ConnectivityManager

    // Returns a Network object corresponding to
    // the currently active default data network.
    val network = connectivityManager.activeNetwork ?: return false

    // Representation of the capabilities of an active network.
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

    return when {
        // Indicates this network uses a Wi-Fi transport,
        // or WiFi has network connectivity
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

        // Indicates this network uses a Cellular transport. or
        // Cellular has network connectivity
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

        // else return false
        else -> false
    }
}
fun Notification.isTheOne(searchTerm: String) : Boolean = (
        this.title.contains(searchTerm, ignoreCase = true) ||
        this.text.contains(searchTerm, ignoreCase = true) ||
        this.titleBig?.contains(
            searchTerm, ignoreCase = true
        ) == true) && !this.isDeleted
fun getFileUri(context: Context, file: File): Uri? {
    var uri: Uri? = null
    try {
        uri = FileProvider.getUriForFile(context, "com.mousavi.NotificationListener.fileprovider", file)
    } catch (e: Exception) {
        Toast.makeText(context, "" + e.message, Toast.LENGTH_LONG).show()
        Log.d("File share Error", "" + e.message)
    }
    return uri
}
fun shareFile(context: Context, file: File) {
    val uri: Uri? = getFileUri(context, file)
    val intent = Intent(Intent.ACTION_SEND)

    // putting uri of image to be shared
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    // setting type to image
    intent.type = getMimeType(file.name)
    // calling startactivity() to share
    context.startActivity(Intent.createChooser(intent, "Share Via"), null)
}
fun getMimeType(filename: String): String {
    // There does not seem to be a way to ask the OS or file itself for this
    // information, so unfortunately resorting to extension sniffing.
    val pos = filename.lastIndexOf('.')
    if (pos != -1) {
        val ext = filename.substring(
            filename.lastIndexOf('.') + 1,
            filename.length
        )
        if (ext.equals("mp3", ignoreCase = true)) return "audio/mpeg"
        if (ext.equals("aac", ignoreCase = true)) return "audio/aac"
        if (ext.equals("wav", ignoreCase = true)) return "audio/wav"
        if (ext.equals("ogg", ignoreCase = true)) return "audio/ogg"
        if (ext.equals("mid", ignoreCase = true)) return "audio/midi"
        if (ext.equals("midi", ignoreCase = true)) return "audio/midi"
        if (ext.equals("wma", ignoreCase = true)) return "audio/x-ms-wma"
        if (ext.equals("mp4", ignoreCase = true)) return "video/mp4"
        if (ext.equals("avi", ignoreCase = true)) return "video/x-msvideo"
        if (ext.equals("wmv", ignoreCase = true)) return "video/x-ms-wmv"
        if (ext.equals("png", ignoreCase = true)) return "image/png"
        if (ext.equals("jpg", ignoreCase = true)) return "image/jpeg"
        if (ext.equals("jpe", ignoreCase = true)) return "image/jpeg"
        if (ext.equals("jpeg", ignoreCase = true)) return "image/jpeg"
        if (ext.equals("gif", ignoreCase = true)) return "image/gif"
        if (ext.equals("xml", ignoreCase = true)) return "text/xml"
        if (ext.equals("txt", ignoreCase = true)) return "text/plain"
        if (ext.equals("cfg", ignoreCase = true)) return "text/plain"
        if (ext.equals("csv", ignoreCase = true)) return "text/plain"
        if (ext.equals("conf", ignoreCase = true)) return "text/plain"
        if (ext.equals("rc", ignoreCase = true)) return "text/plain"
        if (ext.equals("htm", ignoreCase = true)) return "text/html"
        if (ext.equals("html", ignoreCase = true)) return "text/html"
        if (ext.equals("pdf", ignoreCase = true)) return "application/pdf"
        if (ext.equals("apk", ignoreCase = true)) return "application/vnd.android.package-archive"

        // Additions and corrections are welcomed.
    }
    return "*/*"
}
fun writeToFile(context: Context,filename:String,fileText: String){
    val file = context.openFileOutput(filename, Context.MODE_PRIVATE)
    file.write(fileText.toByteArray())
    file.close()
}
fun getAppName(packageName: String, packageManager: PackageManager): String {
    return try {
        val applicationInfo: ApplicationInfo = packageManager.getApplicationInfo(packageName, 0)
        packageManager.getApplicationLabel(applicationInfo).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        Log.e("MyApp", "Error getting app name for package: $packageName", e)
        packageName
    }
}
fun isBlacklistedNotification(sbn: StatusBarNotification?): Boolean {
    if (sbn == null) {
        return true
    }

    if (sbn.packageName.startsWith("com.whatsapp") && sbn.key!!.contains("null")) {
        return true
    }

    if (sbn.packageName == "com.sec.android.app.clock.package") {
        return true
    }


    if (sbn.key == "-1|android|27|null|1000") {
        return true
    }

    if (sbn.key == "charging_state") {
        return true
    }

    return sbn.key == "com.sec.android.app.samsungapps|121314|null|10091"
}
fun getAppIcon(notification: Notification): Drawable? {
    var appIcon: Drawable? = null
    var iconDrawable : Drawable? = null

    try {
        val remotePackageContext = ServiceApplication().createPackageContext(notification.packageName, 0)
        iconDrawable = remotePackageContext.resources.getDrawable(remotePackageContext.resources.getIdentifier("ic_launcher", "drawable", notification.packageName), null)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    appIcon = iconDrawable ?: AppIcon.compute(notification.packageName)
    return appIcon
}
fun getAppVersion(context: Context): String{
    val appVersion = ServiceApplication.myPackageManager.getPackageInfo(context.packageName, 0)
    val appVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        appVersion.longVersionCode
    } else {
        appVersion.versionCode
    }
    val appVersionName = appVersion.versionName
    return "$appVersionCode.$appVersionName"
}
fun getLanguageCode(context: Context):String{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java)
            .applicationLocales[0]?.toLanguageTag()?.split("-")?.first()?: "en"
    }else{
        return AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag()?.split("-")?.first() ?: "en"
    }
}