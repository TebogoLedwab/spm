package com.main.models;
import java.util.List;

public class Recipe {
    String id;
    String name;
    int imageResId;
    int minutes;
    int servings;
    List<String> ingredients; // required ingredient names, lowercase
    List<String> steps;

    public Recipe(String id, String name, int imageResId, int minutes, int servings,
                  List<String> ingredients, List<String> steps) {
        this.id = id;
        this.name = name;
        this.imageResId = imageResId;
        this.minutes = minutes;
        this.servings = servings;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getImageResId() { return imageResId; }
    public int getMinutes() { return minutes; }
    public int getServings() { return servings; }
    public List<String> getIngredients() { return ingredients; }
    public List<String> getSteps() { return steps; }
}
