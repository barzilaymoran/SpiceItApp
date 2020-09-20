package com.moran.spiceitapp.model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.moran.spiceitapp.MyApplication;

    @Database(entities = {User.class, Recipe.class}, version = 1)
    abstract class AppLocalDbRepository extends RoomDatabase {
        public abstract UserDao userDao();
        public abstract RecipeDao recipeDao();
    }
    public class AppLocalDb {
        static public AppLocalDbRepository db =
                Room.databaseBuilder(MyApplication.context,
                        AppLocalDbRepository.class,
                        "dbFileName.db")
                        .fallbackToDestructiveMigration()
                        .build();
    }

