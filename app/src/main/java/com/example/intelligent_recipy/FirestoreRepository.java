package com.example.intelligent_recipy;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.intelligent_recipy.models.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreRepository {

    private static final String TAG = "FirestoreRepo";
    private static final String COLLECTION = "community_recipes";

    private final FirebaseFirestore db;

    public FirestoreRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public interface OnRecipesLoaded {
        void onLoaded(List<Recipe> recipes);
        void onError(String message);
    }

    public interface OnRecipeSaved {
        void onSaved();
        void onError(String message);
    }

    /** Fetch all community recipes, newest first. */
    public void loadCommunityRecipes(final OnRecipesLoaded callback) {
        db.collection(COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(100)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snapshot) {
                        List<Recipe> recipes = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshot) {
                            Recipe r = doc.toObject(Recipe.class);
                            if (r.getId() == null) r.setId(doc.getId());
                            r.setFromCommunity(true);
                            recipes.add(r);
                        }
                        callback.onLoaded(recipes);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Load failed", e);
                        callback.onError(e.getMessage() == null
                                ? "Couldn't load recipes" : e.getMessage());
                    }
                });
    }

    /** Save a new recipe to the community collection. */
    public void saveRecipe(Recipe recipe, final OnRecipeSaved callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", recipe.getName());
        data.put("category", recipe.getCategory());
        data.put("area", recipe.getArea());
        data.put("image", recipe.getImage());
        data.put("ingredients", recipe.getIngredients());
        data.put("measures", recipe.getMeasures());
        data.put("instructions", recipe.getInstructions());
        data.put("authorName", recipe.getAuthorName());
        data.put("createdAt", System.currentTimeMillis());

        db.collection(COLLECTION)
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<com.google.firebase.firestore.DocumentReference>() {
                    @Override
                    public void onSuccess(com.google.firebase.firestore.DocumentReference ref) {
                        callback.onSaved();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Save failed", e);
                        callback.onError(e.getMessage() == null
                                ? "Couldn't save recipe" : e.getMessage());
                    }
                });
    }
}
