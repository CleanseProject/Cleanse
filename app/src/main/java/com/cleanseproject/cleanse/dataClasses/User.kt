package com.cleanseproject.cleanse.dataClasses

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
        var userId: String?="",
        var name: String? = "",
        var surname: String? = "",
        var photo: String? = ""
)
