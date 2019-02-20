package com.cleanseproject.cleanse.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.adapters.MessageListAdapter;
import com.cleanseproject.cleanse.dataClasses.Message;
import com.cleanseproject.cleanse.services.ChatService;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private ChatService chatService;

    private Button btnSend;
    private EditText txtMessage;
    private RecyclerView messageRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        toolbar.setTitle(getIntent().getStringExtra("chatname"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnSend = findViewById(R.id.button_chatbox_send);
        btnSend.setOnClickListener(v -> sendMessage());
        txtMessage = findViewById(R.id.edittext_chatbox);
        txtMessage.addTextChangedListener(sendTextWatcher);
        messageRecycler = findViewById(R.id.reyclerview_message_list);
        messageRecycler.setHasFixedSize(true);
        messageRecycler.setLayoutManager(new GridLayoutManager(this, 1));
        chatService = new ChatService(this::updateMessages);
        chatService.inicializar(getIntent().getStringExtra("chatuid"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private TextWatcher sendTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            if (text.equals(""))
                btnSend.setEnabled(false);
            else
                btnSend.setEnabled(true);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void updateMessages(ArrayList<Message> messages) {
        MessageListAdapter messageListAdapter = new MessageListAdapter(messages);
        messageRecycler.setAdapter(messageListAdapter);
        messageRecycler.scrollToPosition(messageRecycler.getAdapter().getItemCount() - 1);
    }

    private void sendMessage() {
        chatService.sendMessage(txtMessage.getText().toString());
        messageRecycler.scrollToPosition(messageRecycler.getAdapter().getItemCount() - 1);
        txtMessage.setText("");
    }

}
