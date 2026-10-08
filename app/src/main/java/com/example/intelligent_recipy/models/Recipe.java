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

    // Firebase-only fields
    private String authorName;
    private long createdAt;
    private boolean fromCommunity;

    // Required empty constructor for Firestore
    public Recipe() {}

    public String getId()              { return id; }
    public String getName()            { return name; }
    public String getCategory()        { return category; }
    public String getArea()            { return area; }
    public String getImage()           { return image; }
    public List<String> getIngredients() { return ingredients; }
    public List<String> getMeasures()  { return measures; }
    public String getInstructions()    { return instructions; }

    public String getAuthorName()      { return authorName; }
    public long getCreatedAt()         { return createdAt; }
    public boolean isFromCommunity()   { return fromCommunity; }

    // Setters for Firebase deserialization + building from form
    public void setId(String id)                      { this.id = id; }
    public void setName(String name)                  { this.name = name; }
    public void setCategory(String category)          { this.category = category; }
    public void setArea(String area)                  { this.area = area; }
    public void setImage(String image)                { this.image = image; }
    public void setIngredients(List<String> list)     { this.ingredients = list; }
    public void setMeasures(List<String> list)        { this.measures = list; }
    public void setInstructions(String instructions)  { this.instructions = instructions; }
    public void setAuthorName(String authorName)      { this.authorName = authorName; }
    public void setCreatedAt(long createdAt)          { this.createdAt = createdAt; }
    public void setFromCommunity(boolean flag)        { this.fromCommunity = flag; }
}