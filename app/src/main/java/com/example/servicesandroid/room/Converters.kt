package com.example.servicesandroid.room

import android.app.Notification
import android.app.Person
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import android.service.notification.StatusBarNotification
import android.util.Base64
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



fun bitmapToString(bitmap: Bitmap?): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun stringToBitmap(base64String: String): Bitmap? {
    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}

fun convertNotificationToEntity(context: Context, sbn: StatusBarNotification, deleted: Boolean): com.example.servicesandroid.room.Notification {
    val notificationExtras = sbn.notification.extras

    val notificationTitle = if (
        sbn.notification.category == Notification.CATEGORY_CALL
        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val person = notificationExtras.getParcelable<Person>(Notification.EXTRA_CALL_PERSON)
        person?.name.toString()
    }else {
        notificationExtras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
    }
    val packageName = sbn.packageName
    val notificationText = notificationExtras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
    val bigText =
        notificationExtras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()
    val infoText =
        notificationExtras.getCharSequence(Notification.EXTRA_INFO_TEXT)?.toString().orEmpty()
    val titleBig =
        notificationExtras.getCharSequence(Notification.EXTRA_TITLE_BIG)?.toString().orEmpty()
    val conversationTitle =
        notificationExtras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE)?.toString()
            .orEmpty()

    val postTime = convertTimeStampToReadableDate(sbn.postTime)
    val smallIcon = sbn.notification?.smallIcon

    val notificationEntity = Notification(
        title = notificationTitle,
        text = notificationText,
        packageName = packageName ?: "Not Available",
        postTime = postTime,
        smallIcon = bitmapToString(smallIcon?.let { iconToBitmap(it, context) }) ,
        bigText = bigText,
        conversationTitle = conversationTitle,
        infoText = infoText,
        titleBig = titleBig,
        isDeleted = deleted
    )
    return notificationEntity
}

private fun convertTimeStampToReadableDate(timestamp: Long): String {
    val date = Date(timestamp)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(date)
}

fun iconToBitmap(icon: Icon, context: Context): Bitmap? {
    val drawable = icon.loadDrawable(context)
    return drawable?.toBitmap()
}
