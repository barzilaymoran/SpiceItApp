package com.moran.spiceitapp.ui.profile;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moran.spiceitapp.R;
import com.moran.spiceitapp.model.User;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    LiveData<User> liveData;
    User user;
    String userEmail;
    ImageView photo_imageView;
    View root;
    TextView name_textView, location_textView, about_textView;

    public static ProfileFragment newInstance() { return new ProfileFragment(); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ProfileViewModel profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        name_textView = root.findViewById(R.id.profile_name_textView);
        location_textView = root.findViewById(R.id.profile_location_textView);
        about_textView = root.findViewById(R.id.profile_about_textView);
        photo_imageView = root.findViewById(R.id.profile_image_imageView);

        assert getArguments() != null;
        userEmail = ProfileFragmentArgs.fromBundle(getArguments()).getCreatorEmail();

        liveData = profileViewModel.getData(userEmail);
        liveData.observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User _user) {
                setProfileLayout(_user);
            }
        });

        Fragment listFragment = new ProfileRecipesFragment(userEmail);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.child_fragment_contaner, listFragment).commit();

        return root;
    }

    public void setProfileLayout(User _user){
        user = _user;
        name_textView.setText(user.name);
        location_textView.setText(user.location);
        about_textView.setText(user.about);
        if(user.getImageUrl() != null && !(user.getImageUrl().equals("")))
            Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.blank_icon).into(photo_imageView);
        else
            photo_imageView.setImageResource(R.drawable.ic_menu_person);
    }
}