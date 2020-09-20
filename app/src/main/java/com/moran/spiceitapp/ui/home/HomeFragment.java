package com.moran.spiceitapp.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.moran.spiceitapp.R;
import com.moran.spiceitapp.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    RecyclerView list;
    RecipesListAdapter adapter;
    List<Recipe> data = new LinkedList<>();
    LiveData<List<Recipe>> liveData;

    public HomeFragment(){
    }

    /************* DELEGATE **************/
    public interface Delegate{
        void onRecipeSelected(Recipe recipe);
    }
    HomeFragment.Delegate parent;

    /************* INTERFACE *************/
    interface OnItemClickListener{
        void onClick(int position);
    }

    /************* ADAPTER **************/
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

    /************ VIEW HOLDER ************/
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
            if(recipe.getImageUrl() != null && !(recipe.getImageUrl().equals(""))) {
                Picasso.get().load(_recipe.getImageUrl()).placeholder(R.drawable.blank_icon).into(photo_imageView);
            }else
                photo_imageView.setImageResource(R.drawable.no_photo_icon);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        list = root.findViewById(R.id.home_recipes_list);
        list.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(layoutManager);

        adapter = new RecipesListAdapter();
        list.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() { //getting treatment
            @Override
            public void onClick(int position) {
                Recipe recipe = data.get(position);
                parent.onRecipeSelected(recipe); //via activity
            }
        });

        liveData = homeViewModel.getData();
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

        final SwipeRefreshLayout swipeRefresh = root.findViewById(R.id.recipes_list_swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                homeViewModel.getData().observe(getViewLifecycleOwner(), new Observer<List<Recipe>>() { //register as observer
                    @Override
                    public void onChanged(List<Recipe> recipes) {
                        data = recipes;
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

      return root;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        parent = (HomeFragment.Delegate) getActivity();
    }
}