package com.main.pages;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.R;
import com.main.authentication.SignIn;
import com.main.models.Ingredient;

import java.time.LocalDate;

import static com.main.enums.Directory.*;
import static com.main.helper.DatePickerHelper.showDatePicker;
import static com.main.helper.DropDownHelper.createSpinnerAdapter;
import static com.main.helper.IdGeneratorHelper.generateID;
import static com.main.helper.SnackBarHelper.snackbar;

public class AddIngredients extends AppCompatActivity {

    // Intent extra keys (shared with Pantry)
    public static final String EXTRA_ID = "ingredient_id";
    public static final String EXTRA_NAME = "ingredient_name";
    public static final String EXTRA_QUANTITY = "ingredient_quantity";
    public static final String EXTRA_UNIT = "ingredient_unit";
    public static final String EXTRA_CATEGORY = "ingredient_category";
    public static final String EXTRA_EXPIRY = "ingredient_expiry";
    // Non-null when editing an existing ingredient
    private String editingDocumentId;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        EditText nameInput = findViewById(R.id.til_ingredient_name);
        EditText quantityInput = findViewById(R.id.et_quantity);
        TextView expiryDateInput = findViewById(R.id.selected_date);
        Spinner unitSpinnerInput = findViewById(R.id.spinner_unit);
        Spinner categorySpinnerInput = findViewById(R.id.spinner_categories);
        View deleteButton = findViewById(R.id.btn_delete);

        unitSpinnerInput.setAdapter(createSpinnerAdapter(this, R.array.unit_dropdown_items));
        categorySpinnerInput.setAdapter(createSpinnerAdapter(this, R.array.category_dropdown_items));

        // Pre-fill the form if we were opened from a pantry card
        Intent intent = getIntent();
        editingDocumentId = intent.getStringExtra(EXTRA_ID);
        if (editingDocumentId != null) {
            nameInput.setText(intent.getStringExtra(EXTRA_NAME));
            quantityInput.setText(String.valueOf(intent.getLongExtra(EXTRA_QUANTITY, 0L)));
            expiryDateInput.setText(intent.getStringExtra(EXTRA_EXPIRY));
            selectSpinnerItem(unitSpinnerInput, intent.getStringExtra(EXTRA_UNIT));
            selectSpinnerItem(categorySpinnerInput, intent.getStringExtra(EXTRA_CATEGORY));
        }

        if (editingDocumentId == null) {
            // Nothing to delete when adding a new ingredient
            deleteButton.setVisibility(View.GONE);
        } else {
            deleteButton.setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("Delete ingredient?")
                            .setMessage("This can't be undone.")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Delete", (dialog, which) -> deleteIngredient(editingDocumentId))
                            .show());
        }

        findViewById(R.id.date_picker_container).setOnClickListener(v ->
                showDatePicker(this, localDate -> expiryDateInput.setText(localDate.toString()))
        );

        findViewById(R.id.btn_save).setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String quantityStr = quantityInput.getText().toString().trim();
            String unit = unitSpinnerInput.getSelectedItem() != null ? unitSpinnerInput.getSelectedItem().toString() : "";
            String category = categorySpinnerInput.getSelectedItem() != null ? categorySpinnerInput.getSelectedItem().toString() : "";
            String expiryDateStr = expiryDateInput.getText().toString().trim();

            if (isValidInputs(nameInput, quantityInput, name, quantityStr, unit, category, expiryDateStr)) {
                saveIngredient(new Ingredient(
                        name,
                        Long.parseLong(quantityStr),
                        unit,
                        category,
                        expiryDateStr,
                        LocalDate.now().toString()
                ));
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    /** Selects the spinner entry whose text matches the given value (case-insensitive). */
    private void selectSpinnerItem(Spinner spinner, String value) {
        if (value == null) return;
        Adapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            Object item = adapter.getItem(i);
            if (item != null && item.toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }
    private void saveIngredient(Ingredient ingredient) {
        try {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                snackbar(this, "User session expired", false);
                return;
            }

            boolean isEditing = editingDocumentId != null;
            String documentId = isEditing ? editingDocumentId : generateID(ingredient);

            firebaseFirestore.collection(PANTRY.value())
                    .document(user.getUid())
                    .collection(INGREDIENTS.value())
                    .document(documentId)
                    .set(ingredient)
                    .addOnSuccessListener(v -> {
                        snackbar(this, isEditing ? "Updated Successfully!" : "Added Successfully!", true);
                        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToPantry, 2000);
                    })
                    .addOnFailureListener(e -> snackbar(this, e.getMessage(), false));
        } catch (Exception e) {
            snackbar(this, "Failed to add ingredient", false);
        }
    }
    private void deleteIngredient(String documentId) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            snackbar(this, "User session expired", false);
            return;
        }

        firebaseFirestore.collection(PANTRY.value())
                .document(user.getUid())
                .collection(INGREDIENTS.value())
                .document(documentId)
                .delete()
                .addOnSuccessListener(v -> {
                    snackbar(this, "Deleted Successfully!", true);
                    new Handler(Looper.getMainLooper()).postDelayed(this::navigateToPantry, 1500);
                })
                .addOnFailureListener(e -> snackbar(this, e.getMessage(), false));
    }

    private void navigateToPantry() {
        Intent intent = new Intent(this, Pantry.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Validates form inputs and updates UI errors dynamically.
     * Returns true if all fields are valid, false otherwise.
     */
    private boolean isValidInputs(EditText nameField, EditText quantityField,
                                  String name, String quantityStr,
                                  String unit, String category, String expiryDateStr) {
        if (name.isEmpty()) {
            nameField.setError("Name is required");
            return false;
        }

        if (quantityStr.isEmpty()) {
            quantityField.setError("Quantity is required");
            return false;
        }

        try {
            if (Long.parseLong(quantityStr) <= 0) {
                quantityField.setError("Quantity must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            quantityField.setError("Invalid number format");
            return false;
        }

        if (unit.isEmpty() || unit.equalsIgnoreCase("Select Unit")) {
            snackbar(this, "Unit is required", false);
            return false;
        }

        if (category.isEmpty() || category.equalsIgnoreCase("Select Category")) {
            snackbar(this, "Category is required", false);
            return false;
        }

        if (expiryDateStr.isEmpty() || expiryDateStr.equalsIgnoreCase("Select Date")) {
            snackbar(this, "Expiry date is required", false);
            return false;
        }

        return true;
    }
}
