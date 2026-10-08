package com.example.intelligent_recipy.models;

import java.util.List;

public class Recipe {
    private String id;
    private String name;
    private String category;
    private String area;
    private String image;
    private List<String> ingredients;
    private List<String> measures;
    private String instructions;

    public String getId()              { return id; }
    public String getName()            { return name; }
    public String getCategory()        { return category; }
    public String getArea()            { return area; }
    public String getImage()           { return image; }
    public List<String> getIngredients() { return ingredients; }
    public List<String> getMeasures()  { return measures; }
    public String getInstructions()    { return instructions; }
}