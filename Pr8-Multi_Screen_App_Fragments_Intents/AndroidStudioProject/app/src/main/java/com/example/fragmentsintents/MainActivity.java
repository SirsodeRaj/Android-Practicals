package com.example.fragmentsintents;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Experiment 8 - Host activity. Switches between three Fragments using a
 * BottomNavigationView and FragmentManager transactions.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                fragment = new HomeFragment();
                setTitle(R.string.title_home);
            } else if (id == R.id.nav_form) {
                fragment = new FormFragment();
                setTitle(R.string.title_form);
            } else {
                fragment = new AboutFragment();
                setTitle(R.string.title_about);
            }
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            return true;
        });

        // Show Home only the first time (after rotation Android restores the fragment itself)
        if (savedInstanceState == null) {
            nav.setSelectedItemId(R.id.nav_home);
        }
    }
}
