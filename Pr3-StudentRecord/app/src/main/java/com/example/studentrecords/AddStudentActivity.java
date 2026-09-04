package com.example.studentrecords;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddStudentActivity extends AppCompatActivity {

    EditText etStudentName;
    EditText etRollNumber;
    EditText etMarks;

    Spinner spinnerCourse;

    Button btnSaveStudent;

    Button btnBack;

    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_student);


        // Connect XML components

        etStudentName =
                findViewById(R.id.etStudentName);

        etRollNumber =
                findViewById(R.id.etRollNumber);

        spinnerCourse =
                findViewById(R.id.spinnerCourse);

        etMarks =
                findViewById(R.id.etMarks);

        btnSaveStudent =
                findViewById(R.id.btnSaveStudent);

        btnBack = findViewById(R.id.btnBack);


        // Initialize database

        databaseHelper =
                new DatabaseHelper(this);


        // -----------------------------------------
        // COURSE DROPDOWN
        // -----------------------------------------

        String[] courses = {
                "Select Course",
                "B.Tech AIDS",
                "B.Tech CSE",
                "B.Tech IT",
                "B.Tech ECE",
                "B.Tech Mechanical",
                "B.Tech Civil",
                "B.Tech Electrical",
                "BCA",
                "MCA",
                "Other"
        };


        ArrayAdapter<String> courseAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        courses
                );


        courseAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );


        spinnerCourse.setAdapter(courseAdapter);


        // -----------------------------------------
        // BACK BUTTON
        // -----------------------------------------

        btnBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );


        // -----------------------------------------
        // SAVE STUDENT
        // -----------------------------------------

        btnSaveStudent.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        String name =
                                etStudentName
                                        .getText()
                                        .toString()
                                        .trim();


                        String rollNumber =
                                etRollNumber
                                        .getText()
                                        .toString()
                                        .trim();


                        String course =
                                spinnerCourse
                                        .getSelectedItem()
                                        .toString();


                        String marksText =
                                etMarks
                                        .getText()
                                        .toString()
                                        .trim();


                        // Validate name

                        if (name.isEmpty()) {

                            etStudentName.setError(
                                    "Enter student name"
                            );

                            etStudentName.requestFocus();

                            return;
                        }


                        // Validate roll number

                        if (rollNumber.isEmpty()) {

                            etRollNumber.setError(
                                    "Enter roll number"
                            );

                            etRollNumber.requestFocus();

                            return;
                        }


                        // Validate course

                        if (spinnerCourse
                                .getSelectedItemPosition() == 0) {

                            Toast.makeText(
                                    AddStudentActivity.this,
                                    "Please select a course",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }


                        // Validate marks

                        if (marksText.isEmpty()) {

                            etMarks.setError(
                                    "Enter marks"
                            );

                            etMarks.requestFocus();

                            return;
                        }


                        // Convert marks

                        double marks;

                        try {

                            marks =
                                    Double.parseDouble(
                                            marksText
                                    );

                        } catch (NumberFormatException e) {

                            etMarks.setError(
                                    "Enter valid marks"
                            );

                            etMarks.requestFocus();

                            return;
                        }


                        // Check marks range

                        if (marks < 0 || marks > 100) {

                            etMarks.setError(
                                    "Marks must be between 0 and 100"
                            );

                            etMarks.requestFocus();

                            return;
                        }


                        // Insert into SQLite

                        boolean inserted =
                                databaseHelper.insertStudent(
                                        name,
                                        rollNumber,
                                        course,
                                        marks
                                );


                        if (inserted) {

                            Toast.makeText(
                                    AddStudentActivity.this,
                                    "Student saved successfully!",
                                    Toast.LENGTH_SHORT
                            ).show();


                            // Clear fields

                            etStudentName.setText("");

                            etRollNumber.setText("");

                            spinnerCourse.setSelection(0);

                            etMarks.setText("");


                            // Return to previous page

                            finish();

                        } else {

                            Toast.makeText(
                                    AddStudentActivity.this,
                                    "Failed to save student",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }
        );
    }
}