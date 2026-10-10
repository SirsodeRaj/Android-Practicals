package com.example.fragmentsintents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class FormFragment extends Fragment {

    private EditText etName, etEmail, etCourse;
    private SharedViewModel vm;

    // Receives the result sent back by DetailActivity
    private final ActivityResultLauncher<Intent> detailLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    String message = result.getData().getStringExtra(DetailActivity.EXTRA_RESULT_MESSAGE);
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                    vm.setSavedProfile("Last confirmed profile:\n"
                            + etName.getText() + " | " + etCourse.getText());
                } else {
                    Toast.makeText(requireContext(), "Returned without confirming", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        etCourse = view.findViewById(R.id.etCourse);
        vm = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        view.findViewById(R.id.btnSubmit).setOnClickListener(v -> submit());
    }

    private void submit() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String course = etCourse.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            return;
        }
        if (course.isEmpty()) {
            etCourse.setError("Course is required");
            return;
        }

        // Explicit Intent: start a specific Activity of this app and pass data as extras
        Intent intent = new Intent(requireContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_NAME, name);
        intent.putExtra(DetailActivity.EXTRA_EMAIL, email);
        intent.putExtra(DetailActivity.EXTRA_COURSE, course);
        detailLauncher.launch(intent);
    }
}
