package com.example.fragmentsintents;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/** Second screen. Receives data through Intent extras and returns a result. */
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_EMAIL = "extra_email";
    public static final String EXTRA_COURSE = "extra_course";
    public static final String EXTRA_RESULT_MESSAGE = "extra_result_message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);   // Up arrow
        }

        Intent in = getIntent();
        String name = in.getStringExtra(EXTRA_NAME);
        String email = in.getStringExtra(EXTRA_EMAIL);
        String course = in.getStringExtra(EXTRA_COURSE);

        ((TextView) findViewById(R.id.tvName)).setText("Name: " + name);
        ((TextView) findViewById(R.id.tvEmail)).setText("Email: " + email);
        ((TextView) findViewById(R.id.tvCourse)).setText("Course: " + course);

        // Implicit Intent 4: compose an email
        findViewById(R.id.btnEmail).setOnClickListener(v -> {
            Intent mail = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
            mail.putExtra(Intent.EXTRA_SUBJECT, "Hello " + name);
            try {
                startActivity(mail);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
            }
        });

        // Return a result to the calling fragment
        findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            Intent data = new Intent();
            data.putExtra(EXTRA_RESULT_MESSAGE, "Profile confirmed for " + name);
            setResult(RESULT_OK, data);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
