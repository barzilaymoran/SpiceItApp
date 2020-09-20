package com.moran.spiceitapp.ui.myRecipes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import com.moran.spiceitapp.MainActivity;
import com.moran.spiceitapp.R;
import com.moran.spiceitapp.StartActivity;
import com.moran.spiceitapp.auth.Auth;
import com.moran.spiceitapp.auth.AuthManager;
import com.moran.spiceitapp.model.Recipe;
import com.moran.spiceitapp.model.User;
import com.moran.spiceitapp.ui.home.HomeFragment;
import com.moran.spiceitapp.ui.home.HomeFragmentDirections;
import com.moran.spiceitapp.ui.home.HomeViewModel;
import com.moran.spiceitapp.ui.login.LoginActivity;

import java.util.LinkedList;
import java.util.List;

public class MyRecipesFragment extends Fragment {

    RecyclerView list;
    RecipesListAdapter adapter;
    List<Recipe> data = new LinkedList<>();
    LiveData<List<Recipe>> liveData;
    NavController navController;
    View root;

    public MyRecipesFragment(){}

    private MyRecipesViewModel myRecipesViewModel;

    public static MyRecipesFragment newInstance() { return new MyRecipesFragment(); }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
        parent = (MyRecipesFragment.Delegate) getActivity();
    }

    /*********** DELEGATE ***********/
    public interface Delegate{
        void onMyRecipeSelected(Recipe recipe);
    }
    MyRecipesFragment.Delegate parent;

    /********** INTERFACE ***********/
    interface OnItemClickListener{
        void onClick(int position);
    }


    /*********** ADAPTER ***********/

    class RecipesListAdapter extends RecyclerView.Adapter<RecipeViewHolder>{
        private OnItemClickListener listener;

        void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }

        @NonNull
        @Override
        public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.recipes_list_row, viewGroup, false);
            RecipeViewHolder vh = new RecipeViewHolder(v, listener);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
            Recipe recipe = data.get(position);
            holder.bind(recipe);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    /************* VIEW HOLDER ************/
    static class RecipeViewHolder extends RecyclerView.ViewHolder{
        TextView creatorName_textView;
        ImageView photo_imageView;
        TextView title_textView;
        Recipe recipe;

        public RecipeViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            creatorName_textView = itemView.findViewById(R.id.recipes_list_row_creatorName_textView);
            photo_imageView = itemView.findViewById(R.id.recipes_list_row_photo_imageView);
            title_textView = itemView.findViewById(R.id.recipes_list_row_title_textView);

            itemView.setOnClickListener(new View.OnClickListener() { //put listener on row
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onClick(position);
                        }
                    }
                }
            });
        }

        public void bind(Recipe _recipe){
            creatorName_textView.setText(_recipe.getCreatorName());
            title_textView.setText(_recipe.getTitle());
            recipe = _recipe;
            if(_recipe.getImageUrl() != null && !(recipe.getImageUrl().equals("")))
                Picasso.get().load(_recipe.getImageUrl()).placeholder(R.drawable.blank_icon).into(photo_imageView);
            else
                photo_imageView.setImageResource(R.drawable.no_photo_icon);


        }
    }

    /************ MENU *************/
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.my_recipes_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.my_recipes_action_add:
                Navigation.findNavController(root).navigate(R.id.action_nav_myRecipes_to_addRecipeFragment);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        myRecipesViewModel = ViewModelProviders.of(this).get(MyRecipesViewModel.class);
        root = inflater.inflate(R.layout.fragment_my_recipes, container, false);

        list = root.findViewById(R.id.myRecipes_recipes_list);
        list.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(layoutManager);

        adapter = new RecipesListAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() { //getting treatment
            @Override
            public void onClick(int position) {
                Recipe recipe = data.get(position);
                parent.onMyRecipeSelected(recipe); //via activity
            }
        });

        liveData = myRecipesViewModel.getData();
        liveData.observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() { //register as observer
            @Override
            public void onChanged(List<Recipe> recipes) {
                data = recipes;
                adapter.notifyDataSetChanged();
            }
        });

        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        list.addItemDecoration(divider);

        return root;
    }
}