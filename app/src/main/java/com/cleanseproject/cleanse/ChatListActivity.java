package com.cleanseproject.cleanse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class ChatListActivity extends AppCompatActivity {

    private ListView chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        chatList = findViewById(R.id.chat_list);
    }
}
