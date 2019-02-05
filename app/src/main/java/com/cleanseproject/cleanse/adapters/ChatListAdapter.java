package com.cleanseproject.cleanse.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.dataClasses.User;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> userRows;

    public ChatListAdapter(Context context, ArrayList<String> userRows) {
        this.context = context;
        this.userRows = userRows;
    }

    @Override
    public int getCount() {
        return userRows.size();
    }

    @Override
    public String getItem(int position) {
        return userRows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chat_list, null);
        TextView lblName = view.findViewById(R.id.chat_row_username);
        lblName.setText(userRows.get(position));
        return view;
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<String> getUserRows() {
        return userRows;
    }
}
