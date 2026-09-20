package com.main.pages;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.main.R;

public class RecipeDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        ImageView btnBack = findViewById(R.id.btn_back);

        // Closes this screen and returns to the previous one
        btnBack.setOnClickListener(v -> finish());
    }
}