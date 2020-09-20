package com.moran.spiceitapp.ui.myRecipes;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.moran.spiceitapp.MyApplication;
import com.moran.spiceitapp.R;
import com.moran.spiceitapp.model.Recipe;
import com.moran.spiceitapp.model.StorageFirebase;
import com.moran.spiceitapp.model.User;

import java.io.IOException;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class AddRecipeFragment extends Fragment {

    private AddRecipeViewModel addRecipeViewModel;
    EditText title_editText, ingredients_editText, instructions_editText;
    User currentUser;
    ImageView photo_imageView;
    Bitmap imageBitmap;
    String imageUrl;
    Boolean isPhotoUploaded;
    ProgressBar progressBar;
    View shareBtn, uploadPhotoBtn;


    public static AddRecipeFragment newInstance() {
        return new AddRecipeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        addRecipeViewModel = ViewModelProviders.of(this).get(AddRecipeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_add_recipe, container, false);

        title_editText = root.findViewById(R.id.addRecipe_title_editText);
        ingredients_editText = root.findViewById(R.id.addRecipe_ingredients_editView);
        instructions_editText = root.findViewById(R.id.addRecipe_instructions_editView);
        photo_imageView = root.findViewById(R.id.addRecipe_photo_imageView);
        progressBar = root.findViewById(R.id.addRecipe_progressBar);
        uploadPhotoBtn = root.findViewById(R.id.addRecipe_photo_btn);
        shareBtn = root.findViewById(R.id.addRecipe_share_btn);
        isPhotoUploaded=false;

        //upload photo
        uploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto();
            }
        });

        //share recipe
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String title = title_editText.getText().toString();
                final String ingredients = ingredients_editText.getText().toString();
                final String instructions = instructions_editText.getText().toString();
                currentUser = addRecipeViewModel.getCurrentUser();
                final String creatorName = currentUser.getName();
                final String creatorEmail = currentUser.getEmail();

                if (TextUtils.isEmpty(title)) {
                    title_editText.setError("Title is Required");
                    return;
                }

                if (TextUtils.isEmpty(ingredients)) {
                    ingredients_editText.setError("Ingredients are Required");
                    return;
                }

                if (TextUtils.isEmpty(instructions)) {
                    instructions_editText.setError("Instructions are Required");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                shareBtn.setEnabled(false);

                if (isPhotoUploaded) {
                    Date date = new Date();
                    StorageFirebase.uploadImage(imageBitmap, "photo_" + date.getTime(), new StorageFirebase.Listener() {
                        @Override
                        public void onSuccess(String url) {
                            imageUrl = url;
                            Recipe recipe = new Recipe(title, ingredients, instructions, imageUrl, creatorName, creatorEmail);
                            addRecipeViewModel.addRecipe(recipe).observe(getViewLifecycleOwner(), new Observer<Recipe>() {
                                @Override
                                public void onChanged(Recipe recipe) {
                                    if (recipe != null) {
                                        Navigation.findNavController(v).popBackStack(); //back to my recipes
                                    }
                                }
                            });
                        }
                        @Override
                        public void onFail() {
                            imageUrl = "";
                        }
                    });
                } else {
                    imageUrl = "";
                    Recipe recipe = new Recipe(title, ingredients, instructions, imageUrl, creatorName, creatorEmail);
                    addRecipeViewModel.addRecipe(recipe).observe(getViewLifecycleOwner(), new Observer<Recipe>() {
                        @Override
                        public void onChanged(Recipe recipe) {
                            if (recipe != null) {
                                Navigation.findNavController(v).popBackStack(); //back to my recipes
                            }
                        }
                    });
                }
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        shareBtn.setEnabled(true);
    }

    /************ PHOTO *************/

    static final int REQUEST_PICK_IMAGE = 1;

    void uploadPhoto(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (pickPhoto.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { //on return from gallery
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            isPhotoUploaded = true;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(MyApplication.context.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            photo_imageView.setImageBitmap(imageBitmap);
        }
    }
}