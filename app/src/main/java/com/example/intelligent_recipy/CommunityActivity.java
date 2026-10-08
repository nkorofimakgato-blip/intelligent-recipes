package com.example.intelligent_recipy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intelligent_recipy.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class CommunityActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private LinearLayout loadingState;
    private LinearLayout emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        recycler = findViewById(R.id.communityRecycler);
        loadingState = findViewById(R.id.loadingState);
        emptyState = findViewById(R.id.emptyState);
        ImageView backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });

        loadingState.setVisibility(View.VISIBLE);
        recycler.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);

        loadRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh on return from AddRecipeActivity
        if (recycler != null && recycler.getVisibility() == View.VISIBLE) {
            loadRecipes();
        }
    }

    private void loadRecipes() {
        new FirestoreRepository().loadCommunityRecipes(
                new FirestoreRepository.OnRecipesLoaded() {
                    @Override
                    public void onLoaded(List<Recipe> recipes) {
                        loadingState.setVisibility(View.GONE);

                        if (recipes.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                            recycler.setVisibility(View.GONE);
                            return;
                        }

                        emptyState.setVisibility(View.GONE);
                        recycler.setVisibility(View.VISIBLE);

                        List<RecipeAdapter.Row> rows = new ArrayList<>();
                        for (Recipe r : recipes) {
                            RecipeAdapter.Row row = RecipeAdapter.Row.recipe(r, null);
                            row.scoreText = "by " + r.getAuthorName();
                            rows.add(row);
                        }

                        recycler.setLayoutManager(new GridLayoutManager(
                                CommunityActivity.this, 2));

                        RecipeAdapter adapter = new RecipeAdapter(rows,
                                new RecipeAdapter.OnRecipeClickListener() {
                                    @Override
                                    public void onRecipeClick(Recipe recipe) {
                                        Intent intent = new Intent(
                                                CommunityActivity.this,
                                                RecipeDetailActivity.class);
                                        intent.putExtra(
                                                RecipeDetailActivity.EXTRA_RECIPE_ID,
                                                recipe.getId());
                                        // Pass community flag so detail knows to fetch from Firestore
                                        intent.putExtra("from_community", true);
                                        startActivity(intent);
                                    }
                                });
                        recycler.setAdapter(adapter);
                    }

                    @Override
                    public void onError(String message) {
                        loadingState.setVisibility(View.GONE);
                        Toast.makeText(CommunityActivity.this,
                                "Error: " + message, Toast.LENGTH_LONG).show();
                    }
                });
    }
}