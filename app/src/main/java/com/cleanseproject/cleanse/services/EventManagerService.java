package com.cleanseproject.cleanse.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cleanseproject.cleanse.callbacks.EventsLoadCallback;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventManagerService {

    private FirebaseDatabase firebaseDatabase;
    private GeoFire geoFire;

    private ArrayList<Event> events;

    public EventManagerService() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference geoFireRef = firebaseDatabase.getReference("geofire");
        geoFire = new GeoFire(geoFireRef);
    }

    public void createEvent(Event event) {
        DatabaseReference events = firebaseDatabase.getReference("events");
        String eventKey = events.push().getKey();
        events.child(eventKey).setValue(event);
        geoFire.setLocation(eventKey, new GeoLocation(Double.parseDouble(event.getLatitude()),
                        Double.parseDouble(event.getLongitude())),
                (key, error) -> {

                });
    }

    public void getEvent(String key, EventsLoadCallback callback) {
        DatabaseReference eventsRef = firebaseDatabase.getReference("events").child(key);
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                if (event != null) {
                    event.setId(dataSnapshot.getKey());
                    events.add(event);
                    callback.onEventsLoaded(events);
                } else {
                    Log.d("Firebase", "Null Event returned");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getCloseEvents(GeoLocation location, double radius, EventsLoadCallback callback) {
        events = new ArrayList<>();
        GeoQuery geoQuery = geoFire.queryAtLocation(location, radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                getEvent(key, callback);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

}
