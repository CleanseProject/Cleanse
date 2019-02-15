package com.cleanseproject.cleanse.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cleanseproject.cleanse.activities.ChatActivity;
import com.cleanseproject.cleanse.dataClasses.Chat;
import com.cleanseproject.cleanse.dataClasses.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatService {

    private ChatActivity chatActivity;
    private Chat chat;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;

    public ChatService(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

    }

    public void inicializar(String chatId) {
        DatabaseReference chats = firebaseDatabase.getReference();
        chats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chat = dataSnapshot.child("chats").child(chatId).getValue(Chat.class);
                chat.setChatUid(chatId);
                getMessages();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendMessage(String message) {
        DatabaseReference chatMessages = firebaseDatabase.getReference("chatMessages");
        chatMessages.child(chat.getChatUid()).push().setValue(new Message(message, firebaseUser.getUid(), System.currentTimeMillis()));
        getMessages();
        DatabaseReference chatUsers = firebaseDatabase.getReference("chats/" + chat.getChatUid() + "/members");
        chatUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataUser : dataSnapshot.getChildren()) {
                    String user = dataUser.getValue().toString();
                    if (!user.equals(firebaseUser.getUid()))
                        sendNotificationToUser(user, firebaseUser.getDisplayName() + ": " + message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getMessages() {
        DatabaseReference chatMessages = firebaseDatabase.getReference("chatMessages").child(chat.getChatUid());
        chatMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Message> messages = new ArrayList<>();
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    Message message = messageData.getValue(Message.class);
                    messages.add(message);
                }
                chatActivity.updateMessages(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d("updated", "updated");
    }

    public static void sendNotificationToUser(String user, final String message) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference notifications = ref.child("notificationRequests");
        Map<String, String> notification = new HashMap<>();
        notification.put("username", user);
        notification.put("message", message);
        notifications.push().setValue(notification);
    }

}
