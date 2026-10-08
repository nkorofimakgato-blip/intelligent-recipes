package com.example.intelligent_recipy;

import com.example.intelligent_recipy.models.Recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RecipeMatcher {

    /** A recipe the user can make right now — nothing missing. */
    public static class FullMatch {
        public final Recipe recipe;
        public FullMatch(Recipe recipe) { this.recipe = recipe; }
    }

    /** A recipe missing exactly one ingredient. */
    public static class AlmostMatch {
        public final Recipe recipe;
        public final String missingIngredient;
        public AlmostMatch(Recipe recipe, String missingIngredient) {
            this.recipe = recipe;
            this.missingIngredient = missingIngredient;
        }
    }

    /** Normalize: lowercase + trim, for comparison. */
    private static String norm(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT).trim();
    }

    /**
     * Given what the user has, find recipes where EVERY ingredient is present.
     */
    public static List<FullMatch> findFullMatches(
            List<Recipe> allRecipes, List<String> userHas) {

        Set<String> pantry = new HashSet<>();
        for (String item : userHas) pantry.add(norm(item));

        List<FullMatch> results = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            if (recipe.getIngredients() == null) continue;

            boolean allPresent = true;
            for (String ingredient : recipe.getIngredients()) {
                if (!pantry.contains(norm(ingredient))) {
                    allPresent = false;
                    break;
                }
            }
            if (allPresent) {
                results.add(new FullMatch(recipe));
            }
        }
        return results;
    }

    /**
     * Given what the user has, find recipes missing exactly ONE ingredient.
     */
    public static List<AlmostMatch> findAlmostMatches(
            List<Recipe> allRecipes, List<String> userHas) {

        Set<String> pantry = new HashSet<>();
        for (String item : userHas) pantry.add(norm(item));

        List<AlmostMatch> results = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            if (recipe.getIngredients() == null) continue;

            String missing = null;
            int missingCount = 0;

            for (String ingredient : recipe.getIngredients()) {
                if (!pantry.contains(norm(ingredient))) {
                    missingCount++;
                    missing = ingredient;
                    if (missingCount > 1) break;
                }
            }

            if (missingCount == 1) {
                results.add(new AlmostMatch(recipe, missing));
            }
        }
        return results;
    }
}
