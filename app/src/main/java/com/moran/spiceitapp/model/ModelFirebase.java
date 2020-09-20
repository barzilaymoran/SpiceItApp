package com.moran.spiceitapp.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ModelFirebase {

    public FirebaseFirestore db;
    final static String USER_COLLECTION = "users";
    final static String RECIPE_COLLECTION = "recipes";

    public ModelFirebase() {
        db = FirebaseFirestore.getInstance();
    }


    /********************* USER ***********************/

    public void addUser(final User user, @Nullable final Model.Listener<User> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USER_COLLECTION).document(user.getEmail()).set(user.toJson()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    listener.onComplete(user);
                }
            }
        });
    }

    public void getUser(String email, final Model.Listener<User> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USER_COLLECTION).document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> json = document.getData();
                        User user = User.factory(json);
                        listener.onComplete(user);
                        Log.d("TAG", "USER | DocumentSnapshot data : " + document.getData());
                    } else {
                        Log.d("TAG", "ERROR: No Such Document");
                    }
                } else {
                    Log.d("TAG", "ERROR: Get Failed With ", task.getException());
                }
            }
        });
    }

    public void updateUser(User user, final Model.Listener<Boolean> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USER_COLLECTION).document(user.getEmail()).update(user.toJson()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (listener != null) {
                    listener.onComplete(task.isSuccessful());
                }
            }
        });
    }

    /************************ RECIPE *************************/

    public void getAllRecipesSince(long since, final Model.Listener<List<Recipe>> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp ts = new Timestamp(since, 0);
        db.collection(RECIPE_COLLECTION).whereGreaterThan("lastUpdated", ts)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Recipe> recipeData = null;
                if (task.isSuccessful()) {
                    recipeData = new LinkedList<Recipe>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Map<String, Object> json = doc.getData();
                        Recipe recipe = Recipe.factory(json);
                        recipeData.add(recipe);
                    }
                }

                Collections.sort(recipeData, new Comparator<Recipe>() {
                        public int compare(Recipe r1, Recipe r2) {
                            return (int) (r2.lastUpdated - r1.lastUpdated);
                        }
                });

                listener.onComplete(recipeData);
            }
        });
    }

    public void getMyRecipesSince(String userEmail, long since, final Model.Listener<List<Recipe>> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp ts = new Timestamp(since, 0);
        db.collection(RECIPE_COLLECTION).whereGreaterThan("lastUpdated", ts).whereEqualTo("creatorEmail", userEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Recipe> recipeData = null;
                if (task.isSuccessful()) {
                    recipeData = new LinkedList<Recipe>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Map<String, Object> json = doc.getData();
                        Recipe recipe = Recipe.factory(json);
                        recipeData.add(recipe);
                    }
                }

                listener.onComplete(recipeData);
            }
        });
    }

    public void addRecipe(final Recipe recipe, @Nullable final Model.Listener<Recipe> listener) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(RECIPE_COLLECTION).add(recipe.toJson()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d("TAG", "RECIPE | DocumentSnapshot written with ID: " + documentReference.getId());
                recipe.setId(documentReference.getId());
                updateRecipe(recipe, new Model.Listener<Boolean>() {
                    @Override
                    public void onComplete(Boolean data) {
                        if (data) {
                            listener.onComplete(recipe);
                        } else
                            listener.onComplete(null);
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                        listener.onComplete(null);
                    }
                });

    }

    public void getRecipe(String id, final Model.Listener<Recipe> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(RECIPE_COLLECTION).document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> json = document.getData();
                        Recipe recipe = Recipe.factory(json);
                        listener.onComplete(recipe);
                        Log.d("TAG", "RECIPE | DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "ERROR: No Such Document");
                    }
                } else {
                    Log.d("TAG", "ERROR: Get Failed With ", task.getException());
                }
            }
        });
    }

    public void updateRecipe(Recipe recipe, final Model.Listener<Boolean> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(RECIPE_COLLECTION).document(recipe.getId()).update(recipe.toJson()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (listener != null) {
                    listener.onComplete(task.isSuccessful());
                }
            }
        });
    }

    public void deleteRecipe(Recipe recipe, final Model.Listener<Boolean> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(RECIPE_COLLECTION).document(recipe.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "DocumentSnapshot Successfully Deleted");
                if (listener != null)
                    listener.onComplete(task.isSuccessful());
            }
        });
    }
}
