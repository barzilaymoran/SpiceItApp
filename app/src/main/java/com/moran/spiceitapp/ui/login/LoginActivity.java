package com.moran.spiceitapp.ui.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.moran.spiceitapp.MainActivity;
import com.moran.spiceitapp.R;
import com.moran.spiceitapp.auth.Auth;
import com.moran.spiceitapp.auth.AuthManager;
import com.moran.spiceitapp.model.User;

public class LoginActivity extends AppCompatActivity {

    public static EditText email_editText;
    public static EditText password_editText;
    LoginViewModel loginViewModel;
    ProgressBar progressBar;
    Boolean flag = false;
    View loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginViewModel =  ViewModelProviders.of(this).get(LoginViewModel.class);

        email_editText = findViewById(R.id.login_email_editText);
        password_editText = findViewById(R.id.login_password_editText);
        loginBtn = findViewById(R.id.login_login_btn);
        progressBar = findViewById(R.id.login_progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flag = false;
        progressBar.setVisibility(View.GONE);
        loginBtn.setEnabled(true);
    }

    public void onLogin(View view) {
        final String email = email_editText.getText().toString();
        String password = password_editText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            email_editText.setError("Email is Required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            email_editText.setError("Password is Required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        loginBtn.setEnabled(false);

        AuthManager.instance.signIn(email, password, new Auth.CompletionHandler() {
            @Override
            public void onComplete(@Nullable Exception e) {
                if (e == null) {
                    loginViewModel.getData(email).observe(LoginActivity.this, new Observer<User>() {
                        @Override
                        public void onChanged(User user) {
                            loginViewModel.setLoggedInUser(user);
                            if (!flag) {
                                flag = true;
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                        }
                    });
                } else {
                    if (e.getMessage().equals(AuthManager.WRONG_EMAIL_MESSAGE))
                        LoginActivity.email_editText.setError(e.getMessage());
                    if (e.getMessage().equals(AuthManager.WRONG_PASSWORD_MESSAGE))
                        LoginActivity.password_editText.setError(e.getMessage());
                    progressBar.setVisibility(View.GONE);
                    loginBtn.setEnabled(true);
                }
            }
        });
    }
}