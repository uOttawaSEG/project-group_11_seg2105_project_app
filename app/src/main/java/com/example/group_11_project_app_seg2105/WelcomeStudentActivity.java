package com.example.group_11_project_app_seg2105;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group_11_project_app_seg2105.student.StudentSessionActivity;

/**
 * Displays welcome message for Students and allows logout.
 */
public class WelcomeStudentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_student);

        Button btnMySessions = findViewById(R.id.btnMySessions);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        btnMySessions.setOnClickListener(v -> {
            startActivity(new Intent(this, StudentSessionActivity.class));
        });

        logoutBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
