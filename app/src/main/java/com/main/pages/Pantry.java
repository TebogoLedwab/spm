package com.main.pages;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.main.R;

public class Pantry extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        FloatingActionButton fabAdd = findViewById(R.id.fab_add);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Pantry.this, AddIngredients.class);
            startActivity(intent);
        });
    }
}