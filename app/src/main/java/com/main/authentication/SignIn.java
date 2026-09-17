package com.main.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.main.R;

public class SignIn extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Button signUp = findViewById(R.id.signup);
        Button signIn = findViewById(R.id.sign_in);

        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this, SignUp.class);
            startActivity(intent);
        });

        signIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this, SignIn.class);
            startActivity(intent);
        });
    }
}
