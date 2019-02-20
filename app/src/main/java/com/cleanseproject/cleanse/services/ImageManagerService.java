package com.cleanseproject.cleanse.services;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ImageManagerService {

    private FirebaseStorage firebaseStorage;

    public ImageManagerService() {
        firebaseStorage = FirebaseStorage.getInstance();
    }

    public void uploadEventImage(String eventId, Uri filePath) {
        StorageReference storageReference = firebaseStorage.getReference("images/events/" + eventId);
        UploadTask uploadTask = storageReference.putFile(filePath);
    }

}
