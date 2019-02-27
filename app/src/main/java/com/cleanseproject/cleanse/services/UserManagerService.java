package com.cleanseproject.cleanse.services;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.cleanseproject.cleanse.callbacks.UserLoadCallback;
import com.cleanseproject.cleanse.dataClasses.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class UserManagerService {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;

    public UserManagerService() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public void getUser(String userId, UserLoadCallback callback) {
        DatabaseReference userRef = firebaseDatabase.getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                callback.onUserLoad(dataSnapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setProfilePhoto(Uri imagePath) {
        firebaseStorage.getReference("users").child(firebaseUser.getUid())
                .child("profilePhoto").putFile(imagePath);
    }

    public void getProfilePhotoDownloadUrl(String userId) {

    }

}
