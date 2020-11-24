package com.tiansirk.countryquiz.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import timber.log.Timber;

import android.os.Bundle;

import com.google.firebase.firestore.DocumentSnapshot;
import com.tiansirk.countryquiz.BuildConfig;
import com.tiansirk.countryquiz.R;
import com.tiansirk.countryquiz.data.Repository;
import com.tiansirk.countryquiz.databinding.ActivityMainBinding;
import com.tiansirk.countryquiz.model.Level;
import com.tiansirk.countryquiz.model.Question;
import com.tiansirk.countryquiz.model.User;
import com.tiansirk.countryquiz.utils.MyDebugTree;
import com.tiansirk.countryquiz.utils.MyReleaseTree;

import java.util.List;

public class MainActivity extends AppCompatActivity implements Repository.EntityChangeListener,
        WelcomeFragment.WelcomeFragmentListener, MainMenuFragment.MainMenuFragmentListener {

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
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(getString(R.string.app_title));//Sets the title in the action bar

        initTimber();
        initWelcomeFragment();
        initMainMenuFragment();
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
        ft.replace(R.id.container_welcome, welcomeFragment, TAG_WELCOME_FRAGMENT);
        ft.disallowAddToBackStack();
        ft.commit();
    }

    private void initMainMenuFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        mainMenuFragment = new MainMenuFragment();
        ft.add(R.id.container_main_menu, mainMenuFragment, TAG_MAIN_MENU_FRAGMENT);
        ft.addToBackStack(TAG_MAIN_MENU_FRAGMENT);
        ft.commit();
        showHideFragment(mainMenuFragment);
    }

    /** This method is defined in the WelcomeFragment to let retrieve data from it */
    @Override
    public void onSetupFinished(User user) {
        Timber.d("User received from WelcomeFragment");
        mUser = user;
        detachWelcomeFragment();
        showHideFragment(mainMenuFragment);
    }

    /** This method is defined in the MainMenuFragment to let retrieve data from it */
    @Override
    public void onStartGameClicked() {

    }

    /** This ends the WelcomeFragment permanently */
    private void detachWelcomeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.detach(welcomeFragment);
        ft.commit();
    }

    /** Shows or hides the {@param fragment} according to its current state */
    private void showHideFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if(fragment.isHidden()) ft.show(fragment);
        else ft.hide(fragment);
        ft.commit();
    }

    /** This method is defined in the Repository to let retrieve data from it */
    @Override
    public void onEvent(DocumentSnapshot documentSnapshot) {

    }
}