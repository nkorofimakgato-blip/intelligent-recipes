package com.example.intelligent_recipy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intelligent_recipy.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {

    public static final String EXTRA_PANTRY = "extra_pantry";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        ImageView backButton = findViewById(R.id.backButton);
        RecyclerView recycler = findViewById(R.id.resultsRecycler);
        TextView noResults   = findViewById(R.id.noResultsMessage);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });

        // Get the pantry from the Intent
        ArrayList<String> pantry =
                getIntent().getStringArrayListExtra(EXTRA_PANTRY);

        if (pantry == null) pantry = new ArrayList<>();

        // Load recipes + run matcher
        RecipeRepository repo = new RecipeRepository(this);
        List<RecipeMatcher.FullMatch> fullMatches =
                RecipeMatcher.findFullMatches(repo.getAll(), pantry);
        List<RecipeMatcher.AlmostMatch> almostMatches =
                RecipeMatcher.findAlmostMatches(repo.getAll(), pantry);

        // Build a flat row list with headers + recipes
        List<RecipeAdapter.Row> rows = new ArrayList<>();

        if (!fullMatches.isEmpty()) {
            rows.add(RecipeAdapter.Row.header(
                    "✓ You can cook these now (" + fullMatches.size() + ")"));
            for (RecipeMatcher.FullMatch fm : fullMatches) {
                rows.add(RecipeAdapter.Row.recipe(fm.recipe, null));
            }
        }

        if (!almostMatches.isEmpty()) {
            rows.add(RecipeAdapter.Row.header(
                    "⚠ You're 1 ingredient away (" + almostMatches.size() + ")"));
            for (RecipeMatcher.AlmostMatch am : almostMatches) {
                rows.add(RecipeAdapter.Row.recipe(am.recipe, am.missingIngredient));
            }
        }

        if (rows.isEmpty()) {
            noResults.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        } else {
            noResults.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);

            // 2-column grid
            recycler.setLayoutManager(new GridLayoutManager(this, 2));

            RecipeAdapter adapter = new RecipeAdapter(rows,
                    new RecipeAdapter.OnRecipeClickListener() {
                        @Override
                        public void onRecipeClick(Recipe recipe) {
                            // For now: show a toast. Detail screen comes in Phase 8.
                            android.widget.Toast.makeText(ResultsActivity.this,
                                    "Tapped: " + recipe.getName(),
                                    android.widget.Toast.LENGTH_SHORT).show();
                        }
                    });
            recycler.setAdapter(adapter);
        }
    }
}