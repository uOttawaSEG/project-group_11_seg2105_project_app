package com.example.group_11_project_app_seg2105.data;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONObject;
import org.json.JSONException;
import com.example.group_11_project_app_seg2105.core.validation.InputValidator;

public class PrefsUserStore {

    private static final String PREF_NAME = "OTAMS_PREFS";
    private final SharedPreferences prefs;

    public PrefsUserStore(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Save a user by role (student, tutor, admin)
    public void saveUser(String role, String email, String password) {
        try {
            JSONObject user = new JSONObject();
            user.put("role", role);
            user.put("email", email);
            user.put("password", password);

            prefs.edit().putString(email, user.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Retrieve user by email
    public JSONObject getUser(String email) {
        String data = prefs.getString(email, null);
        if (data == null) return null;

        try {
            return new JSONObject(data);
        } catch (JSONException e) {
            return null;
        }
    }

    // Validate login credentials
    public boolean validateLogin(String email, String password) {
        JSONObject user = getUser(email);
        if (user == null) return false;

        return password.equals(user.optString("password"));
    }

    // Return role for a logged-in user
    public String getUserRole(String email) {
        JSONObject user = getUser(email);
        if (user == null) return null;

        return user.optString("role");
    }

    // Preload default admin
    public void seedAdmin() {
        if (getUser("admin@uottawa.ca") == null) {
            saveUser("admin", "admin@uottawa.ca", "admin123");
        }
    }

    // Validate user input before saving or login
    public boolean validateUserInput(String email, String password) {
        if (!InputValidator.isValidEmail(email)) {
            return false;
        }
        if (!InputValidator.isValidPassword(password)) {
            return false;
        }
        return true;
    }
    public boolean getAutoApprove(String email) {
        return prefs.getBoolean("autoApprove_" + email, false);
    }

    public void setAutoApprove(String email, boolean value) {
        prefs.edit().putBoolean("autoApprove_" + email, value).apply();
    }

}
