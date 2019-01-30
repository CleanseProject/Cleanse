package com.cleanseproject.cleanse

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
        var name: String? = "",
        var surname: String? = ""
)
