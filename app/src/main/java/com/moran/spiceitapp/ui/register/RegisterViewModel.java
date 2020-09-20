package com.moran.spiceitapp.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.moran.spiceitapp.model.Model;
import com.moran.spiceitapp.model.Recipe;
import com.moran.spiceitapp.model.User;

public class RegisterViewModel extends ViewModel {
    public RegisterViewModel() {}

    public LiveData<User> addData(User user){
        final MutableLiveData<User> liveData = new MutableLiveData<>();
        Model.instance.addUser(user, new Model.Listener<User>() {
            @Override
            public void onComplete(User data) {
                liveData.setValue(data);
            }
        });
        return liveData;
    }

    public void setLoggedInUser(User user){
        Model.instance.setLoggedInUser(user);
    }
}

