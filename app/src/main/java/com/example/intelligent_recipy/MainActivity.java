package com.example.intelligent_recipy;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intelligent_recipy.models.Meal;
import com.example.intelligent_recipy.models.MealResponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText searchInput;
    private Button searchButton;

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchInput.getText().toString().trim();
                if (query.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Please type what you want to cook",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                searchRecipes(query);
            }
        });
    }

    private void searchRecipes(final String query) {
        // Disable button so we don't fire multiple requests
        searchButton.setEnabled(false);
        searchButton.setText("Searching...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "https://www.themealdb.com/api/json/v1/1/search.php?s=" + query;

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful() || response.body() == null) {
                        throw new IOException("HTTP " + response.code());
                    }

                    String json = response.body().string();
                    Log.d(TAG, "Raw response: " + json);

                    final MealResponse mealResponse =
                            gson.fromJson(json, MealResponse.class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            restoreButton();

                            if (mealResponse == null
                                    || mealResponse.getMeals() == null
                                    || mealResponse.getMeals().isEmpty()) {
                                Toast.makeText(MainActivity.this,
                                        "No recipes found for \"" + query + "\"",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            List<Meal> meals = mealResponse.getMeals();
                            Log.d(TAG, "Found " + meals.size() + " recipes");

                            for (Meal meal : meals) {
                                Log.d(TAG, "→ " + meal.getStrMeal()
                                        + " (" + meal.getStrCategory()
                                        + ", " + meal.getStrArea() + ")");
                            }

                            // Temporary feedback — results screen comes next
                            Toast.makeText(MainActivity.this,
                                    "Found " + meals.size() + " recipe(s). Check Logcat.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (final Exception e) {
                    Log.e(TAG, "Search failed", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            restoreButton();
                            Toast.makeText(MainActivity.this,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void restoreButton() {
        searchButton.setEnabled(true);
        searchButton.setText("Search");
    }
}