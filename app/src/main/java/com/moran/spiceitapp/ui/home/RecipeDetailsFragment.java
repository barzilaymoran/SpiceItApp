package com.moran.spiceitapp.ui.home;

import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moran.spiceitapp.OnTouchClickListener;
import com.moran.spiceitapp.R;
import com.moran.spiceitapp.model.Recipe;
import com.squareup.picasso.Picasso;

public class RecipeDetailsFragment extends Fragment {

    Recipe recipe = new Recipe ();
    TextView title_textView, creatorName_textView, ingredients_textView,  instructions_textView;
    ImageView photo_imageView;
    View root;

    public RecipeDetailsFragment() {}

    public static RecipeDetailsFragment newInstance() {
        return new RecipeDetailsFragment();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        RecipeDetailsViewModel recipeDetailsViewModel = ViewModelProviders.of(this).get(RecipeDetailsViewModel.class);
        root = inflater.inflate(R.layout.fragment_recipe_details, container, false);

        title_textView = root.findViewById(R.id.recipe_details_title_textView);
        creatorName_textView = root.findViewById(R.id.recipe_details_creatorName_textView);
        ingredients_textView = root.findViewById(R.id.recipe_details_ingredients_textView);
        instructions_textView = root.findViewById(R.id.recipe_details_instructions_textView);
        photo_imageView = root.findViewById(R.id.recipe_details_photo_imageView);

        assert getArguments() != null;
        recipe = RecipeDetailsFragmentArgs.fromBundle(getArguments()).getRecipe();
        setRecipeLayout(recipe);

        creatorName_textView.setOnTouchListener(new OnTouchClickListener(new OnTouchClickListener.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecipeDetailsFragmentDirections.ActionRecipeDetailsFragmentToProfileFragment directions = RecipeDetailsFragmentDirections.actionRecipeDetailsFragmentToProfileFragment(recipe.getCreatorEmail());
                Navigation.findNavController(getParentFragment().requireView()).navigate(directions);
            }
        },5));

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setRecipeLayout(Recipe recipe){
        title_textView.setText(recipe.getTitle());
        creatorName_textView.setText(recipe.getCreatorName());
        ingredients_textView.setText(recipe.getIngredients());
        instructions_textView.setText(recipe.getInstructions());
        if(recipe.getImageUrl() != null && !(recipe.getImageUrl().equals("")))
            Picasso.get().load(recipe.getImageUrl()).placeholder(R.drawable.blank_icon).into(photo_imageView);
        else
            photo_imageView.setImageResource(R.drawable.no_photo_icon);
    }
}