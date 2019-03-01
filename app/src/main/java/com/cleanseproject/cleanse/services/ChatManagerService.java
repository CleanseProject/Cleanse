package com.cleanseproject.cleanse.services;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cleanseproject.cleanse.callbacks.ChatListLoadCallback;
import com.cleanseproject.cleanse.callbacks.ChatRemovedCallback;
import com.cleanseproject.cleanse.callbacks.UnreadMessagesCallback;
import com.cleanseproject.cleanse.callbacks.UserNameLoadCallback;
import com.cleanseproject.cleanse.dataClasses.Chat;
import com.cleanseproject.cleanse.dataClasses.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ChatManagerService {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;

    public ChatManagerService() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    public void createChat(ArrayList<String> userIds) {
        DatabaseReference chat = firebaseDatabase.getReference("chats").push();
        String chatKey = chat.getKey();
        chat.setValue(new Chat(chatKey, "", null, "", false, System.currentTimeMillis()));
        for (String userId : userIds) {
            chat.child("members").push().setValue(userId);
        }
        DatabaseReference userChats = firebaseDatabase.getReference("userChats");
        for (String userId : userIds) {
            userChats.child(userId).push().setValue(chatKey);
        }
    }

    public void joinChat(String userId, String chatId) {
        firebaseDatabase.getReference("chats").child(chatId).child("members").child(userId).setValue(userId);
        firebaseDatabase.getReference("userChats").child(userId).child(chatId).child("chatId").setValue(chatId);
    }

    public void createGroupChat(String eventId, String name) {
        DatabaseReference chat = firebaseDatabase.getReference("chats").child(eventId);
        chat.setValue(new Chat(eventId, name, null, "", true, System.currentTimeMillis()));
    }

    public void removeChat(String chatId, ChatRemovedCallback callback) {
        DatabaseReference chatRef = firebaseDatabase.getReference("chats").child(chatId);
        chatRef.child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    firebaseDatabase.getReference("userChats")
                            .child(userSnapshot.getValue().toString())
                            .child(chatId)
                            .removeValue();
                }
                firebaseDatabase.getReference("chatMessages")
                        .child(chatId)
                        .removeValue();
                firebaseDatabase.getReference("chats")
                        .child(chatId)
                        .removeValue();
                callback.chatRemoved();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getUserChats(ChatListLoadCallback callback) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference userChats = firebaseDatabase.getReference();
        userChats.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot userChatsData = dataSnapshot.child("userChats").child(firebaseUser.getUid());
                DataSnapshot users = dataSnapshot.child("users");
                ArrayList<Chat> chats = new ArrayList<>();
                for (DataSnapshot objChatId : userChatsData.getChildren()) {
                    String chatId = objChatId.getKey();
                    Log.d("chatid", chatId);
                    Chat chat = dataSnapshot.child("chats").child(chatId).getValue(Chat.class);
                    if (!chat.getGroupChat()) {
                        for (String memberKey : chat.getMembers().keySet()) {
                            String member = chat.getMembers().get(memberKey);
                            User user = users.child(member).getValue(User.class);
                            if (!user.getUserId().equals(firebaseUser.getUid())) {
                                chat.setChatName(user.getName() + " " + user.getSurname());
                            }
                        }
                    }
                    chat.setChatUid(chatId);
                    chats.add(chat);
                }
                Collections.sort(chats);
                callback.onCallBack(chats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getUserName(String userId, UserNameLoadCallback callback) {
        DatabaseReference userRef = firebaseDatabase.getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.onUsernameLoaded(user.getName() + " " + user.getSurname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void hasUnreadMessages(String chatId, UnreadMessagesCallback callback) {
        firebaseDatabase.getReference("userChats").child(firebaseUser.getUid()).child(chatId).child("unread")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        callback.loadUnreadMessages(dataSnapshot.getValue(Boolean.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}
