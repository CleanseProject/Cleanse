package com.cleanseproject.cleanse.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.dataClasses.Chat;
import com.cleanseproject.cleanse.services.ChatManagerService;
import com.cleanseproject.cleanse.services.ImageManagerService;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Chat> chatRows;
    private ImageManagerService imageManagerService;
    private ChatManagerService chatManagerService;

    public ChatListAdapter(Context context, ArrayList<Chat> chatRows) {
        this.context = context;
        this.chatRows = chatRows;
        imageManagerService = new ImageManagerService();
        chatManagerService = new ChatManagerService();
    }

    @Override
    public int getCount() {
        return chatRows.size();
    }

    @Override
    public Chat getItem(int position) {
        return chatRows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chat_list, null);
        Chat chat = chatRows.get(position);
        TextView lblName = view.findViewById(R.id.chat_row_username);
        TextView lblLastMessage = view.findViewById(R.id.chat_list_last_message);
        ImageView chatImage = view.findViewById(R.id.chat_row_user_img);
        TextView txtUnreadNum = view.findViewById(R.id.txt_unread_num);
        if (chat.getGroupChat()) {
            imageManagerService.eventImageDownloadUrl(chat.getChatUid(),
                    imageUrl -> {
                        Glide.with(view)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_user)
                                .apply(RequestOptions.circleCropTransform())
                                .into(chatImage);
                    });
        }
        lblName.setText(chat.getChatName());
        lblLastMessage.setText(chat.getLastMessageSent());
        chatManagerService.hasUnreadMessages(chat.getChatUid(),
                unread -> {
                    txtUnreadNum.setVisibility(unread > 0 ? View.VISIBLE : View.GONE);
                    txtUnreadNum.setText(String.valueOf(unread));
                });
        return view;
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<Chat> getUserRows() {
        return chatRows;
    }
}
