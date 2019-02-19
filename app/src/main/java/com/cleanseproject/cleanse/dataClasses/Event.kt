package com.cleanseproject.cleanse.dataClasses

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Event(
        var id: String? = "",
        var name: String? = "",
        var description: String? = "",
        var photo: String? = "",
        @Exclude
        var latitude: String? = "",
        @Exclude
        var longitude: String? = ""
)
