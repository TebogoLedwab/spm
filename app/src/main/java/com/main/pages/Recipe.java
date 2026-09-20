package com.main.pages;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.main.R;

public class Recipe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        CardView cardOmelette = findViewById(R.id.card_omelette);
        cardOmelette.setOnClickListener(v ->
                startActivity(new Intent(Recipe.this, RecipeDetail.class)));

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_recipes);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_recipes) {
                return true; // already here
            } else if (id == R.id.nav_pantry) {
                Intent intent = new Intent(Recipe.this, Pantry.class);
                // Reuse the existing Pantry screen instead of stacking a new one
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}