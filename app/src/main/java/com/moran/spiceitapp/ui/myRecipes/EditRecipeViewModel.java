package com.moran.spiceitapp.ui.myRecipes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.moran.spiceitapp.model.Model;
import com.moran.spiceitapp.model.Recipe;
import com.moran.spiceitapp.model.User;

public class EditRecipeViewModel extends ViewModel {

    public EditRecipeViewModel() {}

    public LiveData<Boolean> updateData(Recipe recipe){
        final MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        Model.instance.updateRecipe(recipe, new Model.Listener<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
                liveData.setValue(data);
            }
        });

        return liveData;
    }
}