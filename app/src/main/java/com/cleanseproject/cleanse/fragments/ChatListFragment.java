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
import com.cleanseproject.cleanse.services.ChatManagerService;

import java.util.ArrayList;
import java.util.Objects;

public class ChatListFragment extends Fragment {

    private ListView chatList;
    private ChatListAdapter chatListAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = Objects.requireNonNull(getView());
        chatList = view.findViewById(R.id.chat_list);
        chatList.setOnItemClickListener((parent, v, position, id) -> {
            Chat chat = chatListAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("chatuid", chat.getChatUid());
            intent.putExtra("chatname", chat.getChatName());
            startActivity(intent);
        });
        ChatManagerService chatManagerService = new ChatManagerService();
        chatManagerService.getUserChats(this::populateList);
    }


    private void populateList(ArrayList<Chat> chats) {
        chatListAdapter = new ChatListAdapter(getActivity(), chats);
        chatList.setAdapter(chatListAdapter);
    }

}
