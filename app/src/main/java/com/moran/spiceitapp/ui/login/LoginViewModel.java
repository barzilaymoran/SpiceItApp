package com.moran.spiceitapp.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.moran.spiceitapp.auth.AuthManager;
import com.moran.spiceitapp.model.Model;
import com.moran.spiceitapp.model.User;

public class LoginViewModel extends ViewModel {
    LiveData<User> liveData;

    public LoginViewModel() { }

    public LiveData<User> getData(String email){
        liveData = Model.instance.getUser(email);
        return liveData;
    }

    public void setLoggedInUser(User user){
        Model.instance.setLoggedInUser(user);
    }
}
