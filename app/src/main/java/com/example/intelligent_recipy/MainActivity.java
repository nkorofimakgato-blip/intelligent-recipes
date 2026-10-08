package com.example.intelligent_recipy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CAMERA = 2001;

    private EditText ingredientInput;
    private Button addButton;
    private Button findRecipesButton;
    private Button surpriseMeButton;
    private ImageButton cameraButton;
    private ChipGroup chipGroup;
    private TextView emptyMessage;

    private final List<String> pantry = new ArrayList<>();

    private RecipeRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ingredientInput    = findViewById(R.id.ingredientInput);
        addButton          = findViewById(R.id.addButton);
        findRecipesButton  = findViewById(R.id.findRecipesButton);
        surpriseMeButton   = findViewById(R.id.surpriseMeButton);
        cameraButton       = findViewById(R.id.cameraButton);
        chipGroup          = findViewById(R.id.chipGroup);
        emptyMessage       = findViewById(R.id.emptyMessage);

        repository = new RecipeRepository(this);
        Log.d(TAG, "Loaded " + repository.size() + " recipes");

        updateEmptyMessage();

        findViewById(R.id.clearAllButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pantry.clear();
                chipGroup.removeAllViews();
                updateEmptyMessage();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { addIngredientFromInput(); }
        });

        findRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { findRecipes(false); }
        });

        surpriseMeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { findRecipes(true); }
        });

        // ▼ Camera button
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Camera button tapped");
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });
    }

    private void addIngredientFromInput() {
        String raw = ingredientInput.getText().toString().trim();
        if (raw.isEmpty()) return;

        String normalized = raw.toLowerCase();

        if (pantry.contains(normalized)) {
            Toast.makeText(this, "\"" + raw + "\" is already in your list",
                    Toast.LENGTH_SHORT).show();
            ingredientInput.setText("");
            return;
        }

        pantry.add(normalized);
        addChip(normalized);
        ingredientInput.setText("");
        updateEmptyMessage();
    }

    private void addChip(final String ingredient) {
        Chip chip = new Chip(this);
        chip.setText(ingredient);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setClickable(false);

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pantry.remove(ingredient);
                chipGroup.removeView(chip);
                updateEmptyMessage();
            }
        });

        chipGroup.addView(chip);
    }

    private void updateEmptyMessage() {
        emptyMessage.setVisibility(pantry.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void findRecipes(boolean surpriseMe) {
        if (pantry.isEmpty()) {
            Toast.makeText(this, "Add at least one ingredient first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
        intent.putStringArrayListExtra(
                ResultsActivity.EXTRA_PANTRY,
                new ArrayList<>(pantry));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA
                && resultCode == RESULT_OK
                && data != null) {

            String ingredient = data.getStringExtra(CameraActivity.EXTRA_RESULT);
            if (ingredient != null && !ingredient.trim().isEmpty()) {
                String normalized = ingredient.toLowerCase().trim();
                if (pantry.contains(normalized)) {
                    Toast.makeText(this,
                            "\"" + normalized + "\" is already in your list",
                            Toast.LENGTH_SHORT).show();
                } else {
                    pantry.add(normalized);
                    addChip(normalized);
                    updateEmptyMessage();
                }
            }
        }
    }
}