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
    /** A recipe paired with its pantry-coverage score. */
    public static class Recommendation {
        public final Recipe recipe;
        public final int matchedCount;
        public final int totalCount;
        public Recommendation(Recipe recipe, int matchedCount, int totalCount) {
            this.recipe = recipe;
            this.matchedCount = matchedCount;
            this.totalCount = totalCount;
        }
        public int getPercent() {
            if (totalCount == 0) return 0;
            return (int) Math.round(100.0 * matchedCount / totalCount);
        }
    }

    /**
     * Returns recipes sorted by how many ingredients the user already has
     * (descending), then shuffled a bit within the same score tier.
     * Excludes recipes with zero matches (irrelevant).
     */
    public static List<Recommendation> findRecommendations(
            List<Recipe> allRecipes, List<String> userHas) {

        Set<String> pantry = new HashSet<>();
        for (String item : userHas) pantry.add(norm(item));

        List<Recommendation> results = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            if (recipe.getIngredients() == null) continue;

            int matched = 0;
            int total = 0;
            for (String ingredient : recipe.getIngredients()) {
                if (ingredient == null || ingredient.trim().isEmpty()) continue;
                total++;
                if (pantry.contains(norm(ingredient))) matched++;
            }

            if (matched == 0) continue; // skip recipes with no overlap

            results.add(new Recommendation(recipe, matched, total));
        }

        // Sort by score descending, then randomly within equal scores
        java.util.Random rand = new java.util.Random();
        results.sort((a, b) -> {
            int cmp = Integer.compare(b.matchedCount, a.matchedCount);
            if (cmp != 0) return cmp;
            return Integer.compare(rand.nextInt(1000), rand.nextInt(1000));
        });

        return results;
    }
}