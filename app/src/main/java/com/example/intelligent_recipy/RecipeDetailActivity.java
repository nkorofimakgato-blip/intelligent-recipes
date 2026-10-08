package com.example.intelligent_recipy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.intelligent_recipy.models.Recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";
    public static final String EXTRA_PANTRY = "extra_pantry";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        ImageView backButton    = findViewById(R.id.backButton);
        ImageView detailImage   = findViewById(R.id.detailImage);
        TextView detailName     = findViewById(R.id.detailName);
        TextView detailMeta     = findViewById(R.id.detailMeta);
        TextView detailInstructions = findViewById(R.id.detailInstructions);
        LinearLayout ingredientsContainer = findViewById(R.id.ingredientsContainer);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { finish(); }
        });

        String recipeId = getIntent().getStringExtra(EXTRA_RECIPE_ID);
        ArrayList<String> pantry = getIntent().getStringArrayListExtra(EXTRA_PANTRY);
        if (pantry == null) pantry = new ArrayList<>();

        // Find the recipe
        RecipeRepository repo = new RecipeRepository(this);
        Recipe recipe = null;
        for (Recipe r : repo.getAll()) {
            if (r.getId() != null && r.getId().equals(recipeId)) {
                recipe = r;
                break;
            }
        }

        if (recipe == null) {
            detailName.setText("Recipe not found");
            return;
        }

        final Recipe finalRecipe = recipe;

        // Fill in basic info
        detailName.setText(recipe.getName());
        detailMeta.setText(recipe.getCategory() + " · " + recipe.getArea());
        detailInstructions.setText(recipe.getInstructions());

        Glide.with(this)
                .load(recipe.getImage())
                .placeholder(android.R.color.darker_gray)
                .centerCrop()
                .into(detailImage);

        // Normalize pantry for quick lookup
        Set<String> pantrySet = new HashSet<>();
        for (String item : pantry) {
            pantrySet.add(item.toLowerCase(Locale.ROOT).trim());
        }

        // Render ingredient rows
        List<String> ingredients = recipe.getIngredients();
        List<String> measures    = recipe.getMeasures();
        LayoutInflater inflater  = LayoutInflater.from(this);

        for (int i = 0; i < ingredients.size(); i++) {
            String ing = ingredients.get(i);
            if (ing == null || ing.trim().isEmpty()) continue;

            String measure = (measures != null && i < measures.size())
                    ? measures.get(i) : "";

            View row = inflater.inflate(R.layout.ingredient_row,
                    ingredientsContainer, false);

            TextView statusTxt = row.findViewById(R.id.ingredientStatus);
            TextView nameTxt   = row.findViewById(R.id.ingredientName);
            TextView measureTxt = row.findViewById(R.id.ingredientMeasure);

            boolean haveIt = pantrySet.contains(ing.toLowerCase(Locale.ROOT).trim());

            if (haveIt) {
                statusTxt.setText("✓");
                statusTxt.setTextColor(0xFF16A34A); // green
                nameTxt.setTextColor(0xFF111827);
            } else {
                statusTxt.setText("✗");
                statusTxt.setTextColor(0xFFDC2626); // red
                nameTxt.setTextColor(0xFFDC2626);
            }

            nameTxt.setText(ing);
            measureTxt.setText(measure == null ? "" : measure);

            ingredientsContainer.addView(row);
        }
    }
}