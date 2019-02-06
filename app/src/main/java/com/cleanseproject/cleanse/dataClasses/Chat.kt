package com.cleanseproject.cleanse.dataClasses

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Chat(
        var chatUid: String?,
        var members: Map<String, String>?,
        var lastMessageSent: String?
)