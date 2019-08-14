package com.example.coraapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updateSettings;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;

    private String currentUserID;

    private FirebaseAuth auth;
    private DatabaseReference rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        InitializeFields();

        updateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateSettings();
            }
        });

        RetrieveUserInfo();
    }

    private void RetrieveUserInfo() {
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && (dataSnapshot.hasChild("name") && dataSnapshot.hasChild("status") && dataSnapshot.hasChild("image"))){
                    String retreiveUserName = dataSnapshot.child("name").getValue().toString();
                    String retreiveUserStatus = dataSnapshot.child("status").getValue().toString();
                    String profileImage = dataSnapshot.child("image").getValue().toString();
                    userName.setText(retreiveUserName);
                    userStatus.setText(retreiveUserStatus);
                }
                else if (dataSnapshot.exists() && (dataSnapshot.hasChild("name") && dataSnapshot.hasChild("image"))){
                    String retreiveUserName = dataSnapshot.child("name").getValue().toString();
                    String profileImage = dataSnapshot.child("image").getValue().toString();
                    userName.setText(retreiveUserName);
                }
                else if (dataSnapshot.exists() && (dataSnapshot.hasChild("name") && dataSnapshot.hasChild("status"))){
                    String retreiveUserName = dataSnapshot.child("name").getValue().toString();
                    String retreiveUserStatus = dataSnapshot.child("status").getValue().toString();

                    userName.setText(retreiveUserName);
                    userStatus.setText(retreiveUserStatus);
                }
                else if (dataSnapshot.exists() && dataSnapshot.hasChild("name")) {
                    String retreiveUserName = dataSnapshot.child("name").getValue().toString();
                    userName.setText(retreiveUserName);
                }
                else {
                    Toast.makeText(SettingsActivity.this, "Set your profile information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UpdateSettings() {
        String setUserName = userName.getText().toString();
        String setUserStatus = userStatus.getText().toString();

        if (TextUtils.isEmpty(setUserName)){
            Toast.makeText(this, "Please provide a user name", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", setUserName);
            profileMap.put("status", setUserStatus);
            rootRef.child("Users").child(currentUserID).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SettingsActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        SendUserToMainActivity();
                    } else {
                        String error = task.getException().toString();
                        Toast.makeText(SettingsActivity.this, "Error: "+error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void InitializeFields() {
        updateSettings = (Button) findViewById(R.id.update_settings);
        userName = (EditText) findViewById(R.id.set_user_name);
        userStatus = (EditText) findViewById(R.id.set_user_status);
        userProfileImage = (CircleImageView) findViewById(R.id.profile_image);
    }


    private void SendUserToMainActivity() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
