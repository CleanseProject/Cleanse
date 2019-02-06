package com.cleanseproject.cleanse.dataClasses

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Chat(
        @Exclude
        var chatUid: String? = "",
        var members: ArrayList<String>? = null,
        @Exclude
        var lastMessageSent: String? = ""
)
