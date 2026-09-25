package com.main.pages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.R;
import com.main.data.RecipeRepository;
import com.main.models.Ingredient;
import com.main.models.Recipe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipeDetail extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private LinearLayout ingredientsContainer;
    private LinearLayout methodContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        ImageView btnBack = findViewById(R.id.btn_back);
        // Closes this screen and returns to the previous one
        btnBack.setOnClickListener(v -> finish());

        String recipeId = getIntent().getStringExtra(EXTRA_RECIPE_ID);
        Recipe recipe = RecipeRepository.findById(recipeId);

        if (recipe == null) {
            finish(); // recipe no longer exists — bail out safely instead of crashing
            return;
        }

        ingredientsContainer = findViewById(R.id.ingredients_container);
        methodContainer = findViewById(R.id.method_container);

        bindRecipeHeader(recipe);
        bindMethodSteps(recipe);
        loadPantryThenBindIngredients(recipe);
    }

    private void bindRecipeHeader(Recipe recipe) {
        ((TextView) findViewById(R.id.tv_toolbar_title)).setText(recipe.getName());
        ((TextView) findViewById(R.id.tv_recipe_title)).setText(recipe.getName());
        ((TextView) findViewById(R.id.tv_time)).setText(recipe.getMinutes() + " min");
        ((TextView) findViewById(R.id.tv_servings))
                .setText(recipe.getServings() + (recipe.getServings() == 1 ? " serving" : " servings"));
        ((ImageView) findViewById(R.id.iv_recipe_hero)).setImageResource(recipe.getImageResId());
    }

    private void loadPantryThenBindIngredients(Recipe recipe) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            bindIngredientChecklist(recipe, new HashSet<>());
            return;
        }

        firebaseFirestore.collection("pantry")
                .document(user.getUid())
                .collection("ingredients")
                .get()
                .addOnSuccessListener(snapshot -> {
                    Set<String> owned = new HashSet<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Ingredient ingredient = doc.toObject(Ingredient.class);
                        if (ingredient != null && ingredient.getName() != null) {
                            owned.add(ingredient.getName().trim().toLowerCase());
                        }
                    }
                    bindIngredientChecklist(recipe, owned);
                })
                .addOnFailureListener(e -> bindIngredientChecklist(recipe, new HashSet<>()));
    }

    private void bindIngredientChecklist(Recipe recipe, Set<String> ownedIngredients) {
        ingredientsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (String ingredientName : recipe.getIngredients()) {
            View row = inflater.inflate(R.layout.ingredient_checkbox, ingredientsContainer, false);
            CheckBox checkBox = row.findViewById(R.id.cb_ingredient);

            checkBox.setText(capitalize(ingredientName));
            // Tick it only if the pantry already has it — this shows the user what they still need
            checkBox.setChecked(ownedIngredients.contains(ingredientName.toLowerCase()));

            ingredientsContainer.addView(row);
        }
    }

    private void bindMethodSteps(Recipe recipe) {
        methodContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        List<String> steps = recipe.getSteps();

        for (int i = 0; i < steps.size(); i++) {
            View row = inflater.inflate(R.layout.item_method_step, methodContainer, false);
            ((TextView) row.findViewById(R.id.tv_step_number)).setText(String.valueOf(i + 1));
            ((TextView) row.findViewById(R.id.tv_step_text)).setText(steps.get(i));
            methodContainer.addView(row);
        }
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}