package com.example.intelligent_recipy;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intelligent_recipy.models.Recipe;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ===== TEMPORARY TEST (Phase 5) =====
        RecipeRepository repo = new RecipeRepository(this);
        Log.d(TAG, "Loaded " + repo.size() + " recipes");

        List<String> fakePantry = Arrays.asList(
                "eggs", "cheese", "butter", "salt", "pepper",
                "chicken breast", "garlic"
        );
        
        Log.d(TAG, "Fake pantry: " + fakePantry);

        List<RecipeMatcher.FullMatch> fullMatches =
                RecipeMatcher.findFullMatches(repo.getAll(), fakePantry);
        Log.d(TAG, "=== CAN COOK NOW ===");
        for (RecipeMatcher.FullMatch fm : fullMatches) {
            Log.d(TAG, "  ✓ " + fm.recipe.getName());
        }

        List<RecipeMatcher.AlmostMatch> almostMatches =
                RecipeMatcher.findAlmostMatches(repo.getAll(), fakePantry);
        Log.d(TAG, "=== ONE AWAY ===");
        for (RecipeMatcher.AlmostMatch am : almostMatches) {
            Log.d(TAG, "  ~ " + am.recipe.getName()
                    + "  (missing: " + am.missingIngredient + ")");
        }
        // ===== END TEMPORARY TEST =====
    }
}