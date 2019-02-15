package com.cleanseproject.cleanse.services;

import android.support.annotation.NonNull;

import com.cleanseproject.cleanse.callbacks.EventsLoadCallback;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventManagerService {

    private FirebaseDatabase firebaseDatabase;

    public EventManagerService() {
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public void createEvent(Event event) {
        DatabaseReference events = firebaseDatabase.getReference("events");
        events.push().setValue(event);
    }

    public void getEvents(EventsLoadCallback callback) {
        DatabaseReference eventsRef = firebaseDatabase.getReference("events");
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Event> events = new ArrayList<>();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    events.add(eventSnapshot.getValue(Event.class));
                }
                callback.onEventsLoaded(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
