package com.moran.spiceitapp.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moran.spiceitapp.MyApplication;
import com.moran.spiceitapp.R;
import com.moran.spiceitapp.model.Model;
import com.moran.spiceitapp.model.StorageFirebase;
import com.moran.spiceitapp.model.User;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Date;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment {

    private EditProfileViewModel editProfileViewModel;
    LiveData<User> liveData;
    User user = new User();
    TextView location_textView, about_textView;
    Bitmap imageBitmap;
    String imageUrl;
    ImageView photo_imageView;
    View uploadPhotoBtn, saveBtn;
    ProgressBar progressBar;
    Boolean isPhotoUploaded;

    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        editProfileViewModel = ViewModelProviders.of(this).get(EditProfileViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        location_textView = root.findViewById(R.id.edit_location_textInputEditText);
        about_textView = root.findViewById(R.id.edit_about_textInputEditText);
        photo_imageView = root.findViewById(R.id.edit_photo_imageView);
        uploadPhotoBtn = root.findViewById(R.id.edit_photo_btn);
        saveBtn = root.findViewById(R.id.edit_save_btn);
        progressBar = root.findViewById(R.id.editProfile_progressBar);
        isPhotoUploaded = false;

        liveData = editProfileViewModel.getData();
        liveData.observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User _user) {
                setProfileLayout(_user);
            }
        });

        //upload photo
        uploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto();
            }
        });

        //save changes
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                saveChanges(v);
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        saveBtn.setEnabled(true);
    }

    public void setProfileLayout(User _user){
        user = _user;
        location_textView.setText(user.location);
        about_textView.setText(user.about);
        if (user.getImageUrl() != null && !(user.getImageUrl().equals("")))
            Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.blank_icon).into(photo_imageView);
        else
            photo_imageView.setImageResource(R.drawable.ic_menu_person);
    }

    public void saveChanges(final View v){
        final String email = user.getEmail();
        final String name = user.getName();
        final String location = location_textView.getText().toString();
        final String about = about_textView.getText().toString();

        if (TextUtils.isEmpty(location)) {
            location_textView.setError("Location is Required");
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        saveBtn.setEnabled(false);

        if (isPhotoUploaded) {
            Date date = new Date();
            StorageFirebase.uploadImage(imageBitmap, "photo_" + date.getTime(), new StorageFirebase.Listener() {
                @Override
                public void onSuccess(String url) {
                    imageUrl = url;
                    User updatedUser = new User(email, name, location, about, imageUrl);
                    editProfileViewModel.updateData(updatedUser).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            Navigation.findNavController(v).popBackStack(); //back to profile
                        }
                    });
                }
                @Override
                public void onFail() {}
            });
        } else {
            imageUrl = user.getImageUrl();
            User updatedUser = new User(email, name, location, about, imageUrl);
            editProfileViewModel.updateData(updatedUser).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    Navigation.findNavController(v).popBackStack(); //back to profile
                }
            });
        }
    }

    /********** PHOTO **********/
    static final int REQUEST_PICK_IMAGE = 1;

    void uploadPhoto(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (pickPhoto.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            isPhotoUploaded = true;

            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(
                        MyApplication.context.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            photo_imageView.setImageBitmap(imageBitmap);
        }
    }
}