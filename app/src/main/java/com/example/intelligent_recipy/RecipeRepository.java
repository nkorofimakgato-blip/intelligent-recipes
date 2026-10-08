package com.example.intelligent_recipy;

import android.content.Context;

import com.example.intelligent_recipy.models.Recipe;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RecipeRepository {

    private final List<Recipe> recipes = new ArrayList<>();

    public RecipeRepository(Context context) {
        try {
            InputStream is = context.getAssets().open("recipes.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);

            Type listType = new TypeToken<List<Recipe>>() {}.getType();
            List<Recipe> loaded = new Gson().fromJson(json, listType);

            if (loaded != null) {
                recipes.addAll(loaded);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Recipe> getAll() {
        return recipes;
    }

    public int size() {
        return recipes.size();
    }
}