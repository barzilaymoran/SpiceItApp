package com.moran.spiceitapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.moran.spiceitapp.model.Model;
import com.moran.spiceitapp.model.Recipe;

import java.util.List;

public class HomeViewModel extends ViewModel {
    LiveData<List<Recipe>> liveData;

    public LiveData<List<Recipe>> getData() {
        liveData = Model.instance.getAllRecipes();
        return liveData;
    }
}