package com.example.group_11_project_app_seg2105;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.example.group_11_project_app_seg2105.core.validation.InputValidator;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.RegistrationStatus;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class TutorRegistrationActivity extends AppCompatActivity {

    private EditText firstNameField, lastNameField, emailField, passwordField, confirmPasswordField, phoneNumberField, highestDegree;
    private Button registerButton;
    private TextView loginLink;
    private NestedScrollView scroll;

    private TextInputLayout courseInputLayout;
    private TextInputEditText courseInput;
    private ChipGroup coursesChipGroup;
    private final LinkedHashSet<String> tutorCourses = new LinkedHashSet<>();

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_registration);

        MaterialAutoCompleteTextView titleDropdown = findViewById(R.id.titleDropdown);
        ArrayAdapter<CharSequence> roles = ArrayAdapter.createFromResource(this, R.array.registration_roles_title, android.R.layout.simple_dropdown_item_1line);
        titleDropdown.setAdapter(roles);
        titleDropdown.setOnClickListener(v -> titleDropdown.showDropDown());
        titleDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String choice = parent.getItemAtPosition(position).toString();
            if (choice.toLowerCase().contains("student")) {
                startActivity(new Intent(this, RegistrationActivity.class));
                finish();
            } else {
                titleDropdown.setText("Tutor Registration", false);
            }
        });

        scroll = findViewById(R.id.scroll);
        firstNameField = findViewById(R.id.firstNameField);
        lastNameField = findViewById(R.id.lastNameField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);
        phoneNumberField = findViewById(R.id.phoneNumberField);
        highestDegree = findViewById(R.id.highestDegree);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);

        courseInputLayout = findViewById(R.id.courseInputLayout);
        courseInput = findViewById(R.id.courseInput);
        coursesChipGroup = findViewById(R.id.coursesChipGroup);

        db = new DatabaseHelper(this);
        db.seedAdmin();

        courseInputLayout.setEndIconOnClickListener(v -> addCourseFromInput());
        courseInput.setOnEditorActionListener((tv, actionID, event) -> {
            if (actionID == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)) {
                addCourseFromInput();
                return true;
            }
            return false;
        });

        registerButton.setOnClickListener(this::handleRegister);

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void handleRegister(View v) {
        clearAllFieldErrors();

        String firstName = safeText(firstNameField);
        String lastName = safeText(lastNameField);
        String email = safeText(emailField);
        String password = safeText(passwordField);
        String confirmPassword = safeText(confirmPasswordField);
        String phone = safeText(phoneNumberField);
        String degree = safeText(highestDegree);
        List<String> courseList = new ArrayList<>(tutorCourses);

        List<String> errors = InputValidator.validateTutor(firstName, lastName, email, password, confirmPassword, phone, degree, courseList);
        if (!errors.isEmpty()) {
            applyFieldErrors(firstNameField, errors, "First name is required");
            applyFieldErrors(lastNameField, errors, "Last name is required");
            applyFieldErrors(emailField, errors, "Invalid email");
            applyFieldErrors(passwordField, errors, "Password must be at least 6 characters");
            applyFieldErrors(confirmPasswordField, errors, "Passwords do not match");
            applyFieldErrors(phoneNumberField, errors, "Phone must be digits only");
            applyFieldErrors(highestDegree, errors, "Highest degree is required");
            Toast.makeText(this, bulletJoin(errors), Toast.LENGTH_LONG).show();
            return;
        }

        // Check if account already exists
        if (db.getUserRole(email) != null) {
            setError(emailField, "An account with this email already exists.");
            scrollToView(emailField);
            return;
        }

        // Create user + tutor profile
        boolean ok = db.createTutorWithProfile(email, password, firstName, lastName, phone, degree, courseList);
        if (!ok) {
            setError(emailField, "An account with this email already exists.");
            return;
        }

        // Insert a pending registration request
        db.insertRegistrationRequest(email, "tutor", firstName, lastName, password, phone, degree);

        Toast.makeText(this, "Registration submitted. Await admin approval before logging in.", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, LoginActivity.class).putExtra("prefill_email", email));
        finish();
    }

    private void addCourseFromInput() {
        if (courseInput == null || courseInputLayout == null || coursesChipGroup == null) return;

        String raw = safeText(courseInput).toUpperCase().replaceAll("\\s+", "");
        if (raw.isEmpty()) {
            setError(courseInputLayout, "Add at least one course");
            return;
        }
        if (!tutorCourses.add(raw)) {
            setError(courseInputLayout, "Course already added");
            return;
        }
        setError(courseInputLayout, null);
        addChip(raw);
        courseInput.setText("");
    }

    private void addChip(String label) {
        if (coursesChipGroup == null) return;
        Chip chip = new Chip(this);
        chip.setText(label);
        chip.setCheckable(false);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            tutorCourses.remove(label);
            coursesChipGroup.removeView(chip);
            if (tutorCourses.isEmpty()) {
                setError(courseInputLayout, "Add at least one course");
            }
        });
        coursesChipGroup.addView(chip);
    }

    // --- helpers ---
    private static String safeText(TextView tv) {
        return tv == null ? "" : String.valueOf(tv.getText()).trim();
    }

    private static String bulletJoin(List<String> lines) {
        List<String> dedup = new ArrayList<>(new LinkedHashSet<>(lines));
        StringBuilder sb = new StringBuilder();
        for (String s : dedup) {
            if (sb.length() > 0) sb.append('\n');
            sb.append("• ").append(s);
        }
        return sb.toString();
    }

    private void setError(View field, String message) {
        if (field == null) return;
        if (field instanceof TextInputLayout) {
            ((TextInputLayout) field).setError(message);
            return;
        }
        if (field instanceof EditText) {
            ViewParent parent = field.getParent();
            if (parent instanceof TextInputLayout) {
                ((TextInputLayout) parent).setError(message);
            } else {
                ((EditText) field).setError(message);
            }
        }
    }

    private void clearError(View field) {
        setError(field, null);
    }

    private void applyFieldErrors(View field, List<String> errors, String matchesMessage) {
        if (field == null || errors == null || matchesMessage == null) return;
        for (String e : errors) {
            if (e.equalsIgnoreCase(matchesMessage) || e.toLowerCase().contains(matchesMessage.toLowerCase())) {
                setError(field, e);
                return;
            }
        }
        clearError(field);
    }

    private void clearAllFieldErrors() {
        clearError(firstNameField);
        clearError(lastNameField);
        clearError(emailField);
        clearError(passwordField);
        clearError(confirmPasswordField);
        clearError(phoneNumberField);
        clearError(highestDegree);
        clearError(courseInputLayout);
    }

    private void scrollToView(View target) {
        if (scroll == null || target == null) return;
        scroll.post(() -> scroll.smoothScrollTo(0, Math.max(0, target.getTop() - 24)));
    }
}
