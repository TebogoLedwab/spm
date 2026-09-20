package com.main.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.main.R;
import com.main.pages.Pantry;

import java.util.Objects;

import static com.main.helper.SnackBarHelper.snackbar;

public class SignIn extends AppCompatActivity {

    private static final String TAG = "SIGN_IN_ACTIVITY";
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        firebaseAuth = FirebaseAuth.getInstance();

        EditText emailInput = findViewById(R.id.email_form);
        EditText passwordInput = findViewById(R.id.password_form);
        Button signUp = findViewById(R.id.signup);
        Button signIn = findViewById(R.id.sign_in);

        signUp.setOnClickListener(v -> {
            startActivity(new Intent(SignIn.this, SignUp.class));
        });

        signIn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();

            if (isValidInputs(emailInput, passwordInput, email, password)) {
                signInUser(email, password);
            }
        });
    }

    private void signInUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                snackbar(this, "Welcome back!", true);
                                navigateToPantry();
                                finish();
                            }, 2000);
                        }
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        snackbar(this, Objects.requireNonNull(task.getException()).getMessage(), false);
                    }
                });
    }

    private void navigateToPantry() {
        Intent intent = new Intent(SignIn.this, Pantry.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Validates form inputs and updates UI errors dynamically.
     * Returns true if all fields are valid, false otherwise.
     */
    private boolean isValidInputs(EditText emailInput, EditText passwordInput, String email, String password) {
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            return false;
        }
        if (email.contains(" ") || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Provide a valid email address");
            return false;
        }
        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            return false;
        }
        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return false;
        }
        return true;
    }
}
