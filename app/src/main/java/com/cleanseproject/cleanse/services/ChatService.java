package com.cleanseproject.cleanse.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cleanseproject.cleanse.callbacks.MessagesLoadCallback;
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

    private Chat chat;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private MessagesLoadCallback messagesLoadCallback;

    public ChatService(MessagesLoadCallback messagesLoadCallback) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        this.messagesLoadCallback = messagesLoadCallback;
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
        firebaseDatabase.getReference("chats").child(chat.getChatUid()).child("lastMessageTime").setValue(System.currentTimeMillis());
        firebaseDatabase.getReference("chats").child(chat.getChatUid()).child("lastMessageSent").setValue(message);
        DatabaseReference chatMessages = firebaseDatabase.getReference("chatMessages");
        chatMessages.child(chat.getChatUid()).push().setValue(new Message(message, firebaseUser.getUid(), System.currentTimeMillis()));
        getMessages();
        DatabaseReference chatUsers = firebaseDatabase.getReference("chats/" + chat.getChatUid() + "/members");
        chatUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataUser : dataSnapshot.getChildren()) {
                    String user = dataUser.getValue().toString();
                    if (!user.equals(firebaseUser.getUid())) {
                        sendNotificationToUser(user, firebaseUser.getDisplayName() + ": " + message);
                        firebaseDatabase.getReference("userChats").child(user).child(chat.getChatUid()).child("unread").setValue(true);
                    }
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
                messagesLoadCallback.onMessagesLoaded(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        firebaseDatabase.getReference("userChats").child(firebaseUser.getUid()).child(chat.getChatUid()).child("unread").setValue(false);
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
