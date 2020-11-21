package com.tiansirk.countryquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import timber.log.Timber;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import static android.app.DownloadManager.STATUS_FAILED;
import static android.app.DownloadManager.STATUS_RUNNING;
import static android.app.DownloadManager.STATUS_SUCCESSFUL;

import com.tiansirk.countryquiz.data.Repository;
import com.tiansirk.countryquiz.databinding.ActivityMainBinding;
import com.tiansirk.countryquiz.model.Country;
import com.tiansirk.countryquiz.model.Level;
import com.tiansirk.countryquiz.model.Question;
import com.tiansirk.countryquiz.model.User;
import com.tiansirk.countryquiz.ui.EditNameDialogFragment;
import com.tiansirk.countryquiz.ui.WelcomeFragment;
import com.tiansirk.countryquiz.utils.CountryUtils;
import com.tiansirk.countryquiz.utils.GenerateQuestionUtils;
import com.tiansirk.countryquiz.utils.MyDebugTree;
import com.tiansirk.countryquiz.utils.MyReleaseTree;
import com.tiansirk.countryquiz.utils.MyResultReceiver;

import java.util.List;

public class MainActivity extends AppCompatActivity implements WelcomeFragment.WelcomeFragmentListener {

    public static final String USER_PREFERENCES = MainActivity.class.getPackage().getName().concat("_userPrefs");
    public static final String KEY_SAVED_USER_NAME = "userName";
    public static final String TAG_WELCOME_FRAGMENT = "welcome_fragment";


    private ActivityMainBinding binding;

    /** Member vars for user */
    private User mUser;
    private Level mLevel;
    private List<Question> mQuestions;

    /** Member vars for fragments of this activity */
    private WelcomeFragment welcomeFragment;


    private Repository mRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityMainBinding.inflate(getLayoutInflater()).getRoot());
        setTitle(getString(R.string.app_title));//Sets the title in the action bar

        initTimber();
        initWelcomeFragment();
    }

    /* Setup Firestore EventListener here in order to spare bandwith usage while the app is not in foreground */
    @Override
    protected void onStart() {
        super.onStart();
        //setupEventListener();//TODO: Kell külön initFireStore() az onCreate-ben?
    }

    /** Initiates the logging utility called Timber, see: https://github.com/JakeWharton/timber */
    private void initTimber(){
        if (BuildConfig.DEBUG) {
            Timber.plant(new MyDebugTree());
        } else {
            Timber.plant(new MyReleaseTree());
        }
    }

    private void initWelcomeFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        welcomeFragment = new WelcomeFragment();
        ft.add(R.id.container_welcome, welcomeFragment, TAG_WELCOME_FRAGMENT);
        ft.commit();
    }


    @Override
    public void onSetupFinished(List<Question> questions) {

    }
}