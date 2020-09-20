package com.moran.spiceitapp.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.moran.spiceitapp.auth.AuthManager;
import com.moran.spiceitapp.model.Model;
import com.moran.spiceitapp.model.User;

public class ProfileViewModel extends ViewModel {
    LiveData<User> liveData;

    public ProfileViewModel() {}

    public LiveData<User> getData(String userEmail){
        liveData = Model.instance.getUser(userEmail);
        return liveData;
    }
}