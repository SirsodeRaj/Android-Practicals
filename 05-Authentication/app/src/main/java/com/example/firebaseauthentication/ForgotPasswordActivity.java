package com.example.firebaseauthentication;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etEmail;
    Button btnReset;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        btnReset = findViewById(R.id.btnReset);

        mAuth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();

            if(email.isEmpty()){

                etEmail.setError("Enter Email");
                return;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                etEmail.setError("Invalid Email");
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {

                        if(task.isSuccessful()){

                            Toast.makeText(this,
                                    "Reset Email Sent",
                                    Toast.LENGTH_LONG).show();

                            finish();

                        }else{

                            Toast.makeText(this,
                                    task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();

                        }

                    });

        });

    }
}