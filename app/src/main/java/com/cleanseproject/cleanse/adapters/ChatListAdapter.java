package com.cleanseproject.cleanse.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.dataClasses.Chat;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Chat> chatRows;

    public ChatListAdapter(Context context, ArrayList<Chat> chatRows) {
        this.context = context;
        this.chatRows = chatRows;
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
        TextView lblName = view.findViewById(R.id.chat_row_username);
        lblName.setText(chatRows.get(position).getChatName());
        return view;
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<Chat> getUserRows() {
        return chatRows;
    }
}
