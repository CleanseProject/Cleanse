package com.cleanseproject.cleanse.dataClasses

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message(
        var message: String? = "",
        var user: FirebaseUser?,
        var createdAt: Long?
)
