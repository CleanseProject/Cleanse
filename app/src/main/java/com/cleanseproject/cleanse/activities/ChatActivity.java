package com.cleanseproject.cleanse.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.dataClasses.Chat;
import com.cleanseproject.cleanse.dataClasses.Message;
import com.cleanseproject.cleanse.dataClasses.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private Chat chat;
    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        firebaseDatabase = FirebaseDatabase.getInstance();
        loadData(getIntent().getStringExtra("chatUid"));
    }

    private void loadData(String chatUid) {
        DatabaseReference users = firebaseDatabase.getReference();
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                initializeView(dataSnapshot, chatUid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeView(DataSnapshot dataSnapshot, String chatuid) {
        chat = dataSnapshot.child("chats").child(chatuid).getValue(Chat.class);
        users = new ArrayList<>();
        for (String key : chat.getMembers().keySet()) {
            Log.d("userKey", key);
            User user = dataSnapshot.child("users").child(key).getValue(User.class);
            user.setUserId(key);
            users.add(user);
        }
    }

}
