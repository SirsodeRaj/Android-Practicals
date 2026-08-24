package com.example.firebaseauthentication;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import android.widget.Toast;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        Toast.makeText(this, "Firebase Connected Successfully", Toast.LENGTH_LONG).show();
    }
}