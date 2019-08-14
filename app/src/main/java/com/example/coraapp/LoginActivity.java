package com.example.coraapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.widget.Toast.LENGTH_SHORT;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private Button loginButton;
    private EditText userEmail, userPassword;
    private TextView forgotPassord, needNewAccountLink, logingWithPhoneLink;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        InitializeFields();
    }

    private void InitializeFields() {
        loginButton = (Button) findViewById(R.id.login_button);
        userEmail = (EditText) findViewById(R.id.login_email);
        userPassword = (EditText) findViewById(R.id.login_password);

        forgotPassord = (TextView) findViewById(R.id.forget_password_link);
        needNewAccountLink = (TextView) findViewById(R.id.need_new_account_link);
        logingWithPhoneLink = (TextView) findViewById(R.id.login_using_phone_link);

        loadingBar = new ProgressDialog(this);

        needNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

    }

    private void Login() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter your email...", LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter a password...", LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Logging...");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        SendUserToMainActivity();
                        Toast.makeText(LoginActivity.this, "Successfully logged in" , Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                    else {
                        String message = task.getException().toString();
                        Toast.makeText(LoginActivity.this, "Error: "+message, Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }


    private void SendUserToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void SendUserToRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
