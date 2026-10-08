package com.example.intelligent_recipy;

import com.example.intelligent_recipy.models.Recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RecipeMatcher {

    public static class FullMatch {
        public final Recipe recipe;
        public FullMatch(Recipe recipe) { this.recipe = recipe; }
    }

    public static class AlmostMatch {
        public final Recipe recipe;
        public final String missingIngredient;
        public AlmostMatch(Recipe recipe, String missingIngredient) {
            this.recipe = recipe;
            this.missingIngredient = missingIngredient;
        }
    }

    private static String norm(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT).trim();
    }

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

    public static List<AlmostMatch> findAlmostMatches(
            List<Recipe> allRecipes, List<String> userHas) {

        Set<String> pantry = new HashSet<>();
        for (String item : userHas) pantry.add(norm(item));

        List<AlmostMatch> results = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            if (recipe.getIngredients() == null) continue;

            String lastMissing = null;
            int missingCount = 0;

            for (String ingredient : recipe.getIngredients()) {
                if (!pantry.contains(norm(ingredient))) {
                    missingCount++;
                    lastMissing = ingredient;
                    if (missingCount > 2) break;
                }
            }

            if (missingCount >= 1 && missingCount <= 2) {
                results.add(new AlmostMatch(recipe, lastMissing));
            }
        }
        return results;
    }
}