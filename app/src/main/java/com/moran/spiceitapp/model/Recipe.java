package com.moran.spiceitapp.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
public class Recipe implements Serializable {
    @PrimaryKey
    @NonNull
    public String id;
    public String title;
    public String ingredients;
    public String instructions;
    public String imageUrl;
    public String creatorName;
    public String creatorEmail;
    public long lastUpdated;
    public String isDeleted;

    public Recipe() {}

    public Recipe(String title, String ingredients, String instructions, String imageUrl, String creatorName, String creatorEmail) {
        this.title = title;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
        this.creatorName = creatorName;
        this.creatorEmail = creatorEmail;
        this.isDeleted = "false";
    }

    public  void setId(String id){ this.id = id; };
    public void setTitle(String title) {
        this.title = title;
    }
    public void setIsDeleted(String deleted) {this.isDeleted = deleted; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getId() { return this.id; }
    public String getTitle() { return this.title; }
    public String getIngredients() { return this.ingredients; }
    public String getInstructions() { return this.instructions; }
    public String getImageUrl() { return this.imageUrl; }
    public String getIsDeleted() { return this.isDeleted; }
    public String getCreatorName() { return this.creatorName; }
    public String getCreatorEmail() { return this.creatorEmail; }


    public Map<String, Object> toJson() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("title", title);
        result.put("ingredients", ingredients);
        result.put("instructions", instructions);
        result.put("imageUrl", imageUrl);
        result.put("creatorName", creatorName);
        result.put("creatorEmail", creatorEmail);
        result.put("lastUpdated", FieldValue.serverTimestamp());
        result.put("isDeleted", isDeleted);
        return result;
    }

    static Recipe factory(Map<String, Object> json){
        Recipe recipe = new Recipe();
        recipe.id = (String) Objects.requireNonNull(json.get("id"));
        recipe.title = (String)json.get("title");
        recipe.ingredients = (String)json.get("ingredients");
        recipe.instructions = (String)json.get("instructions");
        recipe.imageUrl = (String)json.get("imageUrl");
        recipe.creatorName = (String)json.get("creatorName");
        recipe.creatorEmail = (String)json.get("creatorEmail");
        Timestamp ts = (Timestamp)json.get("lastUpdated");
        assert ts != null;
        recipe.lastUpdated = ts.getSeconds();
        recipe.isDeleted = (String)json.get("isDeleted");
        return recipe;
    }

    @Override
    public boolean equals(Object obj) {
        return (this.getId().equals(((Recipe)obj).getId())) ;
    }
}
