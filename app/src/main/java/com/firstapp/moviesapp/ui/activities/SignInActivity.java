package com.firstapp.moviesapp.ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.firstapp.moviesapp.R;
import com.firstapp.moviesapp.utils.Constants;
import com.firstapp.moviesapp.utils.Functions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    AppCompatButton signInBtn;
    TextView googleSignInBtn;
    TextView facebookSignInBtn;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    TextView validationPrompt;
    View loading;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        // get views
        signInBtn = findViewById(R.id.btnSignIn);
        googleSignInBtn = findViewById(R.id.btnGoogleSignIn);
        facebookSignInBtn = findViewById(R.id.btnFacebookSignIn);
        emailInput = findViewById(R.id.ipEmail);
        passwordInput = findViewById(R.id.ipPassword);
        validationPrompt = findViewById(R.id.tvValidationPrompt);
        loading = findViewById(R.id.viewLoading);

        signInBtn.setOnClickListener((view) ->
            ensureInternetIsConnected(() -> {
                if (validateEmailAndPassword())
                    signInWithEmailAndPassword(
                        Objects.requireNonNull(emailInput.getText()).toString(),
                        Objects.requireNonNull(passwordInput.getText()).toString()
                    );
            })
        );
        googleSignInBtn.setOnClickListener((view) ->
            ensureInternetIsConnected(this::signInWithGoogle)
        );
        googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::handleGoogleSignInIntent
        );
    }

    void ensureInternetIsConnected(Runnable func) {
        if (Functions.isInternetConnected(this))
            func.run();
        else Functions.showNoInternetPrompt(this);
    }

    void signInWithGoogle() {
        GoogleSignInOptions options = new GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constants.FIREBASE_SERVER_CLIENT_ID)
            .requestEmail()
            .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, options);
        client.revokeAccess();
        googleSignInLauncher.launch(client.getSignInIntent());
    }

    void handleGoogleSignInIntent(ActivityResult result) {
        loading.setVisibility(View.VISIBLE);
        try {
            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(
                result.getData()
            ).getResult();
            AuthCredential credential = GoogleAuthProvider.getCredential(
                account.getIdToken(), null
            );
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(
                                new Intent(SignInActivity.this, MainActivity.class)
                            );
                            finish();
                        } else {
                            Toast.makeText(
                                SignInActivity.this, "Login failed", Toast.LENGTH_SHORT
                            ).show();
                        }
                        loading.setVisibility(View.GONE);
                    }
                });

        } catch (Exception e) {
            Toast.makeText(
                SignInActivity.this,
                "Login failed",
                Toast.LENGTH_SHORT
            ).show();
            loading.setVisibility(View.GONE);
        }
    }

    void signInWithEmailAndPassword(String email, String password) {
        loading.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        loading.setVisibility(View.GONE);
                        startActivity(
                            new Intent(SignInActivity.this, MainActivity.class)
                        );
                        finish();
                    } else {
                        String exceptionName = task.getException().getClass().getName();
                        if (exceptionName.equals(Constants.INVALID_CREDENTIALS_EXCEPTION))
                            createAccountWithEmailAndPassword(email, password);
                    }
                }
            });
    }

    void createAccountWithEmailAndPassword(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful())
                        signInWithEmailAndPassword(email, password);
                    else if (
                        Objects.requireNonNull(task.getException())
                            .getClass().getName().equals(Constants.ACCOUNT_IS_EXISTING_EXCEPTION)
                    ) {
                        createVibration();
                        loading.setVisibility(View.GONE);
                        validationPrompt.setVisibility(View.VISIBLE);
                        validationPrompt.setText("Invalid password!");
                    }
                }
            });
    }

    boolean validateEmailAndPassword() {
        String email = Objects.requireNonNull(emailInput.getText()).toString();
        String password = Objects.requireNonNull(passwordInput.getText()).toString();
        String promptText = null;
        String emailPattern = "^[0-9A-Za-z.+-_%]+@[a-z0-9A-Z-.]+\\.[a-zA-Z0-9]{2,}$";
        String passwordPattern = "^[a-zA-Z0-9]{6,}$";
        boolean valid = true;
        // validate email
        if (!email.matches(emailPattern)) {
            valid = false;
            promptText = "The email is invalid!";
        }
        // validate password
        if (password.length() < 6) {
            valid = false;
            promptText = "The length of password must be at least 8";
        } else if (!password.matches(passwordPattern)) {
            valid = false;
            promptText = "The password must only contains letters, numbers";
        }
        if (!valid) {
            createVibration();
            validationPrompt.setVisibility(View.VISIBLE);
            validationPrompt.setText(promptText);
        } else {
            validationPrompt.setVisibility(View.INVISIBLE);
        }
        return valid;
    }

    void createVibration() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(
            VibrationEffect.createOneShot(75, VibrationEffect.DEFAULT_AMPLITUDE)
        );
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof TextInputEditText) {
                Rect inputRect = new Rect();
                view.getGlobalVisibleRect(inputRect);
                if (!inputRect.contains(
                    (int) ev.getRawX(), (int) ev.getRawY())
                ) {
                    view.clearFocus();
                    hideSoftInput(view);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    void hideSoftInput(View inputView) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
            .hideSoftInputFromWindow(
                inputView.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN
            );
    }
}