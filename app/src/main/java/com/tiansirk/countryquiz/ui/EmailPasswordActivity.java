package com.tiansirk.countryquiz.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.MultiFactorResolver;
import com.tiansirk.countryquiz.BuildConfig;
import com.tiansirk.countryquiz.R;
import com.tiansirk.countryquiz.databinding.ActivityEmailPasswordBinding;
import com.tiansirk.countryquiz.utils.MyDebugTree;
import com.tiansirk.countryquiz.utils.MyReleaseTree;

/** Activity for FireBase Authentication*/
public class EmailPasswordActivity extends AppCompatActivity implements
        View.OnClickListener {

    private ActivityEmailPasswordBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initTimber();

        // Buttons
        binding.emailSignInButton.setOnClickListener(this);
        binding.emailCreateAccountButton.setOnClickListener(this);
        binding.signOutButton.setOnClickListener(this);
        binding.verifyEmailButton.setOnClickListener(this);
        binding.reloadButton.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void createAccount(String email, String password) {
        Timber.d("createAccount:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressBar();
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d("createUserWithEmail:success");
                            mUser = mAuth.getCurrentUser();
                            updateUI(mUser);
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result", mUser);
                            setResult(Activity.RESULT_OK,returnIntent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.e(task.getException(), "createUserWithEmail:failure");
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        hideProgressBar();
                    }
                });
        // [END create_user_with_email]
    }

    private void hideProgressBar() {
        binding.progressBar.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    private void signIn(String email, String password) {
        Timber.d("signIn:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressBar();
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Timber.d("signInWithEmail:success");
                            mUser = mAuth.getCurrentUser();
                            updateUI(mUser);

                        } else {
                            // If sign in fails, display a message to the user.
                            Timber.e(task.getException(), "signInWithEmail:failure");
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);

                            checkForMultiFactorFailure(task.getException());

                        }
                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            binding.status.setText(R.string.auth_failed);
                        }
                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]

    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        // Disable button
        binding.verifyEmailButton.setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        mUser = mAuth.getCurrentUser();
        mUser.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        binding.verifyEmailButton.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Verification email sent to " + mUser.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Timber.e(task.getException(), "sendEmailVerification");
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private void reload() {
        mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateUI(mAuth.getCurrentUser());
                    Toast.makeText(EmailPasswordActivity.this,
                            "Reload successful!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Timber.e(task.getException(), "reload");
                    Toast.makeText(EmailPasswordActivity.this,
                            "Failed to reload user.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = binding.fieldEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            binding.fieldEmail.setError("Required.");
            valid = false;
        } else {
            binding.fieldEmail.setError(null);
        }

        String password = binding.fieldPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            binding.fieldPassword.setError("Required.");
            valid = false;
        } else {
            binding.fieldPassword.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressBar();
        if (user != null) {
            binding.status.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            binding.detail.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            binding.emailPasswordButtons.setVisibility(View.GONE);
            binding.emailPasswordFields.setVisibility(View.GONE);
            binding.signedInButtons.setVisibility(View.VISIBLE);

            if (user.isEmailVerified()) {
                binding.verifyEmailButton.setVisibility(View.GONE);
            } else {
                binding.verifyEmailButton.setVisibility(View.VISIBLE);
            }
            finish();
        } else {
            binding.status.setText(R.string.signed_out);
            binding.detail.setText(null);

            binding.emailPasswordButtons.setVisibility(View.VISIBLE);
            binding.emailPasswordFields.setVisibility(View.VISIBLE);
            binding.signedInButtons.setVisibility(View.GONE);
            finish();
        }
    }

    private void checkForMultiFactorFailure(Exception e) {
        // Multi-factor authentication with SMS is currently only available for
        // Google Cloud Identity Platform projects. For more information:
        // https://cloud.google.com/identity-platform/docs/android/mfa
        if (e instanceof FirebaseAuthMultiFactorException) {
            Timber.e(e, "multiFactorFailure");
            Intent intent = new Intent();
            MultiFactorResolver resolver = ((FirebaseAuthMultiFactorException) e).getResolver();
            intent.putExtra("EXTRA_MFA_RESOLVER", resolver);
            //setResult(MultiFactorActivity.RESULT_NEEDS_MFA_SIGN_IN, intent); //No MFA enabled

        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emailCreateAccountButton) {
            createAccount(binding.fieldEmail.getText().toString(), binding.fieldPassword.getText().toString());
        } else if (i == R.id.emailSignInButton) {
            signIn(binding.fieldEmail.getText().toString(), binding.fieldPassword.getText().toString());
        } else if (i == R.id.signOutButton) {
            signOut();
        } else if (i == R.id.verifyEmailButton) {
            sendEmailVerification();
        } else if (i == R.id.reloadButton) {
            reload();
        }
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