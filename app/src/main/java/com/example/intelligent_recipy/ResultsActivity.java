package com.example.intelligent_recipy;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        ImageView backButton        = findViewById(R.id.backButton);
        RecyclerView recycler       = findViewById(R.id.resultsRecycler);
        LinearLayout emptyState     = findViewById(R.id.emptyState);
        LinearLayout loadingOverlay = findViewById(R.id.loadingOverlay);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });

        ArrayList<String> pantry = getIntent().getStringArrayListExtra(EXTRA_PANTRY);
        if (pantry == null) pantry = new ArrayList<>();

        loadingOverlay.setVisibility(View.VISIBLE);
        recycler.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);

        final ArrayList<String> finalPantry = pantry;

        new Thread(new Runnable() {
            @Override
            public void run() {
                RecipeRepository repo = new RecipeRepository(ResultsActivity.this);

                List<RecipeMatcher.FullMatch> fullMatches =
                        RecipeMatcher.findFullMatches(repo.getAll(), finalPantry);
                List<RecipeMatcher.AlmostMatch> almostMatches =
                        RecipeMatcher.findAlmostMatches(repo.getAll(), finalPantry);

                final List<RecipeAdapter.Row> rows = new ArrayList<>();

                if (!fullMatches.isEmpty()) {
                    rows.add(RecipeAdapter.Row.header(
                            "✓ You can cook these now (" + fullMatches.size() + ")"));
                    for (RecipeMatcher.FullMatch fm : fullMatches) {
                        rows.add(RecipeAdapter.Row.recipe(fm.recipe, null));
                    }
                }

                if (!almostMatches.isEmpty()) {
                    rows.add(RecipeAdapter.Row.header(
                            "⚠ You're close — missing 1–2 (" + almostMatches.size() + ")"));
                    for (RecipeMatcher.AlmostMatch am : almostMatches) {
                        rows.add(RecipeAdapter.Row.recipe(am.recipe, am.missingIngredient));
                    }
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        loadingOverlay.setVisibility(View.GONE);

                        if (rows.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                            recycler.setVisibility(View.GONE);
                        } else {
                            emptyState.setVisibility(View.GONE);
                            recycler.setVisibility(View.VISIBLE);

                            recycler.setLayoutManager(new GridLayoutManager(
                                    ResultsActivity.this, 2));

                            RecipeAdapter adapter = new RecipeAdapter(rows,
                                    new RecipeAdapter.OnRecipeClickListener() {
                                        @Override
                                        public void onRecipeClick(Recipe recipe) {
                                            android.content.Intent intent =
                                                    new android.content.Intent(
                                                            ResultsActivity.this,
                                                            RecipeDetailActivity.class);
                                            intent.putExtra(
                                                    RecipeDetailActivity.EXTRA_RECIPE_ID,
                                                    recipe.getId());
                                            intent.putStringArrayListExtra(
                                                    RecipeDetailActivity.EXTRA_PANTRY,
                                                    finalPantry);
                                            startActivity(intent);
                                        }
                                    });
                            recycler.setAdapter(adapter);
                        }
                    }
                });
            }
        }).start();
    }
}