package com.cleanseproject.cleanse.dataClasses

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Chat(
        @Exclude
        var chatUid: String? = "",
        var chatName: String? = "",
        var members: HashMap<String, String>? = null,
        @Exclude
        var lastMessageSent: String? = "",
        var groupChat: Boolean? = false,
        @Exclude
        var imageId: String? = "",
        var lastMessageTime: Long = 0
) : Comparable<Chat> {
    override fun compareTo(other: Chat): Int {
        return when {
            lastMessageTime > other.lastMessageTime -> -1
            lastMessageTime < other.lastMessageTime -> 1
            else -> 0
        }
    }
}
