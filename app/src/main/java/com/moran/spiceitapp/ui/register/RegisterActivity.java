package com.moran.spiceitapp.ui.register;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.moran.spiceitapp.MainActivity;
import com.moran.spiceitapp.MyApplication;
import com.moran.spiceitapp.R;
import com.moran.spiceitapp.auth.AuthManager;
import com.moran.spiceitapp.auth.Auth;
import com.moran.spiceitapp.model.Model;
import com.moran.spiceitapp.model.StorageFirebase;
import com.moran.spiceitapp.model.User;
import com.moran.spiceitapp.ui.myRecipes.AddRecipeViewModel;

import java.io.IOException;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {
    public static EditText email_editText;
    EditText password_editText, name_editText, location_editText, about_editText;
    ProgressBar progressBar;
    RegisterViewModel registerViewModel;
    Boolean flag = false;
    ImageView photo_imageView;
    Bitmap imageBitmap;
    String imageUrl;
    Boolean isPhotoUploaded;
    View registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);

        email_editText = findViewById(R.id.register_email_editText);
        password_editText = findViewById(R.id.register_password_editText);
        name_editText = findViewById(R.id.register_name_editText);
        location_editText = findViewById(R.id.register_location_editText);
        about_editText = findViewById(R.id.register_about_editText);
        photo_imageView = findViewById(R.id.register_upload_photo_imageView);
        progressBar = findViewById(R.id.register_progressBar);
        registerBtn = findViewById(R.id.register_register_btn);
        isPhotoUploaded = false;

        //upload photo
        View uploadPhotoBtn = findViewById(R.id.register_upload_photo_btn);
        uploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        flag = false;
        progressBar.setVisibility(View.GONE);
        registerBtn.setEnabled(true);
    }

    public void onRegister(View view) {
        final String email = email_editText.getText().toString();
        final String password = password_editText.getText().toString();
        final String name = name_editText.getText().toString();
        final String location = location_editText.getText().toString();
        final String about = about_editText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            email_editText.setError("Email is Required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            password_editText.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            password_editText.setError(("Password must be >= 6 characters"));
            return;
        }

        if (TextUtils.isEmpty(name)) {
            name_editText.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(location)) {
            location_editText.setError("Location is required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        registerBtn.setEnabled(false);


        if (isPhotoUploaded) {
            Date date = new Date();
            StorageFirebase.uploadImage(imageBitmap, "photo_" + date.getTime(), new StorageFirebase.Listener() {
                @Override
                public void onSuccess(String url) {
                    imageUrl = url;
                    final User user = new User(email, name, location, about, imageUrl);
                    AuthManager.instance.register(email, password, new Auth.CompletionHandler() {
                        @Override
                        public void onComplete(@Nullable Exception e) {
                            if (e == null) {
                                registerViewModel.addData(user).observe(RegisterActivity.this, new Observer<User>() {
                                    @Override
                                    public void onChanged(User user) {
                                        if (user != null) {
                                            registerViewModel.setLoggedInUser(user);
                                            progressBar.setVisibility(View.GONE);
                                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        }
                                    }
                                });
                            } else {
                                RegisterActivity.email_editText.setError(e.getMessage());
                                progressBar.setVisibility(View.GONE);
                                registerBtn.setEnabled(true);
                            }
                        }
                    });
                }

                @Override
                public void onFail() {}
            });
        } else {
            imageUrl = "";
            final User user = new User(email, name, location, about, imageUrl);
            AuthManager.instance.register(email, password, new Auth.CompletionHandler() {
                @Override
                public void onComplete(@Nullable Exception e) {
                    if (e == null) {
                        registerViewModel.addData(user).observe(RegisterActivity.this, new Observer<User>() {
                            @Override
                            public void onChanged(User user) {
                                if (user != null) {
                                    registerViewModel.setLoggedInUser(user);
                                    progressBar.setVisibility(View.GONE);
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                }
                            }
                        });
                    } else {
                        RegisterActivity.email_editText.setError(e.getMessage());
                        progressBar.setVisibility(View.GONE);
                        registerBtn.setEnabled(true);
                    }
                }
            });
        }
    }

    /************* PHOTO **************/
    final static int REQUEST_PICK_IMAGE = 1;

    void uploadPhoto() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (pickPhoto.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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