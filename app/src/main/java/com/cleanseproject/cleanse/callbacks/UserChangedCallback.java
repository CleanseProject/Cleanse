package com.cleanseproject.cleanse.callbacks;

import com.cleanseproject.cleanse.dataClasses.User;

public interface UserChangedCallback {

    void onUserLoad(User user);

    void userRemoved(String userId);

}
