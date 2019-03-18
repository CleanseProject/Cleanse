package com.cleanseproject.cleanse.services;

import android.net.Uri;

import com.cleanseproject.cleanse.callbacks.ImageUrlLoadCallback;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.List;

/**
 * Manages Firebase Storage images
 *
 * @author Cleanse Project
 */
public class ImageManagerService {

    private final FirebaseStorage firebaseStorage;

    /**
     * Class constructor
     * Gets the current Firebase Storage instance
     */
    public ImageManagerService() {
        firebaseStorage = FirebaseStorage.getInstance();
    }

    /**
     * Uploads an event image
     *
     * @param eventId  Key of the event
     * @param filePath Local uri of the image
     */
    public void uploadEventImage(String eventId, Uri filePath) {
        firebaseStorage.getReference("images/events/" + eventId).putFile(filePath);
    }

    /**
     * Removes an event image
     *
     * @param eventId Key of the event
     */
    public void removeEventImage(String eventId) {
        firebaseStorage.getReference("images/events/" + eventId).delete();
    }

    /**
     * Uploades a user profile picture
     *
     * @param userid   User key
     * @param filePath Uri of the local file
     */
    public void uploadUserImage(String userid, Uri filePath) {
        firebaseStorage.getReference("images/users/" + userid).putFile(filePath);
    }

    /**
     * Returns the download URL of  an event image
     *
     * @param eventId  Key of the event
     * @param callback Returns the URL
     */
    public void eventImageDownloadUrl(String eventId, ImageUrlLoadCallback callback) {
        // Checks if an image is being uploaded (useful if the event has just been created)
        List<UploadTask> uploadTasks = firebaseStorage.getReference("images/events/" + eventId).getActiveUploadTasks();
        if (uploadTasks.size() > 0) {
            // If an upload task exists, it waits for it to end
            uploadTasks.get(0).addOnCompleteListener(task -> {
                firebaseStorage.getReference("images/events/" + eventId).getDownloadUrl()
                        .addOnSuccessListener(uri -> callback.onUrlLoaded(uri.toString()))
                        .addOnFailureListener(e -> {
                            //TODO: Set default image
                        });
            });
        } else {
            // Otherwise, loads the image URL
            firebaseStorage.getReference("images/events/" + eventId).getDownloadUrl()
                    .addOnSuccessListener(uri -> callback.onUrlLoaded(uri.toString()))
                    .addOnFailureListener(e -> {
                        //TODO: Set default image
                    });
        }
    }

    /**
     * Returns the download URL of an user profile picture
     * @param userId User id
     * @param callback Called on loaded
     */
    public void userImageDownloadUrl(String userId, ImageUrlLoadCallback callback) {
        firebaseStorage.getReference("images/users/" + userId).getDownloadUrl()
                .addOnSuccessListener(uri -> callback.onUrlLoaded(uri.toString()))
                .addOnFailureListener(e -> {
                    //TODO: Set default image
                });
    }

}
