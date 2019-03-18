package com.cleanseproject.cleanse.services;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cleanseproject.cleanse.callbacks.EventLoadCallback;
import com.cleanseproject.cleanse.callbacks.IsAdminCallback;
import com.cleanseproject.cleanse.callbacks.IsFavouriteCallback;
import com.cleanseproject.cleanse.callbacks.KeysLoadCallback;
import com.cleanseproject.cleanse.callbacks.UserChangedCallback;
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

/**
 * Manages events from Firebase
 * @author Cleanse Project
 */
public class EventManagerService {

    private final ChatManagerService chatManagerService;
    private final ImageManagerService imageManagerService;
    private final UserManagerService userManagerService;
    private final FirebaseUser firebaseUser;
    private final FirebaseDatabase firebaseDatabase;
    private final GeoFire geoFire;

    /**
     * Constructor for the class
     * Gets Firebase and Geofire instances
     */
    public EventManagerService() {
        chatManagerService = new ChatManagerService();
        imageManagerService = new ImageManagerService();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userManagerService = new UserManagerService();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference geoFireRef = firebaseDatabase.getReference("geofire");
        geoFire = new GeoFire(geoFireRef);
    }

    /**
     * Creates new event on Firebase database
     * @param event Event to be created
     * @param image Event image uri
     * @param callback Executed when event is created, returns created event
     */
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
        setEventAsFavourite(eventKey);
        callback.onEventLoaded(event);
    }

    /**
     * Deletes event
     * @param key Key of the event to be deleted
     */
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
                    geoFire.removeLocation(key, (key1, error) -> {

                    });
                    imageManagerService.removeEventImage(key);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });
    }

    /**
     * Checks if logged Firebase user is admin of the event
     * @param key Event key
     * @param callback Returns true if the user is admin
     */
    public void isUserAdmin(String key, IsAdminCallback callback) {
        firebaseDatabase.getReference("events").child(key).child("creatorId")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Object objCreatorId = dataSnapshot.getValue();
                        if (objCreatorId != null)
                            callback.onLoad(objCreatorId.toString()
                                    .equals(firebaseUser.getUid()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Gets an event
     * @param key Event to be loaded
     * @param callback Returns the selected event
     */
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

    /**
     * Gets event close to provided location
     * @param location Location to search events from
     * @param radius Radios of the search
     * @param callback Returns an event within the radius, single null event if none
     */
    public void getCloseEvents(GeoLocation location, double radius, EventLoadCallback callback) {
        GeoQuery geoQuery = geoFire.queryAtLocation(location, radius);
        final boolean[] eventsReturned = {false};
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                eventsReturned[0] = true;
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
                if (!eventsReturned[0])
                    callback.onEventLoaded(null);
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    /**
     * Get all upcoming events ordered by date
     * @param callback Returns events within parameters
     */
    public void getUpcomingEvents(EventLoadCallback callback) {
        firebaseDatabase.getReference("events").orderByChild("createdAt")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot eventSnapchot : dataSnapshot.getChildren()) {
                            Event event = eventSnapchot.getValue(Event.class);
                            // Checks whether event is null or after yesterday
                            if (event != null && event.getEventDate() > (System.currentTimeMillis() - 86400000L)) {
                                Log.d("time", "" + System.currentTimeMillis());
                                event.setId(eventSnapchot.getKey());
                                callback.onEventLoaded(event);
                            } else {
                                Log.d("Firebase", "Null Event returned");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Get logged user favourite events
     * @param callback Returns a single favourite event
     */
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

    /**
     * Get the keys of current users's favourite events
     * @param callback Returns a single event within parameters
     */
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

    /**
     * Get the users of an event
     * @param eventId Key of the event
     * @param loadCallback Called by every user of the event
     */
    public void getEventUsers(String eventId, UserChangedCallback loadCallback) {
        firebaseDatabase.getReference("events").child(eventId).child("members")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        userManagerService.getUser(dataSnapshot.getValue().toString(), loadCallback::onUserLoad);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        loadCallback.userRemoved(dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Checks if an event is favourite by the current user
     * @param eventId Key of the event
     * @param callback Returns true if the event is favourite
     */
    public void isEventFavourite(String eventId, IsFavouriteCallback callback) {
        String userId = firebaseUser.getUid();
        firebaseDatabase.getReference("userEvents").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        callback.onLoad(dataSnapshot.child(eventId).exists());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Sets the event as favourite by the current user
     * @param eventId Key of the event
     */
    public void setEventAsFavourite(String eventId) {
        String userId = firebaseUser.getUid();
        firebaseDatabase.getReference("events").child(eventId).child("members").child(userId).setValue(userId);
        firebaseDatabase.getReference("userEvents").child(userId).child(eventId).setValue(eventId);
    }

    /**
     * Deletes event as favourite
     * @param eventId Key of the event
     */
    public void deleteFavouriteEvent(String eventId) {
        String userId = firebaseUser.getUid();
        firebaseDatabase.getReference("events").child(eventId).child("members").child(userId).removeValue();
        firebaseDatabase.getReference("userEvents").child(firebaseUser.getUid()).child(eventId).removeValue();
    }

}
