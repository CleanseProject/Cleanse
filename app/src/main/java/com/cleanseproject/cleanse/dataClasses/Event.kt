package com.cleanseproject.cleanse.dataClasses

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Event(
        var id: String? = "",
        var name: String? = "",
        var description: String? = "",
        @Exclude
        var latitude: Double? = 0.0,
        @Exclude
        var longitude: Double? = 0.0
)
