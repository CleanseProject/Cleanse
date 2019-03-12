package com.cleanseproject.cleanse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
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
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class ChatActivity extends AppCompatActivity {

    private ChatService chatService;

    private Button btnSend;
    private EditText txtMessage;
    private RecyclerView messageRecycler;

    private ArrayList<Message> messages;
    private MessageListAdapter messageListAdapter;
    private HashMap<String, Integer> userColors;
    private int[] chatColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        //TODO: Set chatname from service
        toolbar.setTitle(getIntent().getStringExtra("chatname"));
        setSupportActionBar(toolbar);
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        btnSend = findViewById(R.id.button_chatbox_send);
        btnSend.setOnClickListener(v -> sendMessage());
        txtMessage = findViewById(R.id.edittext_chatbox);
        txtMessage.addTextChangedListener(sendTextWatcher);
        messageRecycler = findViewById(R.id.reyclerview_message_list);
        messageRecycler.setHasFixedSize(true);
        messageRecycler.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) {
                messageRecycler.postDelayed(
                        () -> messageRecycler.scrollToPosition(
                                messageListAdapter.getItemCount() - 1), 100);
            }
        });
        messageRecycler.setLayoutManager(new GridLayoutManager(this, 1));
        messages = new ArrayList<>();
        userColors = new HashMap<>();
        messageListAdapter = new MessageListAdapter(messages, userColors);
        messageRecycler.setAdapter(messageListAdapter);
        chatService = new ChatService(this::updateMessages);
        String chatId = getIntent().getStringExtra("chatuid");
        Bundle bundle = getIntent().getExtras();
        if (chatId == null && bundle != null) {
            //Get data from notification
            chatId = bundle.getString("chatuid");
        }
        chatColors = getResources().getIntArray(R.array.chat_color_array);
        chatService.inicializar(chatId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navigateUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        navigateUp();
    }

    private void navigateUp() {
        Intent intent = NavUtils.getParentActivityIntent(this);
        if (intent != null) {
            intent.putExtra("fragment", "chats");
            // Check if Activity has been opened from notification
            if (NavUtils.shouldUpRecreateTask(this, intent) || isTaskRoot()) {
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(intent)
                        .startActivities();
            } else {
                NavUtils.navigateUpTo(this, intent);
            }
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

    private final TextWatcher sendTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            if (text.toString().trim().equals(""))
                btnSend.setEnabled(false);
            else
                btnSend.setEnabled(true);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void updateMessages(Message message) {
        String user = message.getUser();
        if (!userColors.containsKey(user)) {
            userColors.put(message.getUser(), chatColors[new Random().nextInt(chatColors.length)]);
        }
        messages.add(message);
        messageListAdapter.notifyDataSetChanged();
        messageRecycler.scrollToPosition(messageListAdapter.getItemCount() - 1);
    }

    private void sendMessage() {
        chatService.sendMessage(txtMessage.getText().toString().trim());
        messageRecycler.scrollToPosition(messageListAdapter.getItemCount() - 1);
        txtMessage.setText("");
        btnSend.setEnabled(false);
    }

}
