package com.example.intelligent_recipy.models;

public class Meal {
    private String idMeal;
    private String strMeal;
    private String strCategory;
    private String strArea;
    private String strInstructions;
    private String strMealThumb;

    private String strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5;
    private String strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10;
    private String strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15;
    private String strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20;

    private String strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5;
    private String strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10;
    private String strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15;
    private String strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20;

    // Getters
    public String getIdMeal()          { return idMeal; }
    public String getStrMeal()         { return strMeal; }
    public String getStrCategory()     { return strCategory; }
    public String getStrArea()         { return strArea; }
    public String getStrInstructions() { return strInstructions; }
    public String getStrMealThumb()    { return strMealThumb; }

    // Convenience: array of all 20 ingredients (some may be null or empty)
    public String[] getIngredients() {
        return new String[] {
                strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
                strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
                strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
                strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20
        };
    }

    // Convenience: array of all 20 measurements
    public String[] getMeasures() {
        return new String[] {
                strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5,
                strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10,
                strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15,
                strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20
        };
    }
}