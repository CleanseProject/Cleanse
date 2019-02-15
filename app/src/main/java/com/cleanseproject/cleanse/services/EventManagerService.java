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

    public EventManagerService() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference geoFireRef = firebaseDatabase.getReference("geofire");
        geoFire = new GeoFire(geoFireRef);
        createEvent(new Event("Areeta", "", "", "37.7853889", "-122.4056973"));
        getCloseEvents(new GeoLocation(37.7832, -122.4056), 10);
    }

    public void createEvent(Event event) {
        DatabaseReference events = firebaseDatabase.getReference("events");
        String eventKey = events.push().getKey();
        events.child(eventKey).setValue(event);
        geoFire.setLocation(eventKey, new GeoLocation(Double.parseDouble(event.getLatitude()), Double.parseDouble(event.getLongitude())),
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {

                    }
                });
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

    public void getCloseEvents(GeoLocation location, double radius) {
        GeoQuery geoQuery = geoFire.queryAtLocation(location, radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("geofire", "Location found:" + key + " latitude: " + location.latitude + " longitude: " + location.longitude);
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
