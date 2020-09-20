package com.moran.spiceitapp.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.moran.spiceitapp.auth.AuthManager;
import com.moran.spiceitapp.model.Model;
import com.moran.spiceitapp.model.User;

public class EditProfileViewModel extends ViewModel {
    LiveData<User> liveData;

    public EditProfileViewModel() {}

    public LiveData<User> getData(){
        liveData = Model.instance.getUser(AuthManager.instance.getLoggedInUserEmail());
        return liveData;
    }

    public LiveData<Boolean> updateData(User user){
        final MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        Model.instance.updateUser(user, new Model.Listener<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
                liveData.setValue(data);
            }
        });

        return liveData;
    }
}