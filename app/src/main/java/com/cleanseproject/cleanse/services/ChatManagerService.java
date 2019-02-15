package com.cleanseproject.cleanse.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ChatManagerService {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    public ChatManagerService(){
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
    }

    

}
