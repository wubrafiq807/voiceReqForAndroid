package com.istvn.speechrecognitionsystem.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class RegisteredUser {

    private String userName;
    private String password;
    private String confirmedPassword;

    public RegisteredUser(String userName, String password, String confirmedPassword) {
        this.userName = userName;
        this.password = password;
        this.confirmedPassword = confirmedPassword;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmedPassword()
    {
        return confirmedPassword;
    }
}