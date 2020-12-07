package com.e.ms_client;

import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class SettingsActivity extends PreferenceActivity  {
    /*
    public variables that store the server IP
    and the notification button value
     */
    public static final String
            KEY_SERVER_IP = "key_serverIp";
    public static final String
            KEY_NOTIFICATION = "key_notifications";
    /*
    Creates the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}