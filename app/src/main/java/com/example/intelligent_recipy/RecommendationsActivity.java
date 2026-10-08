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

public class RecommendationsActivity extends AppCompatActivity {

    public static final String EXTRA_PANTRY = "extra_pantry";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        ImageView backButton = findViewById(R.id.backButton);
        RecyclerView recycler = findViewById(R.id.recommendationsRecycler);
        TextView emptyMessage = findViewById(R.id.emptyMessage);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });

        final ArrayList<String> pantry =
                getIntent().getStringArrayListExtra(EXTRA_PANTRY) != null
                        ? getIntent().getStringArrayListExtra(EXTRA_PANTRY)
                        : new ArrayList<String>();

        if (pantry.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
            return;
        }

        RecipeRepository repo = new RecipeRepository(this);
        List<RecipeMatcher.Recommendation> recs =
                RecipeMatcher.findRecommendations(repo.getAll(), pantry);

        if (recs.size() > 20) recs = recs.subList(0, 20);

        if (recs.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
            return;
        }

        final List<RecipeAdapter.Row> rows = new ArrayList<>();
        for (RecipeMatcher.Recommendation rec : recs) {
            RecipeAdapter.Row row = RecipeAdapter.Row.recipe(rec.recipe, null);
            row.scoreText = rec.matchedCount + "/" + rec.totalCount + " ingredients";
            rows.add(row);
        }

        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        RecipeAdapter adapter = new RecipeAdapter(rows,
                new RecipeAdapter.OnRecipeClickListener() {
                    @Override
                    public void onRecipeClick(Recipe recipe) {
                        Intent intent = new Intent(
                                RecommendationsActivity.this,
                                RecipeDetailActivity.class);
                        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID,
                                recipe.getId());
                        intent.putStringArrayListExtra(
                                RecipeDetailActivity.EXTRA_PANTRY,
                                pantry);
                        startActivity(intent);
                    }
                });
        recycler.setAdapter(adapter);
    }
}