package com.moran.spiceitapp.ui.myRecipes;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
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
import android.widget.Toast;

import com.moran.spiceitapp.R;
import com.moran.spiceitapp.model.Recipe;
import com.moran.spiceitapp.ui.home.RecipeDetailsFragmentArgs;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MyRecipeDetailsFragment extends Fragment {

    private MyRecipeDetailsViewModel myRecipeDetailsViewModel;
    Recipe recipe = new Recipe ();
    TextView title_textView, creatorName_textView, ingredients_textView,  instructions_textView;
    ImageView photo_imageView;
    View root;
    NavController navController;

    public MyRecipeDetailsFragment() {}

    public static MyRecipeDetailsFragment newInstance() {
        return new MyRecipeDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        myRecipeDetailsViewModel = ViewModelProviders.of(this).get(MyRecipeDetailsViewModel.class);
        root = inflater.inflate(R.layout.fragment_recipe_details, container, false);

        title_textView = root.findViewById(R.id.recipe_details_title_textView);
        creatorName_textView = root.findViewById(R.id.recipe_details_creatorName_textView);
        ingredients_textView = root.findViewById(R.id.recipe_details_ingredients_textView);
        instructions_textView = root.findViewById(R.id.recipe_details_instructions_textView);
        photo_imageView = root.findViewById(R.id.recipe_details_photo_imageView);

        assert getArguments() != null;
        recipe = RecipeDetailsFragmentArgs.fromBundle(getArguments()).getRecipe();
        setRecipeLayout(recipe);

        return root;
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

    /*********** MENU ************/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.my_recipe_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.my_recipe_details_action_edit:
                navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                MyRecipeDetailsFragmentDirections.ActionMyRecipeDetailsFragmentToEditRecipeFragment directions = MyRecipeDetailsFragmentDirections.actionMyRecipeDetailsFragmentToEditRecipeFragment(recipe);
                navController.navigate(directions);
                return true;
            case R.id.my_recipe_details_action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to delete this recipe?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                recipe.setIsDeleted("true");
                                myRecipeDetailsViewModel.deleteData(recipe).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                                    @Override
                                    public void onChanged(Boolean aBoolean) {
                                        if(aBoolean)
                                            Navigation.findNavController(getParentFragment().requireView()).popBackStack(R.id.nav_myRecipes, false);
                                        else
                                            Toast.makeText(getActivity(), "Could not delete recipe.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", null);

                AlertDialog alert= builder.create();
                alert.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}