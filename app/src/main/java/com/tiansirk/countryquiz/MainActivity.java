package com.tiansirk.countryquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String USER_PREFERENCES = MainActivity.class.getPackage().getName().concat("_userPrefs");
    public static final String KEY_SAVED_USER_NAME = "userName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean newUser = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE).contains(KEY_SAVED_USER_NAME);
        if(newUser) {
            saveNewUser();
            //add user to Firestore users collection
            //fetch API
            //start game
        } else {
            //fetch Firestore's users collection for this user
            //set data to fetched data
            //continue game
        }

    }

    /**
     * Saves a first-timer into {@link SharedPreferences}. Shows a first time dialog to prompt
     * the user to save a username. If no username is saved it will show no progress will be saved.
     */
    private void saveNewUser(){
        String username = "";
        //DialogFragment or AlertDialog?
        //getText from dialog and save as username

        SharedPreferences prefs = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SAVED_USER_NAME, username).apply();
    }
}