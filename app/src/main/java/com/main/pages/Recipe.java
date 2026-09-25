package com.main.pages;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.main.R;
import com.main.data.RecipeRepository;
import com.main.models.Ingredient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Recipe extends AppCompatActivity {

    private static final int MIN_MATCHES_TO_SUGGEST = 1; // show a recipe if at least 1 ingredient matches

    private LinearLayout recipesContainer;
    private LayoutInflater inflater;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        recipesContainer = findViewById(R.id.recipes_container);
        inflater = LayoutInflater.from(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

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

    @Override
    protected void onResume() {
        super.onResume();
        // Reload every time the screen is shown, in case the pantry changed
        loadPantryAndSuggestRecipes();
    }

    private void loadPantryAndSuggestRecipes() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) return;

        firebaseFirestore.collection("pantry")
                .document(user.getUid())
                .collection("ingredients")
                .get()
                .addOnSuccessListener(this::onPantryLoaded);
    }

    private void onPantryLoaded(QuerySnapshot snapshot) {
        Set<String> ownedIngredients = new HashSet<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            Ingredient ingredient = doc.toObject(Ingredient.class);
            if (ingredient != null && ingredient.getName() != null) {
                ownedIngredients.add(ingredient.getName().trim().toLowerCase());
            }
        }
        displaySuggestedRecipes(ownedIngredients);
    }

    private void displaySuggestedRecipes(Set<String> ownedIngredients) {
        recipesContainer.removeAllViews();

        List<com.main.models.Recipe> matches = new ArrayList<>();
        for (com.main.models.Recipe recipe : RecipeRepository.getAllRecipes()) {
            if (countMatches(recipe, ownedIngredients) >= MIN_MATCHES_TO_SUGGEST) {
                matches.add(recipe);
            }
        }

        // Recipes you can make more completely appear first
        matches.sort((a, b) -> countMatches(b, ownedIngredients) - countMatches(a, ownedIngredients));

        if (matches.isEmpty()) {
            showEmptyState();
            return;
        }

        for (com.main.models.Recipe recipe : matches) {
            createRecipeCard(recipe, ownedIngredients);
        }
    }

    private int countMatches(com.main.models.Recipe recipe, Set<String> ownedIngredients) {
        int count = 0;
        for (String required : recipe.getIngredients()) {
            if (ownedIngredients.contains(required.toLowerCase())) count++;
        }
        return count;
    }

    private void createRecipeCard(com.main.models.Recipe recipe, Set<String> ownedIngredients) {
        View card = inflater.inflate(R.layout.activity_recipe_card, recipesContainer, false);

        ImageView ivPhoto = card.findViewById(R.id.iv_recipe_photo);
        TextView tvTitle = card.findViewById(R.id.tv_recipe_title);
        TextView tvTime = card.findViewById(R.id.tv_recipe_time);
        TextView tvAvailable = card.findViewById(R.id.tv_ingredients_available);

        int matched = countMatches(recipe, ownedIngredients);
        int total = recipe.getIngredients().size();

        ivPhoto.setImageResource(recipe.getImageResId());
        tvTitle.setText(recipe.getName());
        tvTime.setText(recipe.getMinutes() + " min");
        tvAvailable.setText(matched + "/" + total + " ingredients available");

        card.setOnClickListener(v -> {
            Intent intent = new Intent(Recipe.this, RecipeDetail.class);
            intent.putExtra(RecipeDetail.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(intent);
        });

        recipesContainer.addView(card);
    }

    private void showEmptyState() {
        TextView emptyText = new TextView(this);
        emptyText.setText("Add a few ingredients to your pantry to see recipe suggestions.");
        emptyText.setTextColor(Color.parseColor("#6F6480"));
        emptyText.setPadding(0, 48, 0, 0);
        recipesContainer.addView(emptyText);
    }
}