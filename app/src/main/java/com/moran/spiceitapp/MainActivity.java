package com.moran.spiceitapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;

import com.google.android.material.navigation.NavigationView;
import com.moran.spiceitapp.auth.Auth;
import com.moran.spiceitapp.auth.AuthManager;
import com.moran.spiceitapp.model.Recipe;
import com.moran.spiceitapp.ui.home.HomeFragment;
import com.moran.spiceitapp.ui.home.HomeFragmentDirections;
import com.moran.spiceitapp.ui.myRecipes.MyRecipesFragment;
import com.moran.spiceitapp.ui.myRecipes.MyRecipesFragmentDirections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements HomeFragment.Delegate, MyRecipesFragment.Delegate {

    private AppBarConfiguration mAppBarConfiguration;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_myRecipes, R.id.nav_profile)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
       navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void onRecipeSelected(Recipe recipe){
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        MobileNavigationDirections.ActionGlobalRecipeDetailsFragment directions = HomeFragmentDirections.actionGlobalRecipeDetailsFragment(recipe);
        navController.navigate(directions);
    }

    public void onMyRecipeSelected(Recipe recipe){
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        MyRecipesFragmentDirections.ActionNavMyRecipesToMyRecipeDetailsFragment directions = MyRecipesFragmentDirections.actionNavMyRecipesToMyRecipeDetailsFragment(recipe);

        navController.navigate(directions);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_logout:
                AuthManager.instance.signOut(new Auth.CompletionHandler() {
                    @Override
                    public void onComplete(@Nullable Exception e) {
                        if(e==null) {
                            startActivity(new Intent(MainActivity.this, StartActivity.class));
                        }else
                            Log.d("TAG", "Error: " + e.getMessage());
                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }
}