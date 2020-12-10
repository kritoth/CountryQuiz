package com.tiansirk.countryquiz.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import timber.log.Timber;

import android.os.Bundle;
import android.os.Parcelable;

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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Repository.EntityChangeListener,
        WelcomeFragment.WelcomeFragmentListener, MainMenuFragment.MainMenuFragmentListener, GameFragment.GameFragmentListener {

    public static final String USER_PREFERENCES = MainActivity.class.getPackage().getName().concat("_userPrefs");
    public static final String KEY_SAVED_USER_NAME = "userName";
    public static final String KEY_USER = "user";
    public static final String KEY_LEVELS = "levels";
    public static final String KEY_CURRENT_LEVEL = "current_level";
    public static final String KEY_NEXT_LEVEL = "next_level";
    public static final String TAG_WELCOME_FRAGMENT = "welcome_fragment";
    public static final String TAG_MAIN_MENU_FRAGMENT = "main_menu_fragment";
    public static final String TAG_GAME_FRAGMENT = "game_fragment";
    private static final String COLLECTION_NAME = "users";
    public static final int LAUNCH_SECOND_ACTIVITY = 1;

    private ActivityMainBinding binding;

    /** Member vars for the game */
    private User mUser;
    private List<Level> mLevels;
    private List<Level> mLevelsCompleted;
    private List<Level> mLevelsUncompleted;
    private List<Question> mQuestions;

    /** Member vars for fragments of this activity */
    private WelcomeFragment welcomeFragment;
    private MainMenuFragment mainMenuFragment;
    private GameFragment gameFragment;

    private Repository mRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(getString(R.string.app_title));//Sets the title in the action bar
        initTimber();
        initWelcomeFragment();
    }

    /* Setup Firestore EventListener here in order to spare bandwith usage while the app is not in foreground */
    @Override
    protected void onStart() {
        super.onStart();
        //initFireStore();
    }

    private void initFireStore(){
        Timber.i("Initializing Repository");
        mRepository = new Repository(this, User.class, COLLECTION_NAME);
    }

    private void initWelcomeFragment(){
        Timber.i("Initializing WelcomeFragment");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        welcomeFragment = new WelcomeFragment();
        ft.replace(R.id.container_welcome, welcomeFragment, TAG_WELCOME_FRAGMENT);
        ft.disallowAddToBackStack();
        ft.commit();
    }

    private void initMainMenuFragment(){
        Timber.i("Initializing MainMenuFragment");
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_USER, mUser);
        bundle.putParcelable(KEY_CURRENT_LEVEL, currentLevel());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        mainMenuFragment = new MainMenuFragment();
        mainMenuFragment.setArguments(bundle);
        ft.replace(R.id.container_main_menu, mainMenuFragment, TAG_MAIN_MENU_FRAGMENT);
        ft.addToBackStack(TAG_MAIN_MENU_FRAGMENT);
        ft.commit();

    }

    private void initGameFragment(){
        Timber.i("Initializing GameFragment");
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_USER, mUser);
        bundle.putParcelable(KEY_NEXT_LEVEL, nextLevel());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        gameFragment = new GameFragment();
        gameFragment.setArguments(bundle);
        ft.replace(R.id.container_game, gameFragment, TAG_GAME_FRAGMENT);
        ft.addToBackStack(TAG_GAME_FRAGMENT);
        ft.commit();
    }

    /** This method is defined in the WelcomeFragment to let retrieve data from it */
    @Override
    public void onSetupFinished(User user, List<Level>... levels) {
        Timber.d("User received from WelcomeFragment: %s", user.toString());
        mUser = user;
        mLevelsCompleted = levels[0];
        mLevelsUncompleted = levels[1];
        if(!welcomeFragment.isHidden()) showHideFragment(welcomeFragment);
        initMainMenuFragment();
    }

    /** This method is defined in the MainMenuFragment to let retrieve data from it */
    @Override
    public void onStartGameClicked() {
        if(!mainMenuFragment.isHidden()) showHideFragment(mainMenuFragment);
        initGameFragment();
    }
    /** This method is defined in the MainMenuFragment to let retrieve data from it */
    @Override
    public void onLeaderboardClicked() {
        //todo: start LeaderBoardActivity with fields

    }

    /** This method is defined in the GameFragment to let retrieve data from it */
    @Override
    public void onSubmitClicked() {
        //todo: get the Question: points, answered and Level:
    }

    /** This gets the last level from the completed Levels */
    private Level currentLevel(){
        return mLevelsCompleted.get(mLevelsCompleted.size()-1);
    }

    /** This gets the first level from the not completed Levels */
    private Level nextLevel(){
        return mLevelsUncompleted.get(0);
    }

    /** Shows or hides the {@param fragment} according to its current state. When it's hidden, it still runs,
     * with its every view and feature and not added to backstack either. */
    private void showHideFragment(Fragment fragment){
        Timber.i("Calling method with: %s, it is hidden: %s", fragment.toString(), fragment.isHidden());
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

    /** This ends the WelcomeFragment permanently */
    private void detachWelcomeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.detach(welcomeFragment);
        ft.commit();
    }

    /** Initiates the logging utility called Timber, see: https://github.com/JakeWharton/timber */
    private void initTimber(){
        if (BuildConfig.DEBUG) {
            Timber.plant(new MyDebugTree());
        } else {
            Timber.plant(new MyReleaseTree());
        }
    }
}