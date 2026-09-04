package com.example.studentrecords;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ViewStudentsActivity extends AppCompatActivity {

    TextView tvStudentCount;
    TextView tvEmptyMessage;
    Button btnBack;

    LinearLayout recordsContainer;

    DatabaseHelper databaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_students);


        // Connect XML components

        tvStudentCount =
                findViewById(R.id.tvStudentCount);

        tvEmptyMessage =
                findViewById(R.id.tvEmptyMessage);

        btnBack = findViewById(R.id.btnBack);

        recordsContainer =
                findViewById(R.id.recordsContainer);


        // Database

        databaseHelper =
                new DatabaseHelper(this);


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
    }


    @Override
    protected void onResume() {
        super.onResume();

        loadStudents();
    }


    // -----------------------------------------
    // LOAD STUDENTS
    // -----------------------------------------

    private void loadStudents() {

        // Get total students

        int totalStudents =
                databaseHelper.getStudentCount();

        tvStudentCount.setText(
                String.valueOf(totalStudents)
        );


        // Get records

        Cursor cursor =
                databaseHelper.getAllStudents();


        // Remove old student cards

        int childCount =
                recordsContainer.getChildCount();

        if (childCount > 1) {

            recordsContainer.removeViews(
                    1,
                    childCount - 1
            );
        }


        // No records

        if (cursor.getCount() == 0) {

            tvEmptyMessage.setVisibility(
                    View.VISIBLE
            );

            cursor.close();

            return;
        }


        // Hide empty message

        tvEmptyMessage.setVisibility(
                View.GONE
        );


        // Read records

        while (cursor.moveToNext()) {

            int id =
                    cursor.getInt(0);

            String name =
                    cursor.getString(1);

            String rollNumber =
                    cursor.getString(2);

            String course =
                    cursor.getString(3);

            double marks =
                    cursor.getDouble(4);


            // Student Card

            LinearLayout studentCard =
                    new LinearLayout(this);

            studentCard.setOrientation(
                    LinearLayout.VERTICAL
            );

            studentCard.setPadding(
                    20,
                    20,
                    20,
                    20
            );

            studentCard.setBackgroundColor(
                    Color.rgb(245, 245, 245)
            );


            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            cardParams.setMargins(
                    0,
                    0,
                    0,
                    16
            );

            studentCard.setLayoutParams(
                    cardParams
            );


            // Student Name

            TextView nameText =
                    new TextView(this);

            nameText.setText(name);

            nameText.setTextSize(20);

            nameText.setTypeface(
                    null,
                    Typeface.BOLD
            );

            nameText.setTextColor(
                    Color.rgb(26, 35, 126)
            );


            // Roll Number

            TextView rollText =
                    new TextView(this);

            rollText.setText(
                    "Roll No: " + rollNumber
            );

            rollText.setTextSize(16);

            rollText.setTextColor(
                    Color.DKGRAY
            );


            // Course

            TextView courseText =
                    new TextView(this);

            courseText.setText(
                    "Course: " + course
            );

            courseText.setTextSize(16);

            courseText.setTextColor(
                    Color.DKGRAY
            );


            // Marks

            TextView marksText =
                    new TextView(this);

            marksText.setText(
                    "Marks: " + marks
            );

            marksText.setTextSize(16);

            marksText.setTextColor(
                    Color.DKGRAY
            );


            // Add information

            studentCard.addView(nameText);

            studentCard.addView(rollText);

            studentCard.addView(courseText);

            studentCard.addView(marksText);


            // -----------------------------------------
            // DELETE BUTTON
            // -----------------------------------------

            Button deleteButton =
                    new Button(this);

            deleteButton.setText(
                    "DELETE"
            );

            deleteButton.setTextSize(14);


            deleteButton.setOnClickListener(
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            new AlertDialog.Builder(
                                    ViewStudentsActivity.this
                            )
                                    .setTitle(
                                            "Delete Student"
                                    )
                                    .setMessage(
                                            "Are you sure you want to delete "
                                                    + name + "?"
                                    )
                                    .setPositiveButton(
                                            "Delete",
                                            (dialog, which) -> {

                                                boolean deleted =
                                                        databaseHelper
                                                                .deleteStudent(id);


                                                if (deleted) {

                                                    Toast.makeText(
                                                            ViewStudentsActivity.this,
                                                            "Student deleted successfully",
                                                            Toast.LENGTH_SHORT
                                                    ).show();

                                                    loadStudents();

                                                } else {

                                                    Toast.makeText(
                                                            ViewStudentsActivity.this,
                                                            "Failed to delete student",
                                                            Toast.LENGTH_SHORT
                                                    ).show();
                                                }
                                            }
                                    )
                                    .setNegativeButton(
                                            "Cancel",
                                            null
                                    )
                                    .show();
                        }
                    }
            );


            studentCard.addView(
                    deleteButton
            );


            // Add card

            recordsContainer.addView(
                    studentCard
            );
        }


        cursor.close();
    }
}