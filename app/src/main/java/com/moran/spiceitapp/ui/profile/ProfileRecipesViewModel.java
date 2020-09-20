package com.moran.spiceitapp.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.moran.spiceitapp.model.Model;
import com.moran.spiceitapp.model.Recipe;

import java.util.List;

public class ProfileRecipesViewModel extends ViewModel {
    LiveData<List<Recipe>> liveData;

    public LiveData<List<Recipe>> getData(String userEmail) {
        liveData = Model.instance.getProfileRecipes(userEmail);
        return liveData;
    }
}
