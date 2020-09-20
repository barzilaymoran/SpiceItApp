package com.moran.spiceitapp.auth;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class AuthManager implements Auth {

    public static final AuthManager instance = new AuthManager(); //Singleton
    private FirebaseAuth firebaseAuth;
    private String loggedInUserEmail;
    public static final String WRONG_EMAIL_MESSAGE = "There is no user record corresponding to this identifier. The user may have been deleted.";
    public static final String WRONG_PASSWORD_MESSAGE = "The password is invalid or the user does not have a password.";

    private AuthManager() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void register(String email, String password, final CompletionHandler completionHandler){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("TAG", "User Register: Success");
                            completionHandler.onComplete(null);
                            loggedInUserEmail =  firebaseAuth.getCurrentUser().getEmail();
                        } else {
                            Log.w("TAG", "User Register: Failure", task.getException());
                            completionHandler.onComplete(task.getException());
                        }
                    }
                });
    }


    @Override
    public void signIn(String email, String password, final CompletionHandler completionHandler) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "User Login: Success");
                            loggedInUserEmail = firebaseAuth.getCurrentUser().getEmail();
                            completionHandler.onComplete(null);
                        } else {
                            Log.w("TAG", "User Login: Failure", task.getException());
                            completionHandler.onComplete(task.getException());
                        }
                    }
                });
    }

    @Override
    public void signOut(CompletionHandler completionHandler){
        try {
            firebaseAuth.signOut();
            completionHandler.onComplete(null);
        } catch (Exception e){
            completionHandler.onComplete(e);
        }
    }

    @Override
    public String getLoggedInUserEmail() {
        return loggedInUserEmail;
    }

}
