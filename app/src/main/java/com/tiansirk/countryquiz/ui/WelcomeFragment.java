package com.tiansirk.countryquiz.ui;

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

import com.tiansirk.countryquiz.NetworkService;
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

    /** Member var for views */
    private FragmentWelcomeBinding binding;
    /** Member var for own custom communication listener */
    private WelcomeFragmentListener listener;
    /** The interface for communication */
    public interface WelcomeFragmentListener {
        void onSetupFinished(User user);
    }
    /** Member vars for user */
    private boolean newUser = true;//TODO: Ez csak Testing miatt, helyette check Firestore
    private String userName;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        //TODO: set newuser with data received from MainActivity
        if(newUser) startNetworkService();
        else initFireStore();
        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (newUser) showEditDialog();
    }

    /** When this fragment is detached from the host, the listeners is set to null, to decouple. */
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    private void initFireStore() {
        //mRepository = new Repository(getActivity());
    }

    /**
     * This dialog is to prompt the user to save its name if she is first time using the app.
     * If she is a recurring user this won't be shown.
     */
    private void showEditDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        EditNameDialogFragment editNameDialogFragment = EditNameDialogFragment.newInstance("Welcome Player!");
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    /**
     * This method is invoked in this activity when the listener is triggered in the {@link EditNameDialogFragment},
     * ie. when the user pushes the save button after entering their name.
     * This method has access to the data result passed by the {@link com.tiansirk.countryquiz.ui.EditNameDialogFragment.EditNameDialogListener}
     * to this activity here, this data should be the user's name as a string.
     */
    @Override
    public void onFinishEditDialog(String inputText) {
        userName = inputText.trim();
        //saveNewUser(inputText);
    }

    /**
     * Saves a first-timer into Firestore. Shows a first time dialog to prompt
     * the user to save a username. If no username is saved it will show no progress will be saved.
     */
    private void saveNewUser(String name) {
        //TODO: Save into Firestore
        /*SharedPreferences prefs = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SAVED_USER_NAME, username).apply();*/
        Timber.d("New user saved");
    }

    /**
     * Loads the current state of the game from Firestore. It uses compound queries to get the data
     * from the User document
     */
    private void loadGame() {
        //mRepository.loadGame();
    }

    /**
     * Starts the network service in which the download, parsing and saving to Firestore is to happen
     */
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
                listener.onSetupFinished(new User(userName, 0, null, result));
                binding.pbWelcomeFragment.setVisibility(View.INVISIBLE);
                break;
            case STATUS_FAILED:
                error = resultData.getString("failed");
                showErrorMessage();
                Timber.e("Error in API response: %s", error);
                break;
        }
    }


    /**
     * This method will make the Welcome view visible and hide the error message
     */
    private void showDataView() {
        // First, make sure the error is invisible
        binding.tvErrorMessageWelcomeFragment.setVisibility(View.INVISIBLE);
        // Then hide loading indicator
        binding.pbWelcomeFragment.setVisibility(View.INVISIBLE);
        // Then, make sure the movie is visible
        binding.tvWelcomeFragment.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the Welcome view
     */
    private void showErrorMessage() {
        // First, hide the currently visible data
        binding.tvWelcomeFragment.setVisibility(View.INVISIBLE);
        // Then hide loading indicator
        binding.pbWelcomeFragment.setVisibility(View.INVISIBLE);
        // Then, show the error
        binding.tvErrorMessageWelcomeFragment.setVisibility(View.VISIBLE);
    }

    /**
     * TODO: change this to Firebase Authentication
     * Setup EventListener to sync user info
     */
    private void setupEventListener() {

    }

}