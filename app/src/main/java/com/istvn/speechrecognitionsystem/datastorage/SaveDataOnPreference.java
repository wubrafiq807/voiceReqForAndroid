package com.istvn.speechrecognitionsystem.datastorage;

import android.content.Context;
import android.content.SharedPreferences;

import com.istvn.speechrecognitionsystem.model.LoginResponse;

import java.sql.Timestamp;

public class SaveDataOnPreference {
    private static final String APP_PREFS_NAME = "SavedData";

    // preference instance
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    // all keys name
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "userName";
    public static final String USER_EMAIL_ADDRESS = "userEmail";
    public static final String USER_PHONE_NUMBER = "userPhoneNumber";
    public static final String LOGIN_TYPE = "loginType";
    public static final String ERROR = "error";
    public static final String MESSAGE = "message";
    public static final String AUTO_RECORD = "switch_preference_record";

    /**
     * initialize instance
     * @param context
     */
    public SaveDataOnPreference(Context context)
    {
        this.appSharedPrefs = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    /**
     * Set Auto Record Status
     * @param value -> Boolean
     */
    public void setAutoRecord(boolean value){
        prefsEditor.putBoolean( AUTO_RECORD, value);
        prefsEditor.commit();
    }

    /**
     * Store User Data
     * @param userData
     */
    public void saveUser(LoginResponse userData){

        if (userData.getResult().getUserId() != null)
            prefsEditor.putString(USER_ID, userData.getResult().getUserId());
        else
            prefsEditor.putString(USER_ID, null);

        if (userData.getResult().getName() != null)
            prefsEditor.putString(USER_NAME, userData.getResult().getName());
        else
            prefsEditor.putString(USER_NAME, null);

        // Sometimes Social account will be created by Phone Number,
        // in that case email address can be null.
        if (userData.getResult().getEmail() != null)
            prefsEditor.putString(USER_EMAIL_ADDRESS, userData.getResult().getEmail());
        else
            prefsEditor.putString(USER_EMAIL_ADDRESS, null);

        if (userData.getResult().getPhone() != null)
            prefsEditor.putString(USER_PHONE_NUMBER, userData.getResult().getPhone());
        else
            prefsEditor.putString(USER_PHONE_NUMBER, null);

        prefsEditor.putInt(LOGIN_TYPE, userData.getResult().getLoginType());
        prefsEditor.putBoolean(ERROR, userData.getError());
        prefsEditor.putString(MESSAGE, userData.getMessage());

        prefsEditor.commit();
    }

    /**
     * Delete User Data From Preferences
     */
    public void logout(){
        prefsEditor.clear();
        prefsEditor.commit();
    }

    /**
     * Get Auto Record Status
     * @return
     */
    public boolean getAutoRecord() {
        return appSharedPrefs.getBoolean(AUTO_RECORD, false);
    }

    /**
     * Get User ID
     * @return
     */
    public String getUserId() {
        return appSharedPrefs.getString(USER_ID, null);
    }

    /**
     * Get User Name
     * @return
     */
    public String getUserName() {
        return appSharedPrefs.getString(USER_NAME, null);
    }

    public String getUserEmailAddress() {
        return appSharedPrefs.getString(USER_EMAIL_ADDRESS, null);
    }

    public String getUserPhoneNumber() {
        return appSharedPrefs.getString(USER_PHONE_NUMBER, null);
    }

    public String getLoginType() {
        return appSharedPrefs.getString(LOGIN_TYPE, null);
    }

    public String getERROR() {
        return appSharedPrefs.getString(ERROR, null);
    }

    public String getMEESSAGE() {
        return appSharedPrefs.getString(MESSAGE, null);
    }
}
