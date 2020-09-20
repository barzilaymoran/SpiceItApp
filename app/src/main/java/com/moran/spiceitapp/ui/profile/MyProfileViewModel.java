package com.moran.spiceitapp.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.moran.spiceitapp.auth.AuthManager;
import com.moran.spiceitapp.model.Model;
import com.moran.spiceitapp.model.User;

public class MyProfileViewModel extends ViewModel {
    LiveData<User> liveData;

    public MyProfileViewModel() {}

    public LiveData<User> getData(){
        liveData = Model.instance.getUser(AuthManager.instance.getLoggedInUserEmail());
        return liveData;
    }
}