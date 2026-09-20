package com.main.pages;

import static com.main.enums.Directory.INGREDIENTS;
import static com.main.enums.Directory.PANTRY;
import static com.main.helper.SnackBarHelper.snackbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.R;
import com.main.models.Ingredient;

import java.time.LocalDate;
import java.util.Objects;

public class Pantry extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private LinearLayout parentContainer;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        // Initialize Firebase and layout utilities
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        parentContainer = findViewById(R.id.parentContainer);
        inflater = LayoutInflater.from(this);

        // Setup FAB Action
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> startActivity(new Intent(Pantry.this, AddIngredients.class)));

        // Setup Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_pantry);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                return true;
            } else if (id == R.id.nav_recipes) {
                startActivity(new Intent(Pantry.this, Recipe.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        loadPantryData();
    }

    private void loadPantryData() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            snackbar(this, "User session expired", false);
            return;
        }

        // Fetch user ingredients asynchronously from Firestore
        firebaseFirestore.collection(PANTRY.value())
                .document(user.getUid())
                .collection(INGREDIENTS.value())
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult() == null) {
                        snackbar(this, "Error getting ingredients", false);
                        return;
                    }

                    // Prevent view duplication on subsequent data refreshes
                    parentContainer.removeAllViews();

                    // Map documents to model objects, filter null entries, and loop
                    task.getResult().getDocuments().stream()
                            .map(d -> d.toObject(Ingredient.class))
                            .filter(Objects::nonNull)
                            .forEach(this::createIngredientCard);
                });
    }

    private void createIngredientCard(Ingredient ingredient) {
        // Inflate custom card layout
        View cardView = inflater.inflate(R.layout.display_card, parentContainer, false);

        // Bind layout views
        TextView tvName = cardView.findViewById(R.id.item_name);
        TextView tvQuantity = cardView.findViewById(R.id.item_quantity);
        TextView tvTag = cardView.findViewById(R.id.item_tag);
        TextView tvAlert = cardView.findViewById(R.id.item_alert);

        // Populate layout text fields
        tvName.setText(ingredient.getName());
        tvQuantity.setText(String.valueOf(ingredient.getQuantity()));
        tvTag.setText(ingredient.getCategory());

        try {
            LocalDate expiryDate = LocalDate.parse(ingredient.getExpiryDate());

            if (expiryDate.isBefore(LocalDate.now().plusDays(5))) {
                tvAlert.setText("Expiring Soon");
                tvAlert.setVisibility(View.VISIBLE);
                tvAlert.setBackgroundColor(Color.parseColor("#BA1A1A"));
            } else {
                tvAlert.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e("Pantry", "Error parsing expiry date for " + ingredient.getName(), e);
            tvAlert.setVisibility(View.GONE);
        }

        // Row interaction click hook
        cardView.setOnClickListener(v -> snackbar(this, "Clicked: " + ingredient.getName(), true));

        // Mount completed view to root container
        parentContainer.addView(cardView);
    }
}
