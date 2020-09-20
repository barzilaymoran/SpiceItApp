package com.moran.spiceitapp.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moran.spiceitapp.R;
import com.moran.spiceitapp.model.User;
import com.moran.spiceitapp.ui.home.HomeFragment;
import com.squareup.picasso.Picasso;

public class MyProfileFragment extends Fragment {

    private MyProfileViewModel myProfileViewModel;
    LiveData<User> liveData;
    User user;
    View root, separator;
    TextView name_textView, location_textView, about_textView, recipes_textView;
    ImageView photo_imageView;

    public static ProfileFragment newInstance() { return new ProfileFragment(); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) { //can be called few times

        myProfileViewModel = ViewModelProviders.of(this).get(MyProfileViewModel.class);
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        name_textView = root.findViewById(R.id.profile_name_textView);
        location_textView = root.findViewById(R.id.profile_location_textView);
        about_textView = root.findViewById(R.id.profile_about_textView);
        photo_imageView = root.findViewById(R.id.profile_image_imageView);
        recipes_textView = root.findViewById(R.id.recipes_textView);
        separator = root.findViewById(R.id.separator_top_view);

        recipes_textView.setVisibility(View.GONE);
        separator.setVisibility(View.GONE);

        liveData = myProfileViewModel.getData();
        liveData.observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User _user) {
                setProfileLayout(_user);
            }
        });

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
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

    /*********** MENU ************/
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.my_profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.my_profile_action_edit:
                Navigation.findNavController(root).navigate(R.id.action_nav_profile_to_editProfileFragment);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}