package com.cleanseproject.cleanse.callbacks;

import com.cleanseproject.cleanse.dataClasses.Chat;

import java.util.ArrayList;

public interface ChatListLoadCallback {

    void onCallBack(ArrayList<Chat> chats);

}
