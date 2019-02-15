package com.cleanseproject.cleanse.callbacks;

import com.cleanseproject.cleanse.dataClasses.Event;

import java.util.ArrayList;

public interface EventsLoadCallback {

    void onEventsLoaded(ArrayList<Event> events);

}
