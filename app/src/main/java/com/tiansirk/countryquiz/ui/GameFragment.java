package com.tiansirk.countryquiz.ui;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import timber.log.Timber;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tiansirk.countryquiz.R;
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
public class GameFragment extends Fragment implements FeedbackDialogFragment.FeedbackDialogListener {

    public static final String KEY_IS_CORRECT = "is_correct";

    /** Member vars for views */
    private FragmentGameBinding binding;

    /** Member var for own custom communication listener */
    private GameFragmentListener listener;

    /** The interface for communication */
    public interface GameFragmentListener {
        void onLevelFinished(Level finishedLevel);
    }

    /** Member for FeedbackDialogFragment */
    private DialogFragment mFeedbackDialogFragment;

    /** Member vars for game */
    private User mUser;
    private Level mLevel;
    private List<Question> mQuestions;
    private int questionNumber;
    private int levelPoints;
    private boolean answerSelected;
    private String selectedAnswer;

    // Required empty public constructor
    public GameFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.i("Receiving User and ArrayList<Level> from MainMenuActivity");
        Bundle bundle = getArguments();
        if (bundle != null) {
            mUser = bundle.getParcelable(KEY_USER);
            mLevel = bundle.getParcelable(KEY_NEXT_LEVEL);
            mQuestions = mLevel.getQuestions();
            Timber.i("User: %s. Level: %s", mUser.toString(), mLevel.toString().substring(0,40));
        }
        else showErrorMessage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGameBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupClickListeners();
        startQuestion();
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

    private void startQuestion(){
        answerSelected = false;
        selectedAnswer = "";
        setDataToViews();
        showDataView();

    }

    /** This method will set the data in member fields to the views */
    private void setDataToViews(){
        Timber.i("Start binding answers to answers-TextViews");
        showProgressBar();
        Question currQuestion = mQuestions.get(questionNumber);
        binding.tvGameQuestion.setText(currQuestion.getQuestion());
        randomBinding(currQuestion);
    }

    /** Shuffles the TextViews in a Collection in order to let them randomly bind with data. */
    private void randomBinding(Question question){
        List<TextView> answerTextViews = new ArrayList<>();
        answerTextViews.add(binding.tvGameAnswer1);
        answerTextViews.add(binding.tvGameAnswer2);
        answerTextViews.add(binding.tvGameAnswer3);
        answerTextViews.add(binding.tvGameAnswer4);
        Collections.shuffle(answerTextViews);
        if(question.getQuestion().equals(R.string.question_flag)) {
            setAsImage(answerTextViews, question);
        } else {
            setAsText(answerTextViews, question);
        }
        Timber.i("Finished randomly binding answers to answer-TextViews");
    }

    private void setAsImage(List<TextView> textViews, Question question){
        //todo: Picasso
    }
    private void setAsText(List<TextView> textViews, Question question){
        textViews.get(0).setText(question.getRightAnswer());
        for(int i=0;i<question.getWrongAnswers().size();i++) {
            textViews.get(i+1).setText(question.getWrongAnswers().get(i));
        }
    }

    /** Listens the Submit button. On click evaluates if answer is marked, evaluates the marked answer */
    public void setupClickListeners(){
        Timber.i("Setting up clickListeners");
        binding.tvGameAnswer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(answerSelected) deMarkAnswer(view);
                    else markAnswer(view);
            }
        });
        binding.tvGameAnswer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(answerSelected) deMarkAnswer(view);
                    else markAnswer(view);
            }
        });
        binding.tvGameAnswer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(answerSelected) deMarkAnswer(view);
                    else markAnswer(view);
            }
        });
        binding.tvGameAnswer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(answerSelected) deMarkAnswer(view);
                    else markAnswer(view);
            }
        });
        binding.btnGameSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!answerSelected) {
                    Toast.makeText(getContext(), "Select an answer first!", Toast.LENGTH_SHORT);
                    return;
                }
                evaluateAnswer();
            }
        });
    }

    /** Marks the {@param view} with different color, textcolor and sets its selected status */
    private void markAnswer(View view) {
        answerSelected = true;
        selectedAnswer = ((TextView) view).getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((TextView) view).setTextAppearance(R.style.AnswerTextSelected);

        }
    }

    /** De-marks the {@param view} by restoring its color, textcolor and re-sets its selected status */
    private void deMarkAnswer(View view) {
        answerSelected = false;
        selectedAnswer = ((TextView) view).getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((TextView) view).setTextAppearance(R.style.AnswerText);
        }
    }

    private void evaluateAnswer(){
        Timber.i("Start evaluating answer");
        Question question = mQuestions.get(questionNumber);
        question.setAnswered(true);
        boolean correct = isCorrectAnswer(question);
        if(correct) levelPoints += 1;
        mLevel.setAchievedPoints(levelPoints);
        if(questionNumber == mQuestions.size() - 1) mLevel.setCompleted(true);
        openFeedbackDialog(correct);
    }

    private boolean isCorrectAnswer(Question question){
        if (selectedAnswer.equals(question.getRightAnswer())) return  true;
        else return false;
    }

    private void openFeedbackDialog(boolean correct){
        Timber.i("Starting FeedbackDialog with answer: %s", correct);
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_IS_CORRECT, correct);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        mFeedbackDialogFragment = new FeedbackDialogFragment();
        mFeedbackDialogFragment.setArguments(bundle);
        mFeedbackDialogFragment.show(fragmentManager, "fragment_feedback_dialog");
    }

    @Override
    public void onFinishFeedbackDialog(){
        Timber.i("FeedbackDialog finished. Starting new Level: %s", mLevel.isCompleted());
        if(mLevel.isCompleted()) newLevel();
        else newQuestion();
    }

    private void newQuestion(){
        questionNumber++;
        Timber.i("Starting a new question no. %d", questionNumber);
        startQuestion();
    }

    private void  newLevel(){
        Timber.i("Starting a new level.");
        listener.onLevelFinished(mLevel);
    }

    /** This method will make the Game view visible and hide the error message */
    private void showDataView() {
        // First, make sure the error is invisible
        binding.tvErrorMessageGameFragment.setVisibility(View.INVISIBLE);
        // Then hide loading indicator
        hideProgressBar();
        // Then, make sure the data is visible
        binding.gameCl.setVisibility(View.VISIBLE);
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