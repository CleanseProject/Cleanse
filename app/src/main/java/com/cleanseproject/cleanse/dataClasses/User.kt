package com.cleanseproject.cleanse.dataClasses

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Data class for Firebase users
 * @author Cleanse Project
 */
@IgnoreExtraProperties
data class User(
        var userId: String?="",
        var name: String? = "",
        var surname: String? = "",
        var photo: String? = ""
)
