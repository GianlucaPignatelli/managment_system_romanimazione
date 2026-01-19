package com.romanimazione.bean;

public class SessionBean {

    private static SessionBean instance;
    private UserBean currentUser;

    private SessionBean() {}

    public static SessionBean getInstance() {
        if (instance == null) {
            instance = new SessionBean();
        }
        return instance;
    }

    public UserBean getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserBean currentUser) {
        this.currentUser = currentUser;
    }
}
