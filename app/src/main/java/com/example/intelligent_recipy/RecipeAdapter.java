package com.example.intelligent_recipy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.intelligent_recipy.models.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Item types
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_RECIPE = 1;

    /** A single row — either a section header or a recipe card. */
    public static class Row {
        final int type;
        final String headerText;
        final Recipe recipe;
        final String missingIngredient; // null for full matches

        private Row(int type, String headerText, Recipe recipe, String missingIngredient) {
            this.type = type;
            this.headerText = headerText;
            this.recipe = recipe;
            this.missingIngredient = missingIngredient;
        }

        public static Row header(String text) {
            return new Row(TYPE_HEADER, text, null, null);
        }

        public static Row recipe(Recipe recipe, String missing) {
            return new Row(TYPE_RECIPE, null, recipe, missing);
        }
    }

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private final List<Row> rows;
    private final OnRecipeClickListener listener;

    public RecipeAdapter(List<Row> rows, OnRecipeClickListener listener) {
        this.rows = rows;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.item_recipe, parent, false);
            return new RecipeViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Row row = rows.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).text.setText(row.headerText);
        } else if (holder instanceof RecipeViewHolder) {
            ((RecipeViewHolder) holder).bind(row.recipe, row.missingIngredient, listener);
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    // ---------- ViewHolders ----------

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        final TextView text;
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(android.R.id.text1);
            text.setTextSize(18f);
            text.setTextColor(0xFF111827);
            text.setPadding(24, 40, 24, 16);
            text.setTypeface(null, android.graphics.Typeface.BOLD);
        }
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView name;
        final TextView category;
        final TextView missing;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            image    = itemView.findViewById(R.id.recipeImage);
            name     = itemView.findViewById(R.id.recipeName);
            category = itemView.findViewById(R.id.recipeCategory);
            missing  = itemView.findViewById(R.id.recipeMissing);
        }

        void bind(final Recipe recipe, String missingIngredient,
                  final OnRecipeClickListener listener) {
            name.setText(recipe.getName());
            category.setText(recipe.getCategory() + " · " + recipe.getArea());

            Context ctx = itemView.getContext();
            Glide.with(ctx)
                    .load(recipe.getImage())
                    .placeholder(android.R.color.darker_gray)
                    .centerCrop()
                    .into(image);

            if (missingIngredient != null && !missingIngredient.isEmpty()) {
                missing.setVisibility(View.VISIBLE);
                missing.setText("Missing: " + missingIngredient);
            } else {
                missing.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (listener != null) listener.onRecipeClick(recipe);
                }
            });
        }
    }
}