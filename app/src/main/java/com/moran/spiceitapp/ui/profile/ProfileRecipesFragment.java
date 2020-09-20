package com.moran.spiceitapp.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moran.spiceitapp.MobileNavigationDirections;
import com.squareup.picasso.Picasso;
import com.moran.spiceitapp.R;
import com.moran.spiceitapp.model.Recipe;
import java.util.LinkedList;
import java.util.List;

public class ProfileRecipesFragment extends Fragment {

    RecyclerView list;
    RecipesListAdapter adapter;
    List<Recipe> data = new LinkedList<>();
    private Context context;
    LiveData<List<Recipe>> liveData;
    NavController navController;
    View root;
    String userEmail;
    public ProfileRecipesViewModel profileRecipesViewModel;

    public ProfileRecipesFragment(){}

    public ProfileRecipesFragment(String profileEmail){
        userEmail = profileEmail;
    }


    /************ INTERFACE *************/
    interface OnItemClickListener{
        void onClick(int position);
    }

    /************ ADAPTER **************/
    class RecipesListAdapter extends RecyclerView.Adapter<RecipeViewHolder>{
        private OnItemClickListener listener;

        void setOnItemClickListener(OnItemClickListener listener){
            this.listener = listener;
        }

        @NonNull
        @Override
        public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.profile_recipes_list_row, viewGroup, false);
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

    /**************** VIEW HOLDER ***************/
    static class RecipeViewHolder extends RecyclerView.ViewHolder{
        ImageView photo_imageView;
        TextView title_textView;
        Recipe recipe;

        public RecipeViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            photo_imageView = itemView.findViewById(R.id.profile_recipes_list_row_photo_imageView);
            title_textView = itemView.findViewById(R.id.profile_recipes_list_row_title_textView);

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
            title_textView.setText(_recipe.getTitle());
            recipe = _recipe;
            if(_recipe.getImageUrl() != null && !(recipe.getImageUrl().equals("")))
                Picasso.get().load(_recipe.getImageUrl()).placeholder(R.drawable.blank_icon).into(photo_imageView);
            else
                photo_imageView.setImageResource(R.drawable.no_photo_icon);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_profile_recipes, container, false);
        profileRecipesViewModel = ViewModelProviders.of(this).get(ProfileRecipesViewModel.class);

        list = root.findViewById(R.id.profileRecipes_recipes_list);
        list.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(layoutManager);

        adapter = new RecipesListAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Recipe recipe = data.get(position);

                navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                MobileNavigationDirections.ActionGlobalRecipeDetailsFragment directions = ProfileFragmentDirections.actionGlobalRecipeDetailsFragment(recipe);
                navController.navigate(directions);
            }
        });

        liveData = profileRecipesViewModel.getData(userEmail);
        liveData.observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() {
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