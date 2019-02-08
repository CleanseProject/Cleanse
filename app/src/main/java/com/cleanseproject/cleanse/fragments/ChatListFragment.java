package com.cleanseproject.cleanse.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.activities.ChatActivity;
import com.cleanseproject.cleanse.adapters.ChatListAdapter;
import com.cleanseproject.cleanse.dataClasses.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatListFragment extends Fragment {

    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;

    private ListView chatList;
    private ChatListAdapter chatListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        chatList = view.findViewById(R.id.chat_list);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        getUserChats();
        chatList.setOnItemClickListener((parent, v, position, id) -> {
            Chat chat = chatListAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("chatuid", chat.getChatUid());
            startActivity(intent);
        });
    }

    private void getUserChats() {
        DatabaseReference userChats = firebaseDatabase.getReference();
        userChats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot userChatsData = dataSnapshot.child("userChats").child(firebaseUser.getUid());
                for (DataSnapshot userChat:userChatsData.getChildren()){
                }
                /*for (String chatId : userChats) {
                    Log.d("chatid", chatId);
                    Chat chat = dataSnapshot.child("chats").child(chatId).getValue(Chat.class);
                    chat.setChatUid(chatId);
                    chats.add(chat);
                }
                populateList(chats);*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void populateList(ArrayList<Chat> chats) {
        chatListAdapter = new ChatListAdapter(getActivity(), chats);
        chatList.setAdapter(chatListAdapter);
    }

}
