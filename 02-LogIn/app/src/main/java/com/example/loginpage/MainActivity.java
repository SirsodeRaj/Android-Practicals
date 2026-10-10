package com.example.loginpage;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;



import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin, btnFacebook;
    TextView tvForgotPass, tvSignUp;

    boolean isPasswordVisible= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnFacebook = findViewById(R.id.btnFacebook);

        tvForgotPass = findViewById(R.id.tvForgotPass);
        tvSignUp = findViewById(R.id.tvSignUp);

        //visibity
        etPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (event.getX() >= (etPassword.getWidth()
                            - etPassword.getCompoundPaddingRight())) {

                        if (isPasswordVisible) {

                            etPassword.setTransformationMethod(
                                    PasswordTransformationMethod.getInstance());

                            etPassword.setCompoundDrawablesWithIntrinsicBounds(
                                    0, 0,
                                    R.drawable.baseline_visibility_24,
                                    0);

                            isPasswordVisible = false;

                        } else {

                            etPassword.setTransformationMethod(
                                    HideReturnsTransformationMethod.getInstance());

                            etPassword.setCompoundDrawablesWithIntrinsicBounds(
                                    0, 0,
                                    R.drawable.baseline_visibility_off_24,
                                    0);

                            isPasswordVisible = true;
                        }

                        etPassword.setSelection(etPassword.getText().length());

                        return true;
                    }
                }

                return false;
            }
        });

        // Login Button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    etUsername.setError("Please enter Username");
                    etUsername.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Please enter Password");
                    etPassword.requestFocus();
                    return;
                }

                // Demo Login Credentials
                if (username.equals("admin") && password.equals("1234")) {

                    Toast.makeText(MainActivity.this,
                            "Login Successful",
                            Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(MainActivity.this,
                            "Invalid Username or Password",
                            Toast.LENGTH_SHORT).show();

                }

            }
        });

        // Facebook Button
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this,
                        "Facebook Login Clicked",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Forgot Password
        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this,
                        "Forgot Password Clicked",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Sign Up
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this,
                        "Sign Up Clicked",
                        Toast.LENGTH_SHORT).show();

            }
        });

    }
}