package com.moran.spiceitapp.ui.myRecipes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.moran.spiceitapp.model.Model;
import com.moran.spiceitapp.model.Recipe;

import java.util.List;

public class MyRecipesViewModel extends ViewModel {
    LiveData<List<Recipe>> liveData;

    public LiveData<List<Recipe>> getData() {
        liveData = Model.instance.getMyRecipes(Model.instance.getLoggedInUser().getEmail());
        return liveData;
    }
}