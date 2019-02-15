package com.cleanseproject.cleanse.callbacks;

import com.cleanseproject.cleanse.dataClasses.Message;

import java.util.ArrayList;

public interface MessagesLoadCallback {

    void onMessagesLoaded(ArrayList<Message> messages);

}
