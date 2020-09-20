package com.moran.spiceitapp.auth;

import androidx.annotation.Nullable;

public interface Auth {

      interface CompletionHandler{
         void onComplete(@Nullable Exception e);
     }

     void signIn(String email, String password, CompletionHandler completionHandler);

     void signOut(CompletionHandler completionHandler);

     void register(String email, String password, CompletionHandler completionHandler);

    String getLoggedInUserEmail();
}
