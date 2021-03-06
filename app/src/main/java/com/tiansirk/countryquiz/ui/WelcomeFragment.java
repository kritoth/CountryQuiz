package com.tiansirk.countryquiz.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import timber.log.Timber;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tiansirk.countryquiz.NetworkService;
import com.tiansirk.countryquiz.data.Repository;
import com.tiansirk.countryquiz.databinding.FragmentWelcomeBinding;
import com.tiansirk.countryquiz.model.Level;
import com.tiansirk.countryquiz.model.User;
import com.tiansirk.countryquiz.utils.MyResultReceiver;

import java.util.ArrayList;
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
    public static final String FIELD_NAME_LEVEL = "level";
    private static boolean FLAG_FIRESTORE_USER_SAVE_FINISHED = false;
    private static boolean FLAG_SERVICE_PARSING_LEVELS_FINISHED = false;

    /** Member var for views */
    private FragmentWelcomeBinding binding;
    /** Member var for own custom communication listener */
    private WelcomeFragmentListener listener;
    /** The interface for communication */
    public interface WelcomeFragmentListener {
        void onSetupFinished(User user, List<Level>... levels);
    }
    /** Member var for Auth */
    private FirebaseAuth mAuth;
    /** Member var for repo */
    private Repository mRepository;
    /** Member var for data */
    private User mUser;
    private List<Level> mLevels;
    private List<Level> mLevelsCompleted;
    private List<Level> mLevelsUncompleted;
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
        initFireBase();
        return rootView;
    }

    private void initFireBase(){
        Timber.i("Initializing Firebase Auth");
        mAuth = FirebaseAuth.getInstance();
        Timber.i("Initializing Repository");
        mRepository = new Repository(getActivity(), User.class, COLLECTION_NAME);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null)
        checkUser();
    }

    private void checkUser(){
        Timber.i("Start checking user auth");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){ //not signed-in
            Timber.i("User NOT exists, starting authActivity");
            showEditDialog();
            startNetworkService();
        } else {
            currentUser.reload();
            if (currentUser != null) { //signed-in already
                Timber.i("User exists, getting from DB");
                getUserFromDb(currentUser);
            } else { //not signed-in
                Timber.i("User NOT exists, starting authActivity");
                showEditDialog();
                startNetworkService();
            }
        }
    }

    /** Loads the current state of the game from Firestore. It uses compound queries to get the data
     * from the User document */
    private void getUserFromDb(FirebaseUser user){
        showProgressBar();
        // Name, email address, and profile photo Url
        String name = user.getDisplayName();
        String email = user.getEmail();
        // Check if user's email is verified
        boolean emailVerified = user.isEmailVerified();
        // The user's ID, unique to the Firebase project. Do NOT use this value to authenticate with your backend server, if you have one. Use FirebaseUser.getIdToken() instead.
        final String uid = user.getUid();
        Timber.i("Retrieving User from DB: %s", uid);
        mRepository
                .get(uid).addOnSuccessListener(getActivity(), new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        mUser = (User) o;
                        Timber.i("User retireved: %s", mUser.toString());
                        getCompletedLevelsFromDb();
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // If load fails, display a message to the user.
                        Timber.e(e, "Failed to load user from Firestore.");
                        Toast.makeText(getActivity(), "Loading user data failed.",
                                Toast.LENGTH_SHORT).show();
                        showErrorMessage();
                    }
                });
    }

    /** Loads the current state of the game from Firestore. It uses compound queries to get the data
     * from the User's 'levels' collection  */
    private void getCompletedLevelsFromDb(){
        showProgressBar();
        Timber.i("Retrieving completed Levels from DB for: %s", mUser.getDocumentId());
        mRepository.getCompletedLevels(mUser.getDocumentId(), FIELD_NAME_LEVEL).addOnSuccessListener(getActivity(), new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                if(o == null) {
                    getUncompletedLevelsFromDb();
                    return;
                }
                mLevelsCompleted = new ArrayList<>();
                QuerySnapshot querySnapshot = (QuerySnapshot) o;
                for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                    Level level = documentSnapshot.toObject(Level.class);
                    mLevelsCompleted.add(level);
                    getUncompletedLevelsFromDb();
                }
                Timber.i("Levels retrieved.");

            }
        })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // If loading of completedLevels fails, load uncompletedLevels
                        getUncompletedLevelsFromDb();
//                        Timber.e(e, "Failed to load levels from Firestore.");
//                        Toast.makeText(getActivity(), "Loading levels data failed.",
//                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    /** Loads the current state of the game from Firestore. It uses compound queries to get the data
     * from the User's 'levels' collection  */
    private void getUncompletedLevelsFromDb(){
        mRepository.getUncompletedLevels(mUser.getDocumentId(), FIELD_NAME_LEVEL).addOnSuccessListener(getActivity(), new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                mLevelsUncompleted = new ArrayList<>();
                QuerySnapshot querySnapshot = (QuerySnapshot) o;
                for(QueryDocumentSnapshot documentSnapshot : querySnapshot){
                    Level level = documentSnapshot.toObject(Level.class);
                    mLevelsUncompleted.add(level);

                }
                setupExistingUserSucceeded();
            }
        })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // If load fails, display a message to the user.
                        Timber.e(e, "Failed to load levels from Firestore.");
                        Toast.makeText(getActivity(), "Loading levels data failed.",
                                Toast.LENGTH_SHORT).show();
                        showErrorMessage();
                    }
                });
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
        showProgressBar();
        String name = null;
        String email = null;
        String password = null;
        if(data != null){
            name = data.getStringExtra("name");
            email = data.getStringExtra("email");
            password = data.getStringExtra("password");
        }
        createAccount(name, email, password);
    }

    /** Creates a Firebase Authentication with email and password */
    private void createAccount(final String name, String email, String password) {
        Timber.d("starting account creation with: %s", email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d("createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            createUser(name, user, null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.e(task.getException(), "createUserWithEmail:failure");
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /** Creates a {@link User} and stores it into member var in order to have it saving into database */
    private void createUser(String name, FirebaseUser user, List<Level> levels){
            boolean emailVerified = user.isEmailVerified(); // Check if user's email is verified
            // The user's ID, unique to the Firebase project. Do NOT use this value to authenticate with your backend server, if you have one. Use FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
            if (mUser == null) {
                mUser = new User(uid, name);
            } else {
                mUser.setDocumentId(uid);
                mUser.setUsername(name);
            }
            saveUserToDb();
    }

    /** Saves the user stored in member var into Firestore. */
    private void saveUserToDb(){
        Timber.i("Saving user to DB: %s", mUser.toString());
        mRepository.create(mUser).addOnCompleteListener(getActivity(), new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Timber.i("user saved to Firestore");
                    FLAG_FIRESTORE_USER_SAVE_FINISHED = true;
                    if(FLAG_SERVICE_PARSING_LEVELS_FINISHED) saveLevelsToDb();
                } else {
                    // If saving fails, display a message to the user.
                    Timber.e(task.getException(), "Failed to save into Firestore");
                    Toast.makeText(getActivity(), "Setup failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveLevelsToDb(){
        if(mLevels != null) {
            Timber.i("Saving levels under user in DB: %s", mLevels.size());
            mRepository.saveLevels(mUser.getDocumentId(), mLevels).addOnCompleteListener(getActivity(), new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                            Timber.i("Level saved to Firestore");
                            getCompletedLevelsFromDb();
                    } else {
                            // If saving fails, display a message to the user.
                            Timber.e(task.getException(), "Failed to save Level into Firestore");
                            Toast.makeText(getActivity(), "Setup failed.",
                                    Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
                showProgressBar();
                break;
            case STATUS_SUCCESSFUL:
                result = resultData.getParcelableArrayList("results");
                Timber.d("Question generating resulted");
                mLevels = result;
//                for(Level level : mLevels){
//                    Timber.d("Order in mLevels when is received: %s", level.getLevel());
//                }
                FLAG_SERVICE_PARSING_LEVELS_FINISHED = true;
                if(FLAG_FIRESTORE_USER_SAVE_FINISHED) saveLevelsToDb();
                break;
            case STATUS_FAILED:
                error = resultData.getString("failed");
                showErrorMessage();
                Timber.e("Error in API response: %s", error);
                break;
        }
    }

    /** Called when new user is created and saved to Firestore finished */
    private void setupNewUserSucceeded(){
        Timber.i("Finished retrieving new user's data. Start sending back setup results. Completed Levels size: #%s. Uncompleted Levels size: %s.", mLevelsCompleted.size(), mLevelsUncompleted.size());
        hideProgressBar();
        listener.onSetupFinished(mUser, mLevelsCompleted, mLevelsUncompleted);
    }

    /** Called when data for existing user from Firestore downloading finished */
    private void setupExistingUserSucceeded(){
        Timber.i("Finished retrieving eisting user's data. Start sending back setup results.");
        hideProgressBar();
        listener.onSetupFinished(mUser, mLevelsCompleted, mLevelsUncompleted);
    }

    /** When this fragment is detached from the host, the listeners is set to null, to decouple. */
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /** This method will show the progressbar */
    private void showProgressBar() {
        binding.pbWelcomeFragment.setVisibility(View.VISIBLE);
    }
    /** This method will hide the progressbar */
    private void hideProgressBar() {
        binding.pbWelcomeFragment.setVisibility(View.INVISIBLE);
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

    /** Manual signing in process */
    private void signIn(String email, String password) {
        Timber.d("Signing in with: %s", email);
        showProgressBar();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d("signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            getUserFromDb(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.e(task.getException(), "signInWithEmail:failure");
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressBar();
                    }
                });
    }

    /** Updates the user stored in member var into Firestore. */
    private void updateUserInDb(){
        Timber.i("Updating user in DB: %s", mUser.toString());
        mRepository.update(mUser).addOnCompleteListener(getActivity(), new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Timber.i("User updated in Firestore");
                   setupExistingUserSucceeded();
                } else {
                    // If saving fails, display a message to the user.
                    Timber.e(task.getException(), "Failed to update Firestore");
                    Toast.makeText(getActivity(), "Setup failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}