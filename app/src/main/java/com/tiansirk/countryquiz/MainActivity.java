package com.tiansirk.countryquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import timber.log.Timber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.tiansirk.countryquiz.utils.NetworkService;
import com.tiansirk.countryquiz.utils.NetworkUtils;

import com.tiansirk.countryquiz.databinding.ActivityMainBinding;
import com.tiansirk.countryquiz.utils.MyDebugTree;
import com.tiansirk.countryquiz.utils.MyReleaseTree;
import com.tiansirk.countryquiz.utils.MyResultReceiver;

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String USER_PREFERENCES = MainActivity.class.getPackage().getName().concat("_userPrefs");
    public static final String KEY_SAVED_USER_NAME = "userName";
    public static final String EXTRA_KEY_URL = "com.tiansirk.countryquiz.extra_key_url";
    public static final String EXTRA_KEY_RECEIVER = "com.tiansirk.countryquiz.extra_key_receiver";

    private ActivityMainBinding binding;
    public MyResultReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initTimber();
        //TODO: check if this can be done in onStart because of Firestore
        //initFireStore();

    }

    private void initView(){
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setTitle(getString(R.string.app_title));//Sets the title in the action bar
    }
    private void initTimber(){
        if (BuildConfig.DEBUG) {
            Timber.plant(new MyDebugTree());
        } else {
            Timber.plant(new MyReleaseTree());
        }
    }
    private void initFireStore(){
        boolean newUser = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE).contains(KEY_SAVED_USER_NAME);
        if(newUser) {
            saveNewUser();
            //add user to Firestore users collection

            //fetch API: ?JobIntentService->okhttp->Gson->?Repository
            startNetworkService();
            //start game
        } else {
            Timber.d("User already exists");
            //fetch Firestore's users collection for this user
            //set data to fetched data
            //continue game
        }
    }

    public void dnload(View view){
        startNetworkService();
    }
    public void saveUser(View view){

    }

    /**
     * Saves a first-timer into {@link SharedPreferences}. Shows a first time dialog to prompt
     * the user to save a username. If no username is saved it will show no progress will be saved.
     */
    private void saveNewUser(){
        String username = "";
        //TODO: NE SharedPref but Firestore
        //TODO: DialogFragment or AlertDialog?
        //getText from dialog and save as username

        SharedPreferences prefs = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SAVED_USER_NAME, username).apply();
        Timber.d("New user saved");
    }

    /**
     * Starts the network service in which the download, parsing and saving to Firestore is to happen
     */
    private void startNetworkService(){
        Intent serviceIntent = new Intent(this, NetworkService.class);
        serviceIntent.putExtra(EXTRA_KEY_URL, NetworkUtils.URL_ALL_COUNTRY);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

}