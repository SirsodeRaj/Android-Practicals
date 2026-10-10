package com.example.studentrecords;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        // Add Student Card
        LinearLayout addStudentCard =
                findViewById(R.id.cardAddStudent);

        addStudentCard.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(
                                MainActivity.this,
                                AddStudentActivity.class
                        );

                        startActivity(intent);
                    }
                }
        );


        // View Students Card
        LinearLayout viewStudentsCard =
                findViewById(R.id.cardViewStudents);

        viewStudentsCard.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(
                                MainActivity.this,
                                ViewStudentsActivity.class
                        );

                        startActivity(intent);
                    }
                }
        );


        // Load dashboard
        loadDashboardData();
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Refresh dashboard after returning
        if (databaseHelper != null) {
            loadDashboardData();
        }
    }


    private void loadDashboardData() {

        // Total students
        TextView totalStudentsText =
                findViewById(R.id.tvTotalStudents);

        int totalStudents =
                databaseHelper.getStudentCount();

        totalStudentsText.setText(
                String.valueOf(totalStudents)
        );


        // Recent students
        TextView recentStudentsText =
                findViewById(R.id.tvRecentStudents);

        Cursor cursor =
                databaseHelper.getAllStudents();


        // No students
        if (cursor.getCount() == 0) {

            recentStudentsText.setText(
                    "No students added yet."
            );

            cursor.close();

            return;
        }


        // Display recent students
        StringBuilder recentStudents =
                new StringBuilder();

        int count = 0;


        while (cursor.moveToNext() && count < 3) {

            String name =
                    cursor.getString(1);

            String rollNumber =
                    cursor.getString(2);

            String course =
                    cursor.getString(3);

            double marks =
                    cursor.getDouble(4);


            recentStudents.append(name);
            recentStudents.append("\n");

            recentStudents.append("Roll No: ");
            recentStudents.append(rollNumber);
            recentStudents.append("\n");

            recentStudents.append("Course: ");
            recentStudents.append(course);
            recentStudents.append("\n");

            recentStudents.append("Marks: ");
            recentStudents.append(marks);
            recentStudents.append("\n\n");

            count++;
        }


        cursor.close();


        recentStudentsText.setText(
                recentStudents.toString()
        );
    }
}