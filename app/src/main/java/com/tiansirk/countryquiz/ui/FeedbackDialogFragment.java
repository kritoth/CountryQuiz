package com.tiansirk.countryquiz.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tiansirk.countryquiz.R;
import com.tiansirk.countryquiz.databinding.FragmentFeedbackDialogBinding;

import static com.tiansirk.countryquiz.ui.GameFragment.KEY_IS_CORRECT;
import static com.tiansirk.countryquiz.ui.MainActivity.TAG_GAME_FRAGMENT;

/**
 * A simple {@link Fragment} subclass for providing feedback about the user's submitted answer.
 */
public class FeedbackDialogFragment extends DialogFragment {

    /* The listener interface with a method passing back data result to host fragment */
    public interface FeedbackDialogListener {
        void onFinishFeedbackDialog();
    }

    /* Member var for listener */
    private FeedbackDialogListener listener;
    /* Member var for views */
    private FragmentFeedbackDialogBinding binding;
    /* Member var for data */
    private boolean mCorrect;

    // Required empty public constructor
    public FeedbackDialogFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCorrect = getArguments().getBoolean(KEY_IS_CORRECT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFeedbackDialogBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Return input text back to calling fragment through the implemented listener
        listener = (FeedbackDialogListener) getActivity().getSupportFragmentManager().findFragmentByTag(TAG_GAME_FRAGMENT);
        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFinishFeedbackDialog();
                dismiss();
            }
        });
        if(mCorrect) showRight();
        else showFalse();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void showRight(){
        binding.tvFeedback.setText(R.string.answer_right);
    }

    private void showFalse(){
        binding.tvFeedback.setText(R.string.answer_wrong);
    }
 }