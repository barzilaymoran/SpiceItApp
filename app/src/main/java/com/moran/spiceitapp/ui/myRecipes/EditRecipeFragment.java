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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.moran.spiceitapp.MyApplication;
import com.moran.spiceitapp.R;
import com.moran.spiceitapp.model.Model;
import com.moran.spiceitapp.model.Recipe;
import com.moran.spiceitapp.model.StorageFirebase;
import com.moran.spiceitapp.model.User;
import com.moran.spiceitapp.ui.home.RecipeDetailsFragmentArgs;
import com.moran.spiceitapp.ui.profile.EditProfileViewModel;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class EditRecipeFragment extends Fragment {

    private EditRecipeViewModel editRecipeViewModel;
    Recipe recipe = new Recipe ();
    EditText title_editText, ingredients_editText,  instructions_editText;
    ImageView photo_imageView;
    View saveBtn, uploadPhotoBtn;
    Bitmap imageBitmap;
    String imageUrl;
    ProgressBar progressBar;
    Boolean isPhotoUploaded;


    public static EditRecipeFragment newInstance() {
        return new EditRecipeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        editRecipeViewModel = ViewModelProviders.of(this).get(EditRecipeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_edit_recipe, container, false);

        title_editText = root.findViewById(R.id.editRecipe_title_textInputEditText);
        ingredients_editText = root.findViewById(R.id.editRecipe_ingredients_editView);
        instructions_editText = root.findViewById(R.id.editRecipe_instructions_editView);
        photo_imageView = root.findViewById(R.id.editRecipe_photo_imageView);
        saveBtn = root.findViewById(R.id.editRecipe_save_btn);
        uploadPhotoBtn = root.findViewById(R.id.editRecipe_photo_btn);
        progressBar = root.findViewById(R.id.editRecipe_progressBar);

        isPhotoUploaded = false;

        assert getArguments() != null;
        recipe = RecipeDetailsFragmentArgs.fromBundle(getArguments()).getRecipe();
        setRecipeLayout(recipe);

        //upload photo
        uploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto();
            }
        });

        //save changes
        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                saveChanges(v);
            }
        });

        return root;
    }

    public void setRecipeLayout(Recipe recipe){
        title_editText.setText(recipe.getTitle());
        ingredients_editText.setText(recipe.getIngredients());
        instructions_editText.setText(recipe.getInstructions());
        if(recipe.getImageUrl() != null && !(recipe.getImageUrl().equals("")))
            Picasso.get().load(recipe.getImageUrl()).placeholder(R.drawable.blank_icon).into(photo_imageView);
        else
            photo_imageView.setImageResource(R.drawable.ic_menu_camera);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        saveBtn.setEnabled(true);
    }

    public void saveChanges(final View v){
        final String id = recipe.getId();
        final String title = title_editText.getText().toString();
        final String ingredients = ingredients_editText.getText().toString();
        final String instructions = instructions_editText.getText().toString();
        final String creatorName = recipe.getCreatorName();
        final String creatorEmail = recipe.getCreatorEmail();

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
        saveBtn.setEnabled(false);

        if (isPhotoUploaded) {
            Date date = new Date();
            StorageFirebase.uploadImage(imageBitmap, "photo_" + date.getTime(), new StorageFirebase.Listener() {
                @Override
                public void onSuccess(String url) {
                    imageUrl = url;
                    Recipe updatedRecipe = new Recipe(title, ingredients, instructions, imageUrl, creatorName, creatorEmail);
                    updatedRecipe.setId(id);

                    editRecipeViewModel.updateData(updatedRecipe).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            if (aBoolean)
                                Navigation.findNavController(v).popBackStack(R.id.nav_myRecipes, false); //back to details
                            else
                                Toast.makeText(getActivity(), "Could not update recipe.", Toast.LENGTH_SHORT).show();
                        }

                    });
                }
                @Override
                public void onFail() {
                }
            });
        }
        else {
            imageUrl = recipe.getImageUrl();
            Recipe updatedRecipe = new Recipe(title, ingredients, instructions, imageUrl, creatorName, creatorEmail);
            updatedRecipe.setId(id);

            editRecipeViewModel.updateData(updatedRecipe).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean)
                        Navigation.findNavController(v).popBackStack(R.id.nav_myRecipes, false); //back to details
                    else
                        Toast.makeText(getActivity(), "Could not update recipe.", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    /*********** PHOTO ***********/
    static final int REQUEST_PICK_IMAGE = 1;

    void uploadPhoto(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (pickPhoto.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(pickPhoto, REQUEST_PICK_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { //on return from gallery
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            isPhotoUploaded =true;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(MyApplication.context.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            photo_imageView.setImageBitmap(imageBitmap);
        }
    }
}