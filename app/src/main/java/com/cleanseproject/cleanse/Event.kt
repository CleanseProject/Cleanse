package com.cleanseproject.cleanse

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Event(
        var name: String? = "",
        var description: String? = "",
        var photo: String? = "",
        var latitude: String? = "",
        var longitude: String? = ""
)
