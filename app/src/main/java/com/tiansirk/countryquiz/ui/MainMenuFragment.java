package com.tiansirk.countryquiz.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import timber.log.Timber;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tiansirk.countryquiz.R;
import com.tiansirk.countryquiz.databinding.FragmentMainMenuBinding;
import com.tiansirk.countryquiz.model.Level;
import com.tiansirk.countryquiz.model.Question;
import com.tiansirk.countryquiz.model.User;

import java.util.ArrayList;
import java.util.List;

import static com.tiansirk.countryquiz.ui.MainActivity.KEY_LEVELS;
import static com.tiansirk.countryquiz.ui.MainActivity.KEY_USER;

/** A simple {@link Fragment} subclass.
 * Use the {@link MainMenuFragment.MainMenuFragmentListener} interface for to communicate with this fragment.
 */
public class MainMenuFragment extends Fragment {

    /** Member vars for views */
    private FragmentMainMenuBinding binding;

    /** Member var for own custom communication listener */
    private MainMenuFragmentListener listener;

    /** The interface for communication */
    public interface MainMenuFragmentListener {
        void onStartGameClicked();
    }

    /** Member vars for game */
    private User mUser;
    private ArrayList<Level> mLevels;
    private List<Question> mQuestions;

    // Required empty public constructor
    public MainMenuFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMainMenuBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        Timber.i("Receiving User and ArrayList<Level> from MainActivity");
        Bundle bundle = getArguments();
        mUser = bundle.getParcelable(KEY_USER);
        mLevels = bundle.getParcelableArrayList(KEY_LEVELS);
        Timber.i("User: %s. Levels: %s", mUser.toString(), mLevels.size());
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDataToViews();
        showDataView();
    }

    /** When this fragment is attached to its host activity, ie {@link MainActivity} the listener interface is connected
     * If not then an error exception is thrown to notify the developer. */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainMenuFragmentListener) {
            listener = (MainMenuFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MainMenuFragmentListener");
        }
    }

    /** When this fragment is detached from the host, the listeners is set to null, to decouple. */
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /** This method will set the data in member fields to the views */
    private void setDataToViews(){
        binding.tvName.setText(mUser.getUsername());
        binding.tvLevel.setText(String.format(getString(R.string.level_main_menu_fragment), mUser.getCompletedLevels().size()));
        binding.tvHighScore.setText(String.format(getString(R.string.high_score_main_menu_fragment), mUser.getTotalPoints()));
    }

    /** This method will show the progressbar */
    private void showProgressBar() {
        binding.pbMainMenuFragment.setVisibility(View.VISIBLE);
    }
    /** This method will hide the progressbar */
    private void hideProgressBar() {
        binding.pbMainMenuFragment.setVisibility(View.INVISIBLE);
    }
    /** This method will make the Welcome view visible and hide the error message */
    private void showDataView() {
        // First, make sure the error is invisible
        binding.tvErrorMessageMainMenuFragment.setVisibility(View.INVISIBLE);
        // Then hide loading indicator
        binding.pbMainMenuFragment.setVisibility(View.INVISIBLE);
        // Then, make sure the data is visible
        binding.tvTitle.setVisibility(View.VISIBLE);
        binding.tvName.setVisibility(View.VISIBLE);
        binding.tvHighScore.setVisibility(View.VISIBLE);
    }
    /** This method will make the error message visible and hide the Welcome view */
    private void showErrorMessage() {
        // First, hide the currently visible data
        binding.tvTitle.setVisibility(View.INVISIBLE);
        binding.tvName.setVisibility(View.INVISIBLE);
        binding.tvHighScore.setVisibility(View.INVISIBLE);
        // Then hide loading indicator
        binding.pbMainMenuFragment.setVisibility(View.INVISIBLE);
        // Then, show the error
        binding.tvErrorMessageMainMenuFragment.setVisibility(View.VISIBLE);
    }
}