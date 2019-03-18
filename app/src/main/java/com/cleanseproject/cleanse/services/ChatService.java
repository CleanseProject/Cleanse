package com.cleanseproject.cleanse.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cleanseproject.cleanse.callbacks.MessageLoadCallback;
import com.cleanseproject.cleanse.dataClasses.Chat;
import com.cleanseproject.cleanse.dataClasses.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages chat instances
 * Initializes opened chats,, sends messages and push notifications
 * @author Cleanse Project
 */
public class ChatService {

    private Chat chat;
    private final FirebaseUser firebaseUser;
    private final FirebaseDatabase firebaseDatabase;
    private final MessageLoadCallback messageLoadCallback;
    private final ChatManagerService chatManagerService;

    public ChatService(MessageLoadCallback messageLoadCallback) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        chatManagerService = new ChatManagerService();
        this.messageLoadCallback = messageLoadCallback;
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
        DatabaseReference chatUsers = firebaseDatabase.getReference("chats/" + chat.getChatUid() + "/members");
        chatUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataUser : dataSnapshot.getChildren()) {
                    String user = dataUser.getValue().toString();
                    if (!user.equals(firebaseUser.getUid())) {
                        chatManagerService.getUserName(
                                firebaseUser.getUid(),
                                username -> sendNotificationToUser(user,
                                        username +
                                                (chat.getGroupChat() ? " @" + chat.getChatName() : "")
                                        , message));
                        firebaseDatabase.getReference("userChats").child(user).child(chat.getChatUid()).child("unread").runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                int unreadNum = mutableData.getValue(Integer.class);
                                mutableData.setValue(++unreadNum);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getMessages() {
        DatabaseReference chatMessages = firebaseDatabase.getReference("chatMessages").child(chat.getChatUid());
        chatMessages.orderByChild("createdAt").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messageLoadCallback.onMessageLoaded(message);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        firebaseDatabase.getReference("userChats").child(firebaseUser.getUid()).child(chat.getChatUid()).child("unread").setValue(0);
    }

    private void sendNotificationToUser(String user, String title, final String message) {
        final DatabaseReference notifications = firebaseDatabase.getReference().child("notificationRequests");
        Map<String, String> notification = new HashMap<>();
        notification.put("username", user);
        notification.put("title", title);
        notification.put("message", message);
        notification.put("chatuid", chat.getChatUid());
        notifications.push().setValue(notification);
    }

}
