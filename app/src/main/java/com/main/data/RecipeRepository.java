package com.main.data;

import com.main.R;
import com.main.models.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeRepository {
    public static List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        recipes.add(new Recipe(
                "omelette",
                "Tomato & Cheese Omelette",
                R.drawable.cheese_and_tomato_omlette,
                15, 1,
                Arrays.asList("eggs", "tomatoes", "cheddar cheese", "salt"),
                Arrays.asList(
                        "Beat the eggs in a clean mixing bowl with a pinch of salt until smooth and light yellow.",
                        "Dice the tomatoes finely and prepare your shredded cheddar cheese.",
                        "Heat a small non-stick pan over medium heat with a light spray of olive oil.",
                        "Pour in the beaten eggs, letting them spread evenly to form a smooth flat circle.",
                        "Once the bottom sets, add the tomato and cheese toppings, then fold in half carefully."
                )
        ));
        recipes.add(new Recipe(
                "cheesy_rice",
                "Cheesy Rice Bowl",
                R.drawable.cheesy_rice,
                20, 2,
                Arrays.asList("rice", "cheese", "milk", "salt"),
                Arrays.asList(
                        "Cook the rice according to the packet instructions until tender.",
                        "Warm the milk gently in a small saucepan over low heat.",
                        "Stir the cheese into the warm milk until melted and smooth.",
                        "Pour the cheese sauce over the cooked rice and season with salt.",
                        "Mix well and serve warm."
                )
        ));

        return recipes;
    }

    public static Recipe findById(String id) {
        for (Recipe recipe : getAllRecipes()) {
            if (recipe.getId().equals(id)) return recipe;
        }
        return null;
    }

}
