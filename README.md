# PantryPal

**Reduce Waste. Cook Smart.**

PantryPal is an Android app that helps users keep track of the food in their pantry, flags items that are close to expiring, and suggests recipes based on the ingredients they already have on hand.

---

## Features

- **User authentication** — sign up and sign in with email and password (Firebase Authentication), with full form validation (required fields, password length, matching passwords).
- **Pantry management (CRUD)** — add, edit, and delete ingredients, each with a name, quantity, unit, category, and expiry date.
- **Expiry tracking** — ingredients expiring within 5 days are flagged with a red "Expiring Soon" badge in the pantry list.
- **Recipe suggestions** — the Recipes screen compares the ingredients in a recipe against the user's current pantry and suggests recipes the user can make, sorted by how many of the required ingredients they already have.
- **Recipe details** — tapping a suggested recipe opens a detail screen showing the full ingredient checklist (pre-ticked for ingredients already owned), step-by-step method, prep time, and servings.

---

## Screens

| Screen | Description |
|---|---|
| Sign Up | Create an account with full name, email, password, and password confirmation. |
| Sign In | Log in with email and password. |
| My Pantry | Lists all stored ingredients, with a floating "+" button to add new items. |
| Add / Edit Ingredient | A single reusable form for adding a new ingredient or editing an existing one; includes a delete option when editing. |
| Recipes | Shows recipes suggested from the current pantry contents. |
| Recipe Detail | Shows a recipe's ingredients (checked off against the pantry), method, and estimated time/servings. |

---

## Tech Stack

- **Language:** Java
- **Platform:** Android (native)
- **Backend:** [Firebase Authentication](https://firebase.google.com/docs/auth) for user accounts, [Cloud Firestore](https://firebase.google.com/docs/firestore) for storing pantry data
- **UI:** XML layouts with `ConstraintLayout`, `CardView`, and Material Components (`BottomNavigationView`, `MaterialButton`)

---

## Data Structure (Firestore)

```
users (collection)
 └── {uid} (document)
      ├── email
      ├── fullName
      └── createdAt

pantry (collection)
 └── {uid} (document)
      └── ingredients (sub-collection)
           └── {ingredientId} (document)
                ├── name
                ├── quantity
                ├── unit
                ├── category
                └── expiryDate
```

Each user's ingredients are stored under their own Firebase Authentication UID, so users can only see and edit their own pantry. Collection/document path names are centralized in `enums/Directory.java` rather than typed as raw strings throughout the codebase.

> **Note:** recipes are currently defined in code (`RecipeRepository.java`) rather than stored in Firestore.

---

## Getting Started

### Prerequisites
- [Android Studio](https://developer.android.com/studio) (Medium Phone API 37.1 emulator or similar used for development/testing)
- A [Firebase](https://console.firebase.google.com/) project with **Authentication** (Email/Password provider) and **Cloud Firestore** enabled

### Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/<your-username>/pantrypal.git
   ```
2. Open the project in Android Studio.
3. Create a Firebase project and add an Android app to it, using your project's application ID.
4. Download the generated `google-services.json` file from the Firebase console and place it in the `app/` directory.
5. Sync Gradle and run the app on an emulator or physical device.

---

## Project Structure (key files)

```
app/src/main/java/com/main/
 ├── authentication/
 │    ├── SignIn.java
 │    └── SignUp.java
 ├── data/
 │    └── RecipeRepository.java
 ├── enums/
 │    └── Directory.java
 ├── helper/
 │    ├── DatePickerHelper.java
 │    ├── DropDownHelper.java
 │    ├── IdGeneratorHelper.java
 │    └── SnackBarHelper.java
 ├── models/
 │    ├── Ingredient.java
 │    ├── Recipe.java
 │    └── User.java
 └── pages/
      ├── AddIngredients.java
      ├── Pantry.java
      ├── Recipe.java          // Recipes list screen
      └── RecipeDetail.java

app/src/main/res/layout/
 ├── activity_add_ingredient.xml
 ├── activity_pantry.xml
 ├── activity_recipe.xml
 ├── activity_recipe_card.xml
 ├── activity_recipe_detail.xml
 ├── activity_signin.xml
 ├── activity_signup.xml
 ├── category_dropdown.xml
 ├── date_picker.xml
 ├── display_card.xml
 ├── ingredient_checkbox.xml
 ├── item_method_step.xml
 └── unit_dropdown.xml
```
