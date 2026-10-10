package com.example.billsplitter;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etBill, etTip, etPeople;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etBill = findViewById(R.id.etBill);
        etTip = findViewById(R.id.etTip);
        etPeople = findViewById(R.id.etPeople);
        tvResult = findViewById(R.id.tvResult);

        findViewById(R.id.btnCalculate).setOnClickListener(v -> calculate());
        findViewById(R.id.btnReset).setOnClickListener(v -> reset());

        // Show the version defined in build.gradle - useful to confirm which build is installed
        TextView tvVersion = findViewById(R.id.tvVersion);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            tvVersion.setText("Version " + info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            tvVersion.setText("Version unknown");
        }
    }

    private void calculate() {
        double bill;
        double tipPercent;
        int people;
        try {
            bill = Double.parseDouble(etBill.getText().toString().trim());
            String tipText = etTip.getText().toString().trim();
            tipPercent = tipText.isEmpty() ? 0 : Double.parseDouble(tipText);
            people = Integer.parseInt(etPeople.getText().toString().trim());
        } catch (NumberFormatException e) {
            tvResult.setText("Please enter valid numbers.");
            return;
        }
        if (bill < 0 || tipPercent < 0 || people < 1) {
            tvResult.setText("Bill and tip must be positive and people at least 1.");
            return;
        }

        double tip = bill * tipPercent / 100.0;
        double total = bill + tip;
        double perPerson = total / people;

        tvResult.setText(String.format(Locale.getDefault(),
                "Tip: %.2f%nTotal bill: %.2f%nEach person pays: %.2f", tip, total, perPerson));
    }

    private void reset() {
        etBill.setText("");
        etTip.setText("");
        etPeople.setText("");
        tvResult.setText("");
        etBill.requestFocus();
    }
}
