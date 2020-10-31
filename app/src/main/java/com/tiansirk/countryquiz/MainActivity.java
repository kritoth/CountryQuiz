package com.tiansirk.countryquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import timber.log.Timber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import static android.app.DownloadManager.STATUS_FAILED;
import static android.app.DownloadManager.STATUS_RUNNING;
import static android.app.DownloadManager.STATUS_SUCCESSFUL;

import com.google.firebase.firestore.FirebaseFirestore;
import com.tiansirk.countryquiz.data.Repository;
import com.tiansirk.countryquiz.databinding.ActivityMainBinding;
import com.tiansirk.countryquiz.utils.MyDebugTree;
import com.tiansirk.countryquiz.utils.MyReleaseTree;
import com.tiansirk.countryquiz.utils.MyResultReceiver;

public class MainActivity extends AppCompatActivity implements MyResultReceiver.Receiver {

    public static final String USER_PREFERENCES = MainActivity.class.getPackage().getName().concat("_userPrefs");
    public static final String KEY_SAVED_USER_NAME = "userName";
    public static final String EXTRA_KEY_URL = "com.tiansirk.countryquiz.extra_key_url";
    public static final String EXTRA_KEY_RECEIVER = "com.tiansirk.countryquiz.extra_key_receiver";

    private ActivityMainBinding binding;
    public MyResultReceiver mReceiver;
    private Repository mRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initTimber();
        initFireStore();

/*        //boolean newUser = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE).contains(KEY_SAVED_USER_NAME);
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
        }*/

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
        FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
        mRepository = new Repository(this, firestoreDb);
    }

    /* Setup Firestore EventListener here in order to spare bandwith usage while the app is not in foreground */
    @Override
    protected void onStart() {
        super.onStart();
        setupEventListener();
    }

    /** TODO: change this to Firebase Authentication
     * Setup EventListener to sync user info
     */
    private void setupEventListener(){

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
        mReceiver = new MyResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent serviceIntent = new Intent(Intent.ACTION_SYNC, null, this, NetworkService.class);
        serviceIntent.putExtra(EXTRA_KEY_RECEIVER, mReceiver);
        serviceIntent.putExtra(EXTRA_KEY_URL, NetworkService.URL_ALL_COUNTRY);
        Timber.d("Calling service for OkHttp");
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    /**
     * Receives the result from IntentService
     * @param resultCode
     * @param resultData
     */
    public void onReceiveResult(int resultCode, Bundle resultData) {
        String result = "";
        switch (resultCode) {
            case STATUS_RUNNING:
                //todo: show progress
                result = "in progress";
                binding.textView.setText(result);
                break;
            case STATUS_SUCCESSFUL:
                result = resultData.getString("results");
                //todo: do something interesting
                binding.textView.setText(result);

                Timber.d("API response: %s", result.substring(0, 33));
                //todo: hide progress
                break;
            case STATUS_FAILED:
                //todo: handle the error;
                result = resultData.getString("failed");
                binding.textView.setText(result);

                Timber.d("Error in API response: %s", result);
                break;
        }
    }
}