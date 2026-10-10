package com.example.fragmentsintents;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvProfile = view.findViewById(R.id.tvProfile);

        // Observe data coming from FormFragment through the shared ViewModel
        SharedViewModel vm = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        vm.getSavedProfile().observe(getViewLifecycleOwner(), text -> tvProfile.setText(text));

        // Implicit Intent 1: open a web page in a browser
        view.findViewById(R.id.btnWebsite).setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://developer.android.com"));
            try {
                startActivity(i);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireContext(), "No browser found", Toast.LENGTH_SHORT).show();
            }
        });

        // Implicit Intent 2: share text with any app
        view.findViewById(R.id.btnShare).setOnClickListener(v -> {
            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("text/plain");
            send.putExtra(Intent.EXTRA_TEXT, "Check out my Fragments and Intents practical app!");
            startActivity(Intent.createChooser(send, "Share via"));
        });
    }
}
