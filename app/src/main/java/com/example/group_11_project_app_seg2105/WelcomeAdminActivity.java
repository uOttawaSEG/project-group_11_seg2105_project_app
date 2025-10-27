package com.example.group_11_project_app_seg2105;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;  // <-- add this import
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.group_11_project_app_seg2105.admin.AdminInboxActivity; // <-- add this import

/**
 * Displays welcome message for Admin and allows logout.
 */
public class WelcomeAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_admin);

        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    // 👇 Add this method below
    public void openInbox(View v) {
        Intent i = new Intent(this, AdminInboxActivity.class);
        startActivity(i);
    }
}
