package com.cleanseproject.cleanse.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.adapters.ChatListAdapter;
import com.cleanseproject.cleanse.dataClasses.User;
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
    }

    private void getUserChats() {
        Log.d("id", firebaseUser.getUid());

        DatabaseReference userChats = firebaseDatabase.getReference("userChats").child(firebaseUser.getUid());
        userChats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> userIds = new ArrayList<>();
                ArrayList<User> users = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d("firebaseUid", snapshot.getKey());
                        userIds.add(snapshot.getKey());
                        DatabaseReference user = firebaseDatabase.getReference("users");
                        user.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (String uid : userIds) {
                                    users.add(dataSnapshot.child(uid).getValue(User.class));
                                }
                                populateList(users);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    private void populateList(ArrayList<User> users) {
        ChatListAdapter chatListAdapter = new ChatListAdapter(getActivity(), users);
        chatList.setAdapter(chatListAdapter);
    }

}
