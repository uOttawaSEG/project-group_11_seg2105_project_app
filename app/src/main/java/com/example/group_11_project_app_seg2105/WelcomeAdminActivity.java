package com.example.group_11_project_app_seg2105;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.group_11_project_app_seg2105.admin.AdminInboxActivity;
import com.example.group_11_project_app_seg2105.admin.RejectedRequestsActivity;

/**
 * Admin dashboard screen.
 * Routes to Pending, Rejected, and Logout.
 */
public class WelcomeAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_admin);

        Button inboxBtn = findViewById(R.id.btnInbox);
        Button rejectedBtn = findViewById(R.id.btnRejected);
        Button logoutBtn = findViewById(R.id.logoutBtn);

        // Pending Requests
        inboxBtn.setOnClickListener(v ->
                startActivity(new Intent(this, AdminInboxActivity.class))
        );

        // Rejected Requests
        rejectedBtn.setOnClickListener(v ->
                startActivity(new Intent(this, RejectedRequestsActivity.class))
        );

        // Logout
        logoutBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
