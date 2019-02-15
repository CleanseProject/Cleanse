package com.cleanseproject.cleanse.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.adapters.MessageListAdapter;
import com.cleanseproject.cleanse.dataClasses.Message;
import com.cleanseproject.cleanse.services.ChatService;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private ChatService chatService;

    private EditText txtMessage;
    private RecyclerView messageRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        toolbar.setTitle(getIntent().getStringExtra("chatname"));
        Button btnSend = findViewById(R.id.button_chatbox_send);
        btnSend.setOnClickListener(v -> sendMessage());
        txtMessage = findViewById(R.id.edittext_chatbox);
        messageRecycler = findViewById(R.id.reyclerview_message_list);
        messageRecycler.setHasFixedSize(true);
        messageRecycler.setLayoutManager(new GridLayoutManager(this, 1));
        chatService = new ChatService(this::updateMessages);
        chatService.inicializar(getIntent().getStringExtra("chatuid"));
    }


    public void updateMessages(ArrayList<Message> messages) {
        MessageListAdapter messageListAdapter = new MessageListAdapter(messages);
        messageRecycler.setAdapter(messageListAdapter);
        messageRecycler.scrollToPosition(messageRecycler.getAdapter().getItemCount() - 1);
    }

    private void sendMessage() {
        chatService.sendMessage(txtMessage.getText().toString());
        messageRecycler.scrollToPosition(messageRecycler.getAdapter().getItemCount() - 1);
    }

}
