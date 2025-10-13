package com.example.group_11_project_app_seg2105.core.validation;

import android.util.Patterns;
import java.util.ArrayList;
import java.util.List;

public class InputValidator {

    public static boolean isNonEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static boolean isValidEmail(String s) {
        return s != null && Patterns.EMAIL_ADDRESS.matcher(s).matches();
    }

    public static boolean isValidPassword(String s) {
        return s != null && s.length() >= 6;
    }

    public static boolean isValidConfirmPassword(String s, String p) { return s!= null && s.equals(p); }

    public static boolean isNumericPhone(String s) {
        return s != null && s.matches("\\d+");
    }

    public static List<String> validateStudent(String fName, String lName, String email, String password, String confirmPassword,String phone, String program) {
        List<String> errors = new ArrayList<>();

        if(!isNonEmpty(fName)) errors.add("First Name is required");
        if(!isNonEmpty(lName)) errors.add("Last name is required");
        if (!isValidEmail(email)) errors.add("Invalid email");
        if (!isValidPassword(password)) errors.add("Password must be at least 6 characters");
        if (!isNumericPhone(phone)) errors.add("Phone must be digits only");
        if (!isNonEmpty(program)) errors.add("Program of study is required");
        if (!isValidConfirmPassword(confirmPassword, password)) errors.add("Passwords do not match");

        return errors;
    }

    public static List<String> validateTutor(String email, String password, String phone, String degree, String course) {
        List<String> errors = new ArrayList<>();

        if (!isValidEmail(email)) errors.add("Invalid email");
        if (!isValidPassword(password)) errors.add("Password must be at least 6 characters");
        if (!isNumericPhone(phone)) errors.add("Phone must be digits only");
        if (!isNonEmpty(degree)) errors.add("Highest degree is required");
        if (!isNonEmpty(course)) errors.add("At least one course must be specified");

        return errors;
    }
}
