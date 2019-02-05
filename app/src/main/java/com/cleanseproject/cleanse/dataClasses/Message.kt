package com.cleanseproject.cleanse.dataClasses

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message(
        var message: String? = "",
        var User: User?,
        var createdAt: Long?
)
