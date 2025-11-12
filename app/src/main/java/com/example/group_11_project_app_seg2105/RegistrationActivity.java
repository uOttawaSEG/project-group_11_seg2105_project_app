package com.example.group_11_project_app_seg2105;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.group_11_project_app_seg2105.core.validation.InputValidator;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private EditText firstNameField, lastNameField, emailField, passwordField, confirmPasswordField, phoneNumberField, programOfStudyField;
    private Button registerButton;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        AutoCompleteTextView titleDropdown = findViewById(R.id.titleDropdown);
        if (titleDropdown != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.registration_roles_title,
                    android.R.layout.simple_dropdown_item_1line
            );
            titleDropdown.setAdapter(adapter);
            titleDropdown.setOnClickListener(v -> titleDropdown.showDropDown());
            titleDropdown.setOnItemClickListener((parent, view, position, id) -> {
                String selected = parent.getItemAtPosition(position).toString().toLowerCase();
                if (selected.contains("tutor")) {
                    startActivity(new Intent(this, TutorRegistrationActivity.class));
                    finish();
                } else {
                    titleDropdown.setText("Student Registration", false);
                }
            });
        }

        firstNameField = findViewById(R.id.firstNameField);
        lastNameField = findViewById(R.id.lastNameField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);
        phoneNumberField = findViewById(R.id.phoneNumberField);
        programOfStudyField = findViewById(R.id.programOfStudyField);
        registerButton = findViewById(R.id.registerButton);

        db = new DatabaseHelper(this);
        db.seedAdmin();

        registerButton.setOnClickListener(v -> handleRegister());

        TextView loginLink = findViewById(R.id.loginLink);
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });
    }

    private void handleRegister() {
        clearAllFieldErrors();

        String firstName = safeText(firstNameField);
        String lastName = safeText(lastNameField);
        String email = safeText(emailField);
        String password = safeText(passwordField);
        String confirmPassword = safeText(confirmPasswordField);
        String phone = safeText(phoneNumberField);
        String program = safeText(programOfStudyField);

        List<String> errors = InputValidator.validateStudent(firstName, lastName, email, password, confirmPassword, phone, program);
        if (!errors.isEmpty()) {
            applyFieldErrors(firstNameField, errors, "First name is required");
            applyFieldErrors(lastNameField, errors, "Last name is required");
            applyFieldErrors(emailField, errors, "Invalid email");
            applyFieldErrors(passwordField, errors, "Password must be at least 6 characters");
            applyFieldErrors(confirmPasswordField, errors, "Passwords do not match");
            applyFieldErrors(phoneNumberField, errors, "Phone must be digits only");
            applyFieldErrors(programOfStudyField, errors, "Program of study is required");
            Toast.makeText(this, bulletJoin(errors), Toast.LENGTH_LONG).show();
            return;
        }

        if (db.getUserRole(email) != null) {
            setError(emailField, "An account with this email already exists");
            Toast.makeText(this, "An account with this email already exists.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // ✅ Use simple role string: "student"
            db.saveUser("student", email, password);

            boolean success = db.insertFullRegistrationRequest(
                    firstName,
                    lastName,
                    email,
                    password,
                    "student",  // consistent role naming
                    phone,
                    program
            );

            if (success) {
                Toast.makeText(this, "Registration submitted. Await admin approval before logging in.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class).putExtra("prefill_email", email));
                finish();
            } else {
                Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error saving registration.", Toast.LENGTH_SHORT).show();
        }
    }

    private static String safeText(EditText text) {
        return text == null ? "" : text.getText().toString().trim();
    }

    private static String bulletJoin(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for (String s : dedupe(lines)) {
            if (sb.length() > 0) sb.append('\n');
            sb.append("• ").append(s);
        }
        return sb.toString();
    }

    private static List<String> dedupe(List<String> list) {
        return new ArrayList<>(new java.util.LinkedHashSet<>(list));
    }

    private void setError(EditText field, String message) {
        if (field == null) return;
        if (field.getParent() instanceof TextInputLayout) {
            ((TextInputLayout) field.getParent()).setError(message);
        } else {
            field.setError(message);
        }
    }

    private void clearError(EditText field) {
        if (field == null) return;
        if (field.getParent() instanceof TextInputLayout) {
            ((TextInputLayout) field.getParent()).setError(null);
        } else {
            field.setError(null);
        }
    }

    private void clearAllFieldErrors() {
        clearError(firstNameField);
        clearError(lastNameField);
        clearError(emailField);
        clearError(passwordField);
        clearError(confirmPasswordField);
        clearError(phoneNumberField);
        clearError(programOfStudyField);
    }

    private void applyFieldErrors(EditText field, List<String> errors, String matchesMessage) {
        for (String e : errors) {
            if (e.equalsIgnoreCase(matchesMessage) || e.toLowerCase().contains(matchesMessage.toLowerCase())) {
                setError(field, e);
                return;
            }
        }
    }
}
