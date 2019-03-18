package com.cleanseproject.cleanse.dataClasses

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Data class for chat messages
 * @author Cleanse Project
 */
@IgnoreExtraProperties
data class Message(
        var message: String? = "",
        var user: String? = "",
        var createdAt: Long? = 0
)
