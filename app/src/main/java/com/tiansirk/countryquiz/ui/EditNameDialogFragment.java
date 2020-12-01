package com.tiansirk.countryquiz.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tiansirk.countryquiz.R;
import com.tiansirk.countryquiz.databinding.FragmentEditNameDialogBinding;

import static com.tiansirk.countryquiz.ui.MainActivity.TAG_WELCOME_FRAGMENT;

/**
 * A simple {@link Fragment} subclass to prompt the user with creating a profile.
 * Use the {@link EditNameDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditNameDialogFragment extends DialogFragment implements TextView.OnEditorActionListener {

    /* The listener interface with a method passing back data result to host fragment */
    public interface EditNameDialogListener {
        void onFinishEditDialog(Intent inputData);
    }
    /* Member var for views */
    FragmentEditNameDialogBinding binding;
    /* Member var data*/
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    // Empty constructor is required for DialogFragment, use `newInstance` instead as shown below
    public EditNameDialogFragment() {
    }

    /**
     * Use this factory method to create a new instance of, this fragment using the provided parameters.
     * @param title Title of the fragment
     * @return A new instance of fragment EditNameDialogFragment.
     */
    public static EditNameDialogFragment newInstance(String title) {
        EditNameDialogFragment frag = new EditNameDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditNameDialogBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        binding.txtYourName.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        // Setup a callback when the "Done" button is pressed on keyboard
        binding.txtYourName.setOnEditorActionListener(this);
    }

    /** Fires whenever the textfield has an action performed. In this case, when the "Done" button is pressed
     * REQUIRES a 'soft keyboard' (virtual keyboard) */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text back to activity through the implemented listener
            EditNameDialogListener listener = (EditNameDialogListener) getActivity().getSupportFragmentManager().findFragmentByTag(TAG_WELCOME_FRAGMENT);
            Intent inputData = new Intent();
            inputData.putExtra("name", binding.txtYourName.getText().toString().trim());
            inputData.putExtra("result", mUser);
            listener.onFinishEditDialog(inputData);
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;
    }
}