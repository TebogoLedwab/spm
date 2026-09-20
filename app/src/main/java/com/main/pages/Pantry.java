package com.main.pages;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Highlight the current tab BEFORE attaching the listener,
        // so this call doesn't trigger a navigation.
        bottomNav.setSelectedItemId(R.id.nav_pantry);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_pantry) {
                return true; // already here
            } else if (id == R.id.nav_recipes) {
                startActivity(new Intent(Pantry.this, Recipe.class));
                overridePendingTransition(0, 0);
                return true;
            }
            // Add a branch for R.id.nav_settings once that screen exists
            return false;
        });
    }
}