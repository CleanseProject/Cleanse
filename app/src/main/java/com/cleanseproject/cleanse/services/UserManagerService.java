package com.cleanseproject.cleanse.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserManagerService {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    public UserManagerService() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    public void setProfilePhoto(){
        
    }

}
