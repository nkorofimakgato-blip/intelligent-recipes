package com.example.intelligent_recipy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intelligent_recipy.models.Recipe;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText nameInput, categoryInput, authorInput, ingredientInput, instructionsInput;
    private ChipGroup ingredientChips;
    private Button saveButton;

    private final List<String> ingredients = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        nameInput        = findViewById(R.id.recipeNameInput);
        categoryInput    = findViewById(R.id.recipeCategoryInput);
        authorInput      = findViewById(R.id.authorNameInput);
        ingredientInput  = findViewById(R.id.ingredientInput);
        instructionsInput= findViewById(R.id.instructionsInput);
        ingredientChips  = findViewById(R.id.ingredientChips);
        saveButton       = findViewById(R.id.saveRecipeButton);
        ImageView backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });

        findViewById(R.id.addIngredientButton).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { addIngredient(); }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { save(); }
        });
    }

    private void addIngredient() {
        String raw = ingredientInput.getText().toString().trim();
        if (raw.isEmpty()) return;

        if (ingredients.contains(raw.toLowerCase())) {
            Toast.makeText(this, "Already added", Toast.LENGTH_SHORT).show();
            ingredientInput.setText("");
            return;
        }

        ingredients.add(raw.toLowerCase());
        addChip(raw);
        ingredientInput.setText("");
    }

    private void addChip(final String text) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setClickable(false);

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredients.remove(text.toLowerCase());
                ingredientChips.removeView(chip);
            }
        });
        ingredientChips.addView(chip);
    }

    private void save() {
        String name         = nameInput.getText().toString().trim();
        String category     = categoryInput.getText().toString().trim();
        String author       = authorInput.getText().toString().trim();
        String instructions = instructionsInput.getText().toString().trim();

        // Validate
        if (name.isEmpty()) {
            Toast.makeText(this, "Recipe name required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ingredients.isEmpty()) {
            Toast.makeText(this, "Add at least one ingredient", Toast.LENGTH_SHORT).show();
            return;
        }
        if (instructions.isEmpty()) {
            Toast.makeText(this, "Instructions required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (author.isEmpty()) author = "Anonymous";

        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setCategory(category.isEmpty() ? "Community" : category);
        recipe.setArea(author);
        recipe.setAuthorName(author);
        recipe.setInstructions(instructions);
        recipe.setIngredients(new ArrayList<>(ingredients));
        recipe.setMeasures(new ArrayList<String>()); // empty for community
        recipe.setImage("");                         // no image for now

        saveButton.setEnabled(false);
        saveButton.setText("Sharing...");

        new FirestoreRepository().saveRecipe(recipe, new FirestoreRepository.OnRecipeSaved() {
            @Override
            public void onSaved() {
                Toast.makeText(AddRecipeActivity.this,
                        "Recipe shared! 🎉", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onError(String message) {
                saveButton.setEnabled(true);
                saveButton.setText("Share with everyone");
                Toast.makeText(AddRecipeActivity.this,
                        "Failed: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
}