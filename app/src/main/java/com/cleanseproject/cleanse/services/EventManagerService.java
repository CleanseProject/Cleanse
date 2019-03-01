package com.cleanseproject.cleanse.services;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cleanseproject.cleanse.callbacks.EventLoadCallback;
import com.cleanseproject.cleanse.callbacks.IsAdminCallback;
import com.cleanseproject.cleanse.callbacks.KeysLoadCallback;
import com.cleanseproject.cleanse.dataClasses.Event;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventManagerService {

    private ChatManagerService chatManagerService;
    private ImageManagerService imageManagerService;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private GeoFire geoFire;

    public EventManagerService() {
        chatManagerService = new ChatManagerService();
        imageManagerService = new ImageManagerService();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference geoFireRef = firebaseDatabase.getReference("geofire");
        geoFire = new GeoFire(geoFireRef);
    }

    public void createEvent(Event event, Uri image, EventLoadCallback callback) {
        DatabaseReference events = firebaseDatabase.getReference("events");
        String eventKey = events.push().getKey();
        event.setId(eventKey);
        event.setCreatorId(firebaseUser.getUid());
        events.child(eventKey).setValue(event);
        geoFire.setLocation(eventKey, new GeoLocation(event.getLatitude(),
                        event.getLongitude()),
                (key, error) -> {

                });
        chatManagerService.createGroupChat(eventKey, event.getName());
        if (image != null) {
            imageManagerService.uploadEventImage(eventKey, image);
        }
        callback.onEventLoaded(event);
    }

    public void deleteEvent(String key) {
        chatManagerService.removeChat(key, () -> {
            DatabaseReference eventRef = firebaseDatabase.getReference("events").child(key);
            eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("members").hasChildren()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.child("members").getChildren()) {
                            firebaseDatabase.getReference("userEvents")
                                    .child(userSnapshot.getValue().toString())
                                    .child(key)
                                    .removeValue();
                        }
                    }
                    firebaseDatabase.getReference("events")
                            .child(key)
                            .removeValue();
                    imageManagerService.removeEventImage(key);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });
    }

    public void isUserAdmin(String key, IsAdminCallback callback) {
        firebaseDatabase.getReference("events").child(key).child("creatorId")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        callback.onLoad(dataSnapshot.getValue().toString()
                                .equals(firebaseUser.getUid()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void getEvent(String key, EventLoadCallback callback) {
        DatabaseReference eventsRef = firebaseDatabase.getReference("events").child(key);
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                if (event != null) {
                    event.setId(dataSnapshot.getKey());
                    callback.onEventLoaded(event);
                } else {
                    Log.d("Firebase", "Null Event returned");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getCloseEvents(GeoLocation location, double radius, EventLoadCallback callback) {
        GeoQuery geoQuery = geoFire.queryAtLocation(location, radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("event", key);
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

    public void getFavouriteEvents(EventLoadCallback callback) {
        DatabaseReference userEventsRef = firebaseDatabase.getReference("userEvents").child(firebaseUser.getUid());
        userEventsRef.keepSynced(true);
        userEventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot event : dataSnapshot.getChildren()) {
                    getEvent(event.getKey(), callback);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getFavouriteKeys(KeysLoadCallback callback) {
        ArrayList<String> keys = new ArrayList<>();
        DatabaseReference userEventsRef = firebaseDatabase.getReference("userEvents").child(firebaseUser.getUid());
        userEventsRef.keepSynced(true);
        userEventsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                keys.add(dataSnapshot.getValue().toString());
                if (keys.size() >= dataSnapshot.getChildrenCount())
                    callback.onKeysLoad(keys);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setEventAsFavourite(String eventId) {
        String userId = firebaseUser.getUid();
        firebaseDatabase.getReference("events").child(eventId).child("members").child(userId).setValue(userId);
        firebaseDatabase.getReference("userEvents").child(userId).child(eventId).setValue(eventId);
    }

    public void deleteFavouriteEvent(String eventId) {
        String userId = firebaseUser.getUid();
        firebaseDatabase.getReference("events").child(eventId).child("members").child(userId).removeValue();
        firebaseDatabase.getReference("userEvents").child(firebaseUser.getUid()).child(eventId).removeValue();
    }

}
