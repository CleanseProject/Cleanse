package com.cleanseproject.cleanse.services;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;

public class ImageManagerService {

    private FirebaseStorage firebaseStorage;

    public ImageManagerService() {
        firebaseStorage = FirebaseStorage.getInstance();
    }

    public void uploadEventImage(String eventId, Uri filePath) {
        firebaseStorage.getReference("images/events/" + eventId).putFile(filePath);
    }

}
