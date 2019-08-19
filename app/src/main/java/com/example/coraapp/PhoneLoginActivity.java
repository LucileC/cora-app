package com.example.coraapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private Button sendVerificationCodeButtn, verifyPhoneButton;
    private EditText phoneNumberInput, verificationCodeInput;
    private CountryCodePicker ccp;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();


        sendVerificationCodeButtn = (Button) findViewById(R.id.send_verification_code_button);
        verifyPhoneButton = (Button) findViewById(R.id.verify_phone_button);
        phoneNumberInput = (EditText) findViewById(R.id.phone_number_input);
        verificationCodeInput = (EditText) findViewById(R.id.verification_code_input);

        ccp = (CountryCodePicker) findViewById(R.id.country_code_picker);
        ccp.registerCarrierNumberEditText(phoneNumberInput);
        ccp.setNumberAutoFormattingEnabled(true);

        loadingBar = new ProgressDialog(this);

        sendVerificationCodeButtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                String phoneNumber = phoneNumberInput.getText().toString();
                String phoneNumber = ccp.getFullNumberWithPlus();
                Toast.makeText(PhoneLoginActivity.this, phoneNumber, Toast.LENGTH_LONG).show();
                if (TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(PhoneLoginActivity.this, "Enter your phone number", Toast.LENGTH_SHORT).show();
                }
                else if (!ccp.isValidFullNumber()){
                    Toast.makeText(PhoneLoginActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                }
                else{
                    loadingBar.setTitle("Phone verification");
                    loadingBar.setMessage("Please wait while we're verifying your phone");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();


                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });


        verifyPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String verificationCode = verificationCodeInput.getText().toString();
                if (TextUtils.isEmpty(verificationCode)){
                    Toast.makeText(PhoneLoginActivity.this, "Verification code required", Toast.LENGTH_SHORT).show();
                }
                else {

                    loadingBar.setTitle("Verification Code");
                    loadingBar.setMessage("Please wait while we are verifying your phone");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();


                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();

                sendVerificationCodeButtn.setVisibility(View.VISIBLE);
                verificationCodeInput.setVisibility(View.INVISIBLE);
                verifyPhoneButton.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                loadingBar.dismiss();

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                sendVerificationCodeButtn.setVisibility(View.INVISIBLE);
                verificationCodeInput.setVisibility(View.VISIBLE);
                verifyPhoneButton.setVisibility(View.VISIBLE);

                Toast.makeText(PhoneLoginActivity.this, "Verification code sent", Toast.LENGTH_SHORT).show();
            }

        };
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "You are logged in successfully", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                        }
                        else {
                            String errorMessage = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendUserToMainActivity() {
        Intent intent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
