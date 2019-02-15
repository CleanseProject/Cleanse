package com.cleanseproject.cleanse.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cleanseproject.cleanse.callbacks.ChatListLoadCallback;
import com.cleanseproject.cleanse.dataClasses.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatManagerService {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    public ChatManagerService() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void createChat(ArrayList<String> userIds) {
        DatabaseReference chat = firebaseDatabase.getReference("chats").push();
        String chatKey = chat.getKey();
        chat.setValue(new Chat(chatKey, "", userIds, ""));
        DatabaseReference userChats = firebaseDatabase.getReference("userChats");
        for (String userId : userIds) {
            userChats.child(userId).push().setValue(chatKey);
        }
    }

    public ArrayList<Chat> getUserChats(ChatListLoadCallback callback) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference userChats = firebaseDatabase.getReference();
        userChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("chatid", firebaseUser.getUid());
                DataSnapshot userChatsData = dataSnapshot.child("userChats").child(firebaseUser.getUid());
                ArrayList<Chat> chats = new ArrayList<>();
                for (DataSnapshot objChatId : userChatsData.getChildren()) {
                    String chatId = objChatId.getValue().toString();
                    Log.d("chatid", chatId);
                    Chat chat = dataSnapshot.child("chats").child(chatId).getValue(Chat.class);
                    if (chat != null) {
                        chat.setChatUid(chatId);
                        chats.add(chat);
                    }
                }
                callback.onCallBack(chats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return null;
    }

}
