package com.cleanseproject.cleanse.services;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import java.net.URI;

public class UserManagerService {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseStorage firebaseStorage;

    public UserManagerService() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    public void setProfilePhoto(Uri imagePath) {
        firebaseStorage.getReference("users").child(firebaseUser.getUid())
                .child("profilePhoto").putFile(imagePath);
    }

    public void getProfilePhotoDownloadUrl(String userId){

    }

}
