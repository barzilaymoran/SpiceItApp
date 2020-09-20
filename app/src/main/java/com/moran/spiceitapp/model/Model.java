package com.moran.spiceitapp.model;

import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.moran.spiceitapp.MyApplication;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Model {

    public static final Model instance = new Model(); //Singleton
    ModelFirebase modelFirebase;
    User loggedInUser;

    public interface Listener<T> {
        void onComplete(T data);
    }

    public interface CompListener {
        void onComplete();
    }

    private Model() {
        modelFirebase = new ModelFirebase();
    }


    /************************** USER *****************************/

    public void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void addUser(final User user, final Listener<User> listener) {
        modelFirebase.addUser(user, new Listener<User>() {
            @Override
            public void onComplete(final User data) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppLocalDb.db.userDao().insert(data);
                    }
                }).start();

                listener.onComplete(data);
            }
        });
    }

    public LiveData<User> getUser(final String email) {
        final MutableLiveData<User> liveData = new MutableLiveData<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final User user = AppLocalDb.db.userDao().getUser(email);
                if(user!=null) {
                    liveData.postValue(user);
                }
                modelFirebase.getUser(email, new Listener<User>() {
                    @Override
                    public void onComplete(final User data) {
                        if(user!=null) {
                            if (user.lastUpdated < data.lastUpdated) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppLocalDb.db.userDao().insert(data);
                                    }
                                }).start();
                            }
                        }else{
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    AppLocalDb.db.userDao().insert(data);
                                }
                            }).start();
                        }
                            liveData.setValue(data);
                        }
                });
            }
        }).start();

        return liveData;
    }

    public void updateUser(final User user, Listener<Boolean> listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppLocalDb.db.userDao().insert(user);
            }
        }).start();
        modelFirebase.updateUser(user, listener);
    }


    /*************************** RECIPE *****************************/

    public void addRecipe(final Recipe recipe, final Listener<Recipe> listener) {
        modelFirebase.addRecipe(recipe, new Listener<Recipe>() {
            @Override
            public void onComplete(final Recipe data) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppLocalDb.db.recipeDao().insert(data);
                    }
                }).start();

                listener.onComplete(data);
            }
        });
    }

    public LiveData<Recipe> getRecipe(final String id) {
        final MutableLiveData<Recipe> liveData = new MutableLiveData<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Recipe recipe = AppLocalDb.db.recipeDao().getRecipe(id);
                liveData.postValue(recipe);
                modelFirebase.getRecipe(id, new Listener<Recipe>() {
                    @Override
                    public void onComplete(final Recipe data) {
                        if (recipe.lastUpdated < data.lastUpdated) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    AppLocalDb.db.recipeDao().insert(data);
                                }
                            }).start();
                            liveData.setValue(data);
                        }
                    }
                });
            }
        }).start();

        return liveData;
    }

    public void updateRecipe(final Recipe recipe, Listener<Boolean> listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppLocalDb.db.recipeDao().insert(recipe);
            }
        }).start();
        modelFirebase.updateRecipe(recipe, listener);
    }

    public void deleteRecipe(final Recipe recipe, final Listener<Boolean> listener) {
        modelFirebase.deleteRecipe(recipe, new Listener<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AppLocalDb.db.recipeDao().delete(recipe);
                    }
                }).start();

                listener.onComplete(data);
            }
        });

    }

    public LiveData<List<Recipe>> getAllRecipes() {
        final MutableLiveData<List<Recipe>> liveData = new MutableLiveData<>();
        // get the last timestamp of getAllRecipes
        final long lastUpdated = MyApplication.context.getSharedPreferences("TAG", MODE_PRIVATE).getLong("AllRecipesLastUpdateDate", 0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Recipe> recipes = AppLocalDb.db.recipeDao().getAll("false");
                Collections.sort(recipes, new Comparator<Recipe>(){
                    public int compare(Recipe r1, Recipe r2) {
                        return (int)(r2.lastUpdated - r1.lastUpdated);
                    }
                });
                liveData.postValue(recipes);

                modelFirebase.getAllRecipesSince(lastUpdated, new Listener<List<Recipe>>() {
                    @Override
                    public void onComplete(List<Recipe> data) {
                        long lastUpdated = 0;
                        for (final Recipe updatedRecipe : data) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    AppLocalDb.db.recipeDao().insert(updatedRecipe);
                                }
                            }).start();

                            if (updatedRecipe.lastUpdated > lastUpdated)
                                lastUpdated = updatedRecipe.lastUpdated;
                        }
                        // update new timestamp
                        SharedPreferences.Editor edit = MyApplication.context.getSharedPreferences("TAG", MODE_PRIVATE).edit();
                        edit.putLong("AllRecipesLastUpdateDate", lastUpdated);
                        edit.apply();

                        for(final Recipe updatedRecipe: data){
                            if(recipes.contains(updatedRecipe)){
                                int index = recipes.indexOf(updatedRecipe);
                                recipes.remove(index);
                            }
                            if(updatedRecipe.getIsDeleted().equals("false"))
                                 recipes.add(updatedRecipe);
                        }

                        Collections.sort(recipes, new Comparator<Recipe>(){
                            public int compare(Recipe r1, Recipe r2) {
                                return (int)(r2.lastUpdated - r1.lastUpdated);
                            }
                        });

                        liveData.setValue(recipes);
                    }
                });
            }
        }).start();

        return liveData;
    }


    public LiveData<List<Recipe>> getMyRecipes(final String userEmail) {
        final MutableLiveData<List<Recipe>> liveData = new MutableLiveData<>();
        // get the last timestamp of getMyRecipes
        final long lastUpdated = MyApplication.context.getSharedPreferences("TAG", MODE_PRIVATE).getLong("MyRecipesLastUpdateDate", 0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Recipe> recipes = AppLocalDb.db.recipeDao().getMyRecipes(userEmail, "false");
                Collections.sort(recipes, new Comparator<Recipe>(){
                    public int compare(Recipe r1, Recipe r2) {
                        return (int)(r2.lastUpdated - r1.lastUpdated);
                    }
                });
                liveData.postValue(recipes);
                modelFirebase.getMyRecipesSince(userEmail, lastUpdated, new Listener<List<Recipe>>() {
                    @Override
                    public void onComplete(List<Recipe> data) {
                        long lastUpdated = 0;
                        for (final Recipe updatedRecipe : data) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    AppLocalDb.db.recipeDao().insert(updatedRecipe);
                                }
                            }).start();

                            if (updatedRecipe.lastUpdated > lastUpdated)
                                lastUpdated = updatedRecipe.lastUpdated;
                        }
                        // update new timestamp
                        SharedPreferences.Editor edit = MyApplication.context.getSharedPreferences("TAG", MODE_PRIVATE).edit();
                        edit.putLong("MyRecipesLastUpdateDate", lastUpdated);
                        edit.apply();

                        for(final Recipe updatedRecipe: data){
                            if(recipes.contains(updatedRecipe)){
                                int index = recipes.indexOf(updatedRecipe);
                                recipes.remove(index);
                            }
                            if(updatedRecipe.getIsDeleted().equals("false"))
                                recipes.add(updatedRecipe);
                        }

                        Collections.sort(recipes, new Comparator<Recipe>(){
                            public int compare(Recipe r1, Recipe r2) {
                                return (int)(r2.lastUpdated - r1.lastUpdated);
                            }
                        });

                        liveData.setValue(recipes);
                    }
                });
            }
        }).start();

        return liveData;
    }

    public LiveData<List<Recipe>> getProfileRecipes(final String userEmail) {
        final MutableLiveData<List<Recipe>> liveData = new MutableLiveData<>();
        // get the last timestamp of getMyRecipes
        final long lastUpdated = MyApplication.context.getSharedPreferences("TAG", MODE_PRIVATE).getLong("ProfileRecipesLastUpdateDate", 0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Recipe> recipes = AppLocalDb.db.recipeDao().getMyRecipes(userEmail, "false");
                Collections.sort(recipes, new Comparator<Recipe>(){
                    public int compare(Recipe r1, Recipe r2) {
                        return (int)(r2.lastUpdated - r1.lastUpdated);
                    }
                });
                liveData.postValue(recipes);
                modelFirebase.getMyRecipesSince(userEmail, lastUpdated, new Listener<List<Recipe>>() {
                    @Override
                    public void onComplete(List<Recipe> data) {
                        long lastUpdated = 0;
                        for (final Recipe updatedRecipe : data) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    AppLocalDb.db.recipeDao().insert(updatedRecipe);
                                }
                            }).start();

                            if (updatedRecipe.lastUpdated > lastUpdated)
                                lastUpdated = updatedRecipe.lastUpdated;
                        }
                        // update new timestamp
                        SharedPreferences.Editor edit = MyApplication.context.getSharedPreferences("TAG", MODE_PRIVATE).edit();
                        edit.putLong("ProfileRecipesLastUpdateDate", lastUpdated);
                        edit.apply();

                        for(final Recipe updatedRecipe: data){
                            if(recipes.contains(updatedRecipe)){
                                int index = recipes.indexOf(updatedRecipe);
                                recipes.remove(index);
                            }
                            recipes.add(updatedRecipe);
                        }

                        Collections.sort(recipes, new Comparator<Recipe>(){
                            public int compare(Recipe r1, Recipe r2) {
                                return (int)(r2.lastUpdated - r1.lastUpdated);
                            }
                        });

                        liveData.setValue(recipes);
                    }
                });
            }
        }).start();

        return liveData;
    }
}
