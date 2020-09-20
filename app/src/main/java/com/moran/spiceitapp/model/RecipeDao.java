package com.moran.spiceitapp.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecipeDao {
    @Query("select * from Recipe where isDeleted = :notDeleted")
    List<Recipe> getAll(String notDeleted);

    @Query("select * from Recipe where creatorEmail = :userEmail and isDeleted = :notDeleted")
    List<Recipe> getMyRecipes(String userEmail, String notDeleted);

    @Query("select * from Recipe where id = :id")
    Recipe getRecipe(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Recipe recipe);

    @Delete
    void delete(Recipe recipe);
}
