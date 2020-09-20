package com.moran.spiceitapp.ui.myRecipes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.moran.spiceitapp.auth.AuthManager;
import com.moran.spiceitapp.model.Model;
import com.moran.spiceitapp.model.Recipe;
import com.moran.spiceitapp.model.User;

public class AddRecipeViewModel extends ViewModel {

    public LiveData<Recipe> addRecipe(Recipe recipe){
        final MutableLiveData<Recipe> liveData = new MutableLiveData<>();
        Model.instance.addRecipe(recipe, new Model.Listener<Recipe>() {
            @Override
            public void onComplete(Recipe data) {
                liveData.setValue(data);
            }
        });
        return liveData;
    }

    public User getCurrentUser(){
         return Model.instance.getLoggedInUser();
    }
}