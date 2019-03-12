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
        var longitude: Double? = 0.0,
        @Exclude
        var distance: Float = 0f,
        @Exclude
        var isFavourite: Boolean = false,
        @Exclude
        var eventDate: Long = 0,
        var creatorId: String? = "",
        var state: Int? = 0
) : Comparable<Event> {

    override fun compareTo(other: Event): Int {
        if (distance != -1f && other.distance != -1f) {
            if (distance < other.distance)
                return -1
            else if (distance > other.distance)
                return 1
            else
                return 0
        } else {
            if (eventDate < other.eventDate)
                return -1
            else if (eventDate > other.eventDate)
                return 1
            else
                return 0
        }
    }
}
