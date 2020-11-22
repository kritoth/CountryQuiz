package com.tiansirk.countryquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import timber.log.Timber;

import android.os.Bundle;

import com.tiansirk.countryquiz.data.Repository;
import com.tiansirk.countryquiz.databinding.ActivityMainBinding;
import com.tiansirk.countryquiz.model.Level;
import com.tiansirk.countryquiz.model.Question;
import com.tiansirk.countryquiz.model.User;
import com.tiansirk.countryquiz.ui.MainMenuFragment;
import com.tiansirk.countryquiz.ui.WelcomeFragment;
import com.tiansirk.countryquiz.utils.MyDebugTree;
import com.tiansirk.countryquiz.utils.MyReleaseTree;

import java.util.List;

public class MainActivity extends AppCompatActivity implements WelcomeFragment.WelcomeFragmentListener, MainMenuFragment.MainMenuFragmentListener {

    public static final String USER_PREFERENCES = MainActivity.class.getPackage().getName().concat("_userPrefs");
    public static final String KEY_SAVED_USER_NAME = "userName";
    public static final String TAG_WELCOME_FRAGMENT = "welcome_fragment";
    public static final String TAG_MAIN_MENU_FRAGMENT = "main_menu_fragment";


    private ActivityMainBinding binding;

    /** Member vars for the game */
    private User mUser;
    private List<Level> mLevels;
    private List<Question> mQuestions;

    /** Member vars for fragments of this activity */
    private WelcomeFragment welcomeFragment;
    private MainMenuFragment mainMenuFragment;

    private Repository mRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityMainBinding.inflate(getLayoutInflater()).getRoot());
        setTitle(getString(R.string.app_title));//Sets the title in the action bar

        initTimber();
        initWelcomeFragment();
        initMainMenuFragment();//TODO Csak ha végzett a WelcomFragment
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

    private void initMainMenuFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        mainMenuFragment = new MainMenuFragment();
        ft.add(R.id.container_main_menu, mainMenuFragment, TAG_MAIN_MENU_FRAGMENT);
    }

    /** This method is defined in the WelcomeFragment to let retrieve data from it */
    @Override
    public void onSetupFinished(List<Level> levels) {
        mLevels = levels;
    }

    /** This method is defined in the MainMenuFragment to let retrieve data from it */
    @Override
    public void userExists() {

    }
}