package com.romanimazione.bean;

public class SessionBean {

    private UserBean currentUser;

    private SessionBean() {}

    private static class SingletonHelper {
        private static final SessionBean INSTANCE = new SessionBean();
    }

    public static SessionBean getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public UserBean getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserBean currentUser) {
        this.currentUser = currentUser;
    }
}
