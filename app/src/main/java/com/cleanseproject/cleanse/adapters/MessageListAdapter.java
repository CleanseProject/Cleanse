package com.cleanseproject.cleanse.adapters;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.dataClasses.Message;
import com.cleanseproject.cleanse.services.ChatManagerService;
import com.cleanseproject.cleanse.services.ImageManagerService;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED_NO_USER = 3;

    private final List<Message> messages;
    private final ChatManagerService chatManagerService;
    private final HashMap<String, Integer> userColors;
    private final ImageManagerService imageManagerService;

    public MessageListAdapter(List<Message> messages, HashMap<String, Integer> userColors) {
        this.userColors = userColors;
        this.messages = messages;
        imageManagerService = new ImageManagerService();
        chatManagerService = new ChatManagerService();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED_NO_USER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received_no_user, parent, false);
            return new ReceivedNoUserMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Message message = messages.get(position);
        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) viewHolder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) viewHolder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED_NO_USER:
                ((ReceivedNoUserMessageHolder) viewHolder).bind(message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.getUser().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            boolean showUser = position == 0 || !messages.get(position - 1).getUser().equals(messages.get(position).getUser());
            return showUser ? VIEW_TYPE_MESSAGE_RECEIVED : VIEW_TYPE_MESSAGE_RECEIVED_NO_USER;
        }
    }


    private class SentMessageHolder extends RecyclerView.ViewHolder {

        final TextView messageText;
        final TextView timeText;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            timeText.setText(formatDate(message.getCreatedAt()));
        }

    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        final TextView messageText;
        final TextView timeText;
        final TextView nameText;
        final ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            nameText = itemView.findViewById(R.id.text_message_name);
            profileImage = itemView.findViewById(R.id.image_message_profile);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            timeText.setText(formatDate(message.getCreatedAt()));
            chatManagerService.getUserName(message.getUser(), nameText::setText);
            setBubbleColor(itemView, messageText, userColors.get(message.getUser()));
            imageManagerService.userImageDownloadUrl(message.getUser(), url ->
                    Glide.with(itemView)
                            .load(url)
                            .placeholder(R.drawable.ic_user)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profileImage)
            );
        }

    }

    private class ReceivedNoUserMessageHolder extends RecyclerView.ViewHolder {

        final TextView messageText;
        final TextView timeText;

        ReceivedNoUserMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            timeText.setText(formatDate(message.getCreatedAt()));
            setBubbleColor(itemView, messageText, userColors.get(message.getUser()));
        }

    }

    private void setBubbleColor(View itemView, TextView messageText, int color) {
        Drawable roundDrawable = itemView.getResources().getDrawable(R.drawable.chat_bubble_received);
        roundDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        messageText.setBackground(roundDrawable);
    }

    private String formatDate(long time) {
        Date date = new Date(time);
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        return dateFormat.format(date);
    }

}
