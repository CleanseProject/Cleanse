package com.cleanseproject.cleanse.dataClasses

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

/**
 * Data class for Events objects on Firebase
 * Implements Comparable class in order to sort by distance
 * Overrides equals and hashCode to check if events exist on array
 * @author Cleanse Project
 */
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
        return if (distance != -1f && other.distance != -1f) {
            when {
                distance < other.distance -> -1
                distance > other.distance -> 1
                else -> 0
            }
        } else {
            when {
                eventDate < other.eventDate -> -1
                eventDate > other.eventDate -> 1
                else -> 0
            }
        }
    }

    override fun equals(other: Any?) = (other is Event)
            && id.equals(other.id)

    override fun hashCode(): Int {
        return id.hashCode()
    }

}
