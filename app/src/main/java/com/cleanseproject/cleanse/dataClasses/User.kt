package com.cleanseproject.cleanse.dataClasses

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
        @Exclude
        var userId: String?="",
        var name: String? = "",
        var surname: String? = "",
        var photo: String? = ""
)
