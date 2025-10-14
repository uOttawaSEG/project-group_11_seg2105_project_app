package com.example.group_11_project_app_seg2105;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.core.validation.InputValidator;

/**
 * Handles login for all user roles using SQLite (DatabaseHelper) and InputValidator.
 * Combines both versions' logic while keeping all functionality required by Deliverable 1.
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

        // Registration link
        TextView registerLink = findViewById(R.id.registerLink);
        if (registerLink != null) {
            registerLink.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            });
        }

        // Prefill email if passed from RegistrationActivity
        String prefill = getIntent().getStringExtra("prefill_email");
        if (prefill != null) {
            emailField.setText(prefill);
        }

        // Handle login button click
        loginButton.setOnClickListener(this::handleLogin);
    }

    private void handleLogin(View v) {
        String email = emailField != null ? emailField.getText().toString().trim() : "";
        String password = passwordField != null ? passwordField.getText().toString().trim() : "";

        // Simple field validation
        if (!InputValidator.isValidEmail(email) || !InputValidator.isValidPassword(password)) {
            Toast.makeText(this, "Invalid email or password format", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Validate against database
            boolean valid = db.validateLogin(email, password);
            if (!valid) {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            String role = db.getUserRole(email);
            if (role == null) {
                Toast.makeText(this, "No role found for account", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent next;
            switch (role.toLowerCase()) {
                case "admin":
                    next = new Intent(this, WelcomeAdminActivity.class);
                    break;
                case "tutor":
                    next = new Intent(this, WelcomeTutorActivity.class);
                    break;
                case "student":
                    next = new Intent(this, WelcomeStudentActivity.class);
                    break;
                default:
                    Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
                    return;
            }

            startActivity(next);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            android.util.Log.e("LoginActivity", "Login error", e);
        }
    }
}
