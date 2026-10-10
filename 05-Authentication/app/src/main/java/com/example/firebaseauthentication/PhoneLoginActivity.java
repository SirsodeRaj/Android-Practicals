package com.example.firebaseauthentication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import android.content.Intent;

public class PhoneLoginActivity extends AppCompatActivity {

    EditText etPhone, etOtp;
    Button btnSendOtp, btnVerifyOtp;

    FirebaseAuth mAuth;

    String verificationId;

    PhoneAuthProvider.ForceResendingToken resendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        etPhone = findViewById(R.id.etPhone);
        etOtp = findViewById(R.id.etOtp);

        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);

        mAuth = FirebaseAuth.getInstance();

        btnSendOtp.setOnClickListener(v -> sendOTP());
        btnVerifyOtp.setOnClickListener(v -> verifyOTP());

    }

    private void verifyOTP() {

        String otp = etOtp.getText().toString().trim();

        if (otp.isEmpty()) {
            etOtp.setError("Enter OTP");
            return;
        }

        if (verificationId == null) {
            Toast.makeText(this,
                    "Please send OTP first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(
                        verificationId,
                        otp
                );

        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)

                .addOnCompleteListener(task -> {

                    if(task.isSuccessful()){

                        Toast.makeText(
                                PhoneLoginActivity.this,
                                "Login Successful",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(
                                PhoneLoginActivity.this,
                                HomeActivity.class
                        );

                        startActivity(intent);
                        finish();

                    }else{

                        Toast.makeText(
                                PhoneLoginActivity.this,
                                task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

    private void sendOTP() {

        String phone = etPhone.getText().toString().trim();

        if (!phone.startsWith("+")) {
            phone = "+91" + phone;   // Adds India country code automatically
        }

        if(phone.isEmpty()){
            etPhone.setError("Enter phone number");
            return;
        }

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(callbacks)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    signInWithCredential(phoneAuthCredential);

                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {

                    e.printStackTrace();

                    Toast.makeText(
                            PhoneLoginActivity.this,
                            e.getClass().getSimpleName() + "\n" + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }

                @Override
                public void onCodeSent(@NonNull String s,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {

                    super.onCodeSent(s, token);

                    verificationId = s;
                    resendToken = token;

                    Toast.makeText(PhoneLoginActivity.this,
                            "OTP Sent",
                            Toast.LENGTH_SHORT).show();

                }

            };

}