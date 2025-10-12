package com.example.group_11_project_app_seg2105;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.core.validation.InputValidator;

/**
 * Handles login for all user roles using SQLite (DatabaseHelper) and InputValidator.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginButton;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);

        // Initialize SQLite and seed admin
        db = new DatabaseHelper(this);
        db.seedAdmin();

        loginButton.setOnClickListener(this::handleLogin);
    }

    private void handleLogin(View v) {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        // Validate input
        if (!InputValidator.isValidEmail(email) || !InputValidator.isValidPassword(password)) {
            Toast.makeText(this, "Invalid email or password format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate against SQLite DB
        if (db.validateLogin(email, password)) {
            String role = db.getUserRole(email);

            if ("admin".equalsIgnoreCase(role)) {
                startActivity(new Intent(this, WelcomeAdminActivity.class));
            } else if ("tutor".equalsIgnoreCase(role)) {
                startActivity(new Intent(this, WelcomeTutorActivity.class));
            } else if ("student".equalsIgnoreCase(role)) {
                startActivity(new Intent(this, WelcomeStudentActivity.class));
            } else {
                Toast.makeText(this, "Unknown role", Toast.LENGTH_SHORT).show();
                return;
            }

            finish();
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }
    }
}
