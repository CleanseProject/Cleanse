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
        var lastMessageTime: Long = 0
) : Comparable<Chat> {
    override fun compareTo(other: Chat): Int {
        if (lastMessageTime > other.lastMessageTime)
            return -1
        else if (lastMessageTime < other.lastMessageTime)
            return 1
        else
            return 0
    }
}
