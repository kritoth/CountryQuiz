package com.tiansirk.countryquiz.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tiansirk.countryquiz.R;
import com.tiansirk.countryquiz.databinding.FragmentMainMenuBinding;
import com.tiansirk.countryquiz.model.Level;
import com.tiansirk.countryquiz.model.Question;
import com.tiansirk.countryquiz.model.User;

import java.util.List;

/** A simple {@link Fragment} subclass.
 * Use the {@link MainMenuFragment.MainMenuFragmentListener} interface for to communicate with this fragment.
 */
public class MainMenuFragment extends Fragment {

    /**
     * Member vars for views
     */
    private FragmentMainMenuBinding binding;

    /**
     * Member var for own custom communication listener
     */
    private MainMenuFragmentListener listener;

    /**
     * The interface for communication
     */
    public interface MainMenuFragmentListener {
        void onStartGameClicked();
    }

    /**
     * Member vars for game
     */
    private User mUser;
    private Level mLevel;
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

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * When this fragment is attached to its host activity, ie {@link com.tiansirk.countryquiz.MainActivity} the listener interface is connected
     * If not then an error exception is thrown to notify the developer.
     *
     * @param context
     */
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

    /**
     * When this fragment is detached from the host, the listeners is set to null, to decouple.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}