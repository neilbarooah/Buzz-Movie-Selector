package com.thundercats50.moviereviewer.models;

import android.app.Application;

import java.util.HashMap;

/**
 * @author Neil Barooah
 */
public class UserManager extends Application {

    public static User currentMember = new User("","","","","");
    //so app does not crash if DB connection unavailable

    /**
     * adds a member to the backing hashMap
     * @param member the ember to add
     */
    public void setCurrentMember(User member) {
        currentMember = member;
    }

    /**
     * gets a member based on the email
     * @return the user associated with the email
     */
    public User getCurrentMember() {
        return currentMember;
    }

}
