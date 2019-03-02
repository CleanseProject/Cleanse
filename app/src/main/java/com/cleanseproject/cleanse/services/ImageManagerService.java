package com.cleanseproject.cleanse.services;

import android.net.Uri;

import com.cleanseproject.cleanse.callbacks.ImageUrlLoadCallback;
import com.google.firebase.storage.FirebaseStorage;

public class ImageManagerService {

    private FirebaseStorage firebaseStorage;

    public ImageManagerService() {
        firebaseStorage = FirebaseStorage.getInstance();
    }

    public void uploadEventImage(String eventId, Uri filePath) {
        firebaseStorage.getReference("images/events/" + eventId).putFile(filePath);
    }

    public void uploadUserImage(String userid, Uri filePath) {
        firebaseStorage.getReference("images/users/" + userid).putFile(filePath);
    }

    public void eventImageDownloadUrl(String eventId, ImageUrlLoadCallback callback) {
        firebaseStorage.getReference("images/events/" + eventId).getDownloadUrl()
                .addOnSuccessListener(uri -> callback.onUrlLoaded(uri.toString()))
                .addOnFailureListener(e -> {
                    //TODO: Set default image
                });
    }

}
