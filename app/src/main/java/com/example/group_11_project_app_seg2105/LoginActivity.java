package com.example.group_11_project_app_seg2105;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group_11_project_app_seg2105.data.DatabaseHelper;

/**
 * Handles login for all user roles.
 * Admin credentials are hardcoded:
 * admin@uottawa.ca / admin123
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

<<<<<<< Updated upstream
        loginButton.setOnClickListener(v -> handleLogin());

        // Initialize SQLite and seed admin
        db = new DatabaseHelper(this);
        db.seedAdmin();


        TextView registerLink = findViewById(R.id.registerLink);
        if (registerLink != null) {
            registerLink.setOnClickListener(v ->
                    startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));
        }

        String prefill = getIntent().getStringExtra("prefill_email");
        if(prefill != null) {
            emailField.setText(prefill);
        }

        loginButton.setOnClickListener(v -> handleLogin());

=======
        loginButton.setOnClickListener(this::handleLogin);
>>>>>>> Stashed changes
    }

    private void handleLogin() {
        String email = emailField != null ? emailField.getText().toString().trim() : "";
        String password = passwordField != null ? passwordField.getText().toString().trim() : "";


        boolean hasErr = false;
        if (email.isEmpty()) { if (emailField != null) emailField.setError("Email required"); hasErr = true; }
        if (password.isEmpty()) { if (passwordField != null) passwordField.setError("Password required"); hasErr = true; }
        if (hasErr) { Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show(); return; }


        // Hardcoded admin account
        if (email.equals("admin@uottawa.ca") && password.equals("admin123")) {
            startActivity(new Intent(this, WelcomeAdminActivity.class));
            finish();
            return;
        }


        try {
            if (db == null) db = new DatabaseHelper(this);

            boolean ok = db.validateLogin(email, password);
            if (!ok) { Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show(); return; }

            String role = db.getUserRole(email);
            if (role == null) { Toast.makeText(this, "Account has no role set.", Toast.LENGTH_SHORT).show(); return; }

            Intent next;
            switch (role.toLowerCase()) {
                case "admin":   next = new Intent(this, WelcomeAdminActivity.class); break;
                case "tutor":   next = new Intent(this, WelcomeTutorActivity.class); break;
                case "student": next = new Intent(this, WelcomeStudentActivity.class); break;
                default:        Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_SHORT).show(); return;
            }
            startActivity(next);
            finish();

        } catch (Exception e) {
            // Show the underlying cause instead of crashing
            String msg = e.getMessage();
            Toast.makeText(this, "Login failed: " + (msg == null ? e.getClass().getSimpleName() : msg), Toast.LENGTH_LONG).show();
            android.util.Log.e("LoginActivity", "Login crash", e);
        }
    }

}



