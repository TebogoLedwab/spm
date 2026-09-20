package com.main.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.R;

import java.util.HashMap;
import java.util.Map;

import static com.main.helper.SnackBarHelper.snackbar;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Initialize UI Elements
        Button signIn = findViewById(R.id.sign_in);
        Button signUp = findViewById(R.id.signup);

        EditText fullNameInput = findViewById(R.id.full_name_form);
        EditText emailInput = findViewById(R.id.email_form);
        EditText passwordInput = findViewById(R.id.password_form);
        EditText passwordConfirmationInput = findViewById(R.id.confirm_password_form);

        // Redirect to SignIn Activity
        signIn.setOnClickListener(v -> startActivity(new Intent(SignUp.this, SignIn.class)));

        // Handle Sign Up Logic
        signUp.setOnClickListener(v -> {
            String fullName = fullNameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            String passwordConfirmation = passwordConfirmationInput.getText().toString();

            if (isValidInputs(fullNameInput, emailInput, passwordInput, passwordConfirmationInput,
                    fullName, email, password, passwordConfirmation)) {
                registerNewUser(email, password, fullName);
            }
        });
    }

    private void registerNewUser(String email, String password, String displayName) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            saveUserDataToFirestore(user.getUid(), email, displayName);
                        }
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        String error = task.getException() != null ? task.getException().getMessage() : "Authentication Failed";
                        snackbar(this, error, false);
                    }
                });
    }

    private void saveUserDataToFirestore(String uid, String email, String fullName) {
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("email", email);
        userProfile.put("fullName", fullName);
        userProfile.put("createdAt", Timestamp.now());

        firebaseFirestore.collection("users").document(uid)
                .set(userProfile)
                .addOnSuccessListener(v -> {
                    snackbar(this, "Registration Successful!", true);

                    // Delay redirect until the user has time to actually read the success message
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        Intent intent = new Intent(SignUp.this, SignIn.class);
                        startActivity(intent);
                        finish();
                    }, 2000); // 2-second delay
                })
                .addOnFailureListener(e -> snackbar(this, e.getMessage(), false));
    }

    /**
     * Validates form inputs and updates UI errors dynamically.
     * Returns true if all fields are valid, false otherwise.
     */
    private boolean isValidInputs(EditText fullNameInput, EditText emailInput, EditText passwordInput,
                                   EditText passwordConfirmationInput, String fullName, String email,
                                   String password, String passwordConfirmation) {
        if (fullName.isEmpty()) {
            fullNameInput.setError("Full name is required");
            return false;
        }
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            return false;
        }
        if (email.contains(" ") || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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
        if (passwordConfirmation.isEmpty()) {
            passwordConfirmationInput.setError("Password confirmation is required");
            return false;
        }
        if (!passwordConfirmation.equals(password)) {
            passwordConfirmationInput.setError("Passwords don't match");
            return false;
        }
        return true;
    }
}
