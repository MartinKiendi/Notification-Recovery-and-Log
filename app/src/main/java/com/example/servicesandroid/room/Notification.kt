package com.example.servicesandroid.room

import androidx.room.Entity
import kotlinx.serialization.Serializable


@Entity(tableName = "notification_table", primaryKeys = ["title", "text"])
@Serializable
data class Notification(
    val postTime: String,
    val id: Long = 0,
    val title: String,
    val text: String,
    val smallIcon: String,
    val packageName: String,
    val bigText: String? = null,
    val conversationTitle: String? = null,
    val infoText: String? = null,
    val titleBig: String? = null,
    val isDeleted: Boolean = false
) {
    override fun toString(): String =
        "\ntitle: $title\n" +
                "time: $postTime\n" +
                "text='$text'\n" +
                "bigText=$bigText\n" +
                "conversationTitle=$conversationTitle\n" +
                "infoText=$infoText\n" +
                "titleBig=$titleBig\n" +
                "isDeleted=$isDeleted. \n"
}




