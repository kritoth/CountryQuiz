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
import android.widget.TextView;

import com.tiansirk.countryquiz.databinding.FragmentGameBinding;
import com.tiansirk.countryquiz.model.Level;
import com.tiansirk.countryquiz.model.Question;
import com.tiansirk.countryquiz.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.tiansirk.countryquiz.ui.MainActivity.KEY_NEXT_LEVEL;
import static com.tiansirk.countryquiz.ui.MainActivity.KEY_USER;

/** A simple {@link Fragment} subclass.
 * Use the {@link GameFragmentListener} interface for communication. */
public class GameFragment extends Fragment {

    /** Member vars for views */
    private FragmentGameBinding binding;

    /** Member var for own custom communication listener */
    private GameFragmentListener listener;

    /** The interface for communication */
    public interface GameFragmentListener {
        void onSubmitClicked();
    }

    /** Member vars for game */
    private User mUser;
    private Level mLevel;
    private List<Question> mQuestions;
    private int questionNumber;

    // Required empty public constructor
    public GameFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionNumber = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGameBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        Timber.i("Receiving User and ArrayList<Level> from MainMenuActivity");
        Bundle bundle = getArguments();
        mUser = bundle.getParcelable(KEY_USER);
        mLevel = bundle.getParcelable(KEY_NEXT_LEVEL);
        mQuestions = mLevel.getQuestions();
        Timber.i("User: %s. Level: %s", mUser.toString(), mLevel.toString());
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
        if (context instanceof GameFragmentListener) {
            listener = (GameFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GameFragmentListener");
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
        showProgressBar();
        Question currQuestion = mQuestions.get(questionNumber);
        binding.tvGameQuestion.setText(currQuestion.getQuestion());
        randomBinding(currQuestion);
    }

    private void randomBinding(Question question){
        List<TextView> answerTextViews = new ArrayList<>();
        answerTextViews.add(binding.tvGameAnswer1);
        answerTextViews.add(binding.tvGameAnswer2);
        answerTextViews.add(binding.tvGameAnswer3);
        answerTextViews.add(binding.tvGameAnswer4);
        Collections.shuffle(answerTextViews);
        answerTextViews.get(0).setText(question.getRightAnswer());
        for(int i=0;i<question.getWrongAnswers().size();i++) {
            answerTextViews.get(i+1).setText(question.getWrongAnswers().get(i));
        }
    }

    /** This method will make the Game view visible and hide the error message */
    private void showDataView() {
        // First, make sure the error is invisible
        binding.tvErrorMessageGameFragment.setVisibility(View.INVISIBLE);
        // Then hide loading indicator
        hideProgressBar();
        // Then, make sure the data is visible
        binding.gameCl.setVisibility(View.INVISIBLE);
    }
    /** This method will make the error message visible and hide the Game view */
    private void showErrorMessage() {
        // First, hide the currently visible data
        binding.gameCl.setVisibility(View.INVISIBLE);
        // Then hide loading indicator
        hideProgressBar();
        // Then, show the error
        binding.tvErrorMessageGameFragment.setVisibility(View.VISIBLE);
    }
    /** This method will show the progressbar */
    private void showProgressBar() {
        binding.pbGameFragment.setVisibility(View.VISIBLE);
    }
    /** This method will hide the progressbar */
    private void hideProgressBar() {
        binding.pbGameFragment.setVisibility(View.INVISIBLE);
    }
}