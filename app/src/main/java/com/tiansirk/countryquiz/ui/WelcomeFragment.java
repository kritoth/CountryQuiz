package com.tiansirk.countryquiz.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import timber.log.Timber;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tiansirk.countryquiz.NetworkService;
import com.tiansirk.countryquiz.data.Repository;
import com.tiansirk.countryquiz.databinding.FragmentWelcomeBinding;
import com.tiansirk.countryquiz.model.Level;
import com.tiansirk.countryquiz.model.User;
import com.tiansirk.countryquiz.utils.MyResultReceiver;

import java.util.List;

import static android.app.DownloadManager.STATUS_FAILED;
import static android.app.DownloadManager.STATUS_RUNNING;
import static android.app.DownloadManager.STATUS_SUCCESSFUL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WelcomeFragmentListener} interface for to communicate with this fragment.
 */
public class WelcomeFragment extends Fragment implements MyResultReceiver.Receiver, EditNameDialogFragment.EditNameDialogListener {

    public static final String EXTRA_KEY_URL = "com.tiansirk.countryquiz.extra_key_url";
    public static final String EXTRA_KEY_RECEIVER = "com.tiansirk.countryquiz.extra_key_receiver";
    private static final String COLLECTION_NAME = "users";

    /** Member var for views */
    private FragmentWelcomeBinding binding;
    /** Member var for own custom communication listener */
    private WelcomeFragmentListener listener;
    /** The interface for communication */
    public interface WelcomeFragmentListener {
        void onSetupFinished(User user);
    }
    /** Member var for repo */
    private Repository mRepository;
    /** Member var for user */
    private User mUser;
    /** Member var for communicating with networking service */
    public MyResultReceiver mReceiver;

    // Required empty public constructor
    public WelcomeFragment() {
    }

    /**
     * When this fragment is attached to its host activity, ie {@link MainActivity} the listener interface is connected
     * If not then an error exception is thrown to notify the developer.
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof WelcomeFragmentListener) {
            listener = (WelcomeFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement WelcomeFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        initFireStore();
        checkUser();
        return rootView;
    }

    private void initFireStore(){
        Timber.i("Initializing Repository");
        mRepository = new Repository(getActivity(), User.class, COLLECTION_NAME);
    }

    private void checkUser(){
        Timber.i("Checking user auth");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Timber.i("User exists, getting from DB");
            getUserFromDb(user);
        } else {
            Timber.i("User NOT exists, starting authActivity");
            //TODO: hogy igy párhuzamosan menjen e kettő
            startNetworkService();
            showEditDialog();
        }
    }

    /** This dialog is to prompt the user to register and save its name if she uses first time the app.
     * If she is a recurring user this won't be shown. */
    private void showEditDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        EditNameDialogFragment editNameDialogFragment = EditNameDialogFragment.newInstance("Welcome Player!");
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    /** This method is invoked in when the listener is triggered in the {@link EditNameDialogFragment},
     * ie. when the user pushes the save button after entering their name.
     * This method has access to the data result passed by the {@link com.tiansirk.countryquiz.ui.EditNameDialogFragment.EditNameDialogListener} */
    @Override
    public void onFinishEditDialog(Intent data) {
        //TODO: change layout: update with name, show loading
        saveUserToDb(data);
    }

    /** Saves a first-timer into Firestore. Shows a first time dialog to prompt
     * the user to save a username. If no username is saved it will show no progress will be saved. */
    private void saveUserToDb(Intent data){
        String userName = data.getStringExtra("name");
        FirebaseUser user = data.getParcelableExtra("result");
        String name = user.getDisplayName(); // Name, email address, and profile photo Url
        boolean emailVerified = user.isEmailVerified(); // Check if user's email is verified
        // The user's ID, unique to the Firebase project. Do NOT use this value to authenticate with your backend server,
        // if you have one. Use FirebaseUser.getIdToken() instead.
        String uid = user.getUid();

        mUser = new User(uid, userName);
        Timber.i("Saving user to DB: " + mUser.toString());
        mRepository.create(mUser);

        //TODO: listener vagy flag jelez setupSucceeded() -nek ha ez megvan
    }

    /** Loads the current state of the game from Firestore. It uses compound queries to get the data
     * from the User document */
    private void getUserFromDb(FirebaseUser user){
        // Name, email address, and profile photo Url
        String name = user.getDisplayName();
        String email = user.getEmail();
        // Check if user's email is verified
        boolean emailVerified = user.isEmailVerified();
        // The user's ID, unique to the Firebase project. Do NOT use this value to authenticate with your backend server,
        // if you have one. Use FirebaseUser.getIdToken() instead.
        String uid = user.getUid();
        Timber.i("Retrieving User from DB: %s", uid);
        mRepository.get(uid).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                mUser = (User) o;
                Timber.i("User retireved: %s", mUser.toString());
                //TODO: listener vagy flag jelez setupSucceeded() -nek ha ez megvan
            }
        });

    }

    /** Starts the network service in which the download, parsing, question generating and levels creating is to happen */
    private void startNetworkService() {
        mReceiver = new MyResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent serviceIntent = new Intent(Intent.ACTION_SYNC, null, getContext(), NetworkService.class);
        serviceIntent.putExtra(EXTRA_KEY_RECEIVER, mReceiver);
        serviceIntent.putExtra(EXTRA_KEY_URL, NetworkService.URL_ALL_COUNTRY);
        Timber.d("Calling service for OkHttp");
        ContextCompat.startForegroundService(getContext(), serviceIntent);
    }

    /**
     * Receives the result from IntentService and acts accordingly if it's running, OK or Failed
     * @param resultCode is the code of the status of result
     * @param resultData is the data came with the result
     */
    public void onReceiveResult(int resultCode, Bundle resultData) {
        List<Level> result;
        String error;
        switch (resultCode) {
            case STATUS_RUNNING:
                binding.pbWelcomeFragment.setVisibility(View.VISIBLE);
                break;
            case STATUS_SUCCESSFUL:
                result = resultData.getParcelableArrayList("results");
                Timber.d("Question generating resulted");
                //TODO: listener vagy flag jelez setupSucceeded() -nek ha ez megvan

                binding.pbWelcomeFragment.setVisibility(View.INVISIBLE);
                break;
            case STATUS_FAILED:
                error = resultData.getString("failed");
                showErrorMessage();
                Timber.e("Error in API response: %s", error);
                break;
        }
    }

    //TODO: Called when BOTH onReceiveResult and saveUserToDb/getUserFromDb finished
    private void setupSucceeded(){
        listener.onSetupFinished(mUser);
    }

    /** When this fragment is detached from the host, the listeners is set to null, to decouple. */
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /** This method will make the Welcome view visible and hide the error message */
    private void showDataView() {
        // First, make sure the error is invisible
        binding.tvErrorMessageWelcomeFragment.setVisibility(View.INVISIBLE);
        // Then hide loading indicator
        binding.pbWelcomeFragment.setVisibility(View.INVISIBLE);
        // Then, make sure the movie is visible
        binding.tvWelcomeFragment.setVisibility(View.VISIBLE);
    }
    /** This method will make the error message visible and hide the Welcome view */
    private void showErrorMessage() {
        // First, hide the currently visible data
        binding.tvWelcomeFragment.setVisibility(View.INVISIBLE);
        // Then hide loading indicator
        binding.pbWelcomeFragment.setVisibility(View.INVISIBLE);
        // Then, show the error
        binding.tvErrorMessageWelcomeFragment.setVisibility(View.VISIBLE);
    }














    //TODO: Ez helyett az showEditDialog
    //    private void startAuthActivity(){
    //        Timber.i(" startAuthActivity is called");
    //        Intent activityIntent = new Intent(this, EmailPasswordActivity.class);
    //        startActivityForResult(activityIntent, LAUNCH_SECOND_ACTIVITY);
    //    }

    //TODO: Ez helyett az onFinishEditDialog
    //    @Override
    //    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    //        super.onActivityResult(requestCode, resultCode, data);
    //
    //        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
    //            if(resultCode == Activity.RESULT_OK){
    //                FirebaseUser user = data.getParcelableExtra("result");
    //                saveUserToDb(user);
    //            }
    //            if (resultCode == Activity.RESULT_CANCELED) {
    //                //Write your code if there's no result
    //            }
    //        }
    //    }

}