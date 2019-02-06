package com.cleanseproject.cleanse.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.adapters.MessageListAdapter;
import com.cleanseproject.cleanse.dataClasses.Chat;
import com.cleanseproject.cleanse.dataClasses.Message;
import com.cleanseproject.cleanse.dataClasses.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
    private FirebaseAuth firebaseAuth;
    private Chat chat;
    private ArrayList<User> users;
    private User currentUser;

    private EditText txtMessage;
    private RecyclerView messageRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        loadData(getIntent().getStringExtra("chatuid"));
        Button btnSend = findViewById(R.id.button_chatbox_send);
        btnSend.setOnClickListener(v -> sendMessage());
        txtMessage = findViewById(R.id.edittext_chatbox);
        messageRecycler = findViewById(R.id.reyclerview_message_list);
        messageRecycler.setHasFixedSize(true);
        messageRecycler.setLayoutManager(new GridLayoutManager(this,1));
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
        chat.setChatUid(chatuid);
        Log.d("exists", dataSnapshot.child("userChats").child(chatuid).exists() + "");
        Log.d("userKey", chat.toString());
        users = new ArrayList<>();
        for (String uid : chat.getMembers()) {
            Log.d("userKey", uid);
            User user = dataSnapshot.child("users").child(uid).getValue(User.class);
            user.setUserId(uid);
            if (firebaseAuth.getUid().equals(uid))
                currentUser = user;
            users.add(user);
        }
        DatabaseReference chatMessages = firebaseDatabase.getReference("chatMessages").child(chat.getChatUid());
        chatMessages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                updateMessages(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateMessages(DataSnapshot dataSnapshot) {
        ArrayList<Message> messages = new ArrayList<>();
        for (DataSnapshot messageData : dataSnapshot.getChildren()) {
            Message message = messageData.getValue(Message.class);
            messages.add(message);
            Log.d("message",message.getMessage());
        }
        Log.d("updated","updated");
        MessageListAdapter messageListAdapter = new MessageListAdapter(this, messages);
        messageRecycler.setAdapter(messageListAdapter);
    }

    private void sendMessage() {
        DatabaseReference chatMessages = firebaseDatabase.getReference("chatMessages");
        chatMessages.child(chat.getChatUid()).push().setValue(new Message(txtMessage.getText().toString(), currentUser.getUserId(), System.currentTimeMillis()));
    }

}
