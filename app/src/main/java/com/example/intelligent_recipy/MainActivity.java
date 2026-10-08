package com.example.intelligent_recipy;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText ingredientInput;
    private Button addButton;
    private Button findRecipesButton;
    private Button surpriseMeButton;
    private ChipGroup chipGroup;
    private TextView emptyMessage;

    // The user's "pantry" — list of ingredients they've added
    private final List<String> pantry = new ArrayList<>();

    // Recipe data (loaded once on start)
    private RecipeRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ingredientInput    = findViewById(R.id.ingredientInput);
        addButton          = findViewById(R.id.addButton);
        findRecipesButton  = findViewById(R.id.findRecipesButton);
        surpriseMeButton   = findViewById(R.id.surpriseMeButton);
        chipGroup          = findViewById(R.id.chipGroup);
        emptyMessage       = findViewById(R.id.emptyMessage);

        // Load recipes once
        repository = new RecipeRepository(this);
        Log.d(TAG, "Loaded " + repository.size() + " recipes");

        updateEmptyMessage();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { addIngredientFromInput(); }
        });

        findRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { findRecipes(false); }
        });

        surpriseMeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { findRecipes(true); }
        });
    }

    /** Reads the input field, adds it as a chip, clears the field. */
    private void addIngredientFromInput() {
        String raw = ingredientInput.getText().toString().trim();
        if (raw.isEmpty()) return;

        String normalized = raw.toLowerCase();

        // Prevent duplicates
        if (pantry.contains(normalized)) {
            Toast.makeText(this, "\"" + raw + "\" is already in your list",
                    Toast.LENGTH_SHORT).show();
            ingredientInput.setText("");
            return;
        }

        pantry.add(normalized);
        addChip(normalized);
        ingredientInput.setText("");
        updateEmptyMessage();
    }

    /** Creates and attaches a chip with a close (X) icon. */
    private void addChip(final String ingredient) {
        Chip chip = new Chip(this);
        chip.setText(ingredient);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setClickable(false);

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pantry.remove(ingredient);
                chipGroup.removeView(chip);
                updateEmptyMessage();
            }
        });

        chipGroup.addView(chip);
    }

    /** Hides "No ingredients yet" when pantry has items. */
    private void updateEmptyMessage() {
        emptyMessage.setVisibility(pantry.isEmpty() ? View.VISIBLE : View.GONE);
    }

    /**
     * Runs the matcher and logs the results.
     * @param surpriseMe if true, skip full matches and only show "one away" (i.e., recipes
     *                   the user is NOT able to make, giving them a suggestion of what to buy)
     */
    private void findRecipes(boolean surpriseMe) {
        if (pantry.isEmpty()) {
            Toast.makeText(this, "Add at least one ingredient first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "=== Searching with pantry: " + pantry + " ===");

        List<RecipeMatcher.FullMatch> fullMatches =
                RecipeMatcher.findFullMatches(repository.getAll(), pantry);
        List<RecipeMatcher.AlmostMatch> almostMatches =
                RecipeMatcher.findAlmostMatches(repository.getAll(), pantry);

        if (!surpriseMe) {
            Log.d(TAG, "CAN COOK NOW (" + fullMatches.size() + "):");
            for (RecipeMatcher.FullMatch fm : fullMatches) {
                Log.d(TAG, "  ✓ " + fm.recipe.getName());
            }
        } else {
            Log.d(TAG, "SURPRISE ME — recipes you're 1 ingredient away from:");
        }

        Log.d(TAG, "ONE AWAY (" + almostMatches.size() + "):");
        for (RecipeMatcher.AlmostMatch am : almostMatches) {
            Log.d(TAG, "  ~ " + am.recipe.getName()
                    + "  (missing: " + am.missingIngredient + ")");
        }

        Toast.makeText(this,
                fullMatches.size() + " cook now, "
                        + almostMatches.size() + " one away. Check Logcat.",
                Toast.LENGTH_LONG).show();
    }
}