package com.example.group_11_project_app_seg2105.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.RegistrationRequest;

import java.util.ArrayList;

/**
 * Admin inbox that shows all pending registration requests.
 * Includes email notification when a request is approved or rejected.
 */
public class AdminInboxActivity extends AppCompatActivity {

    private RecyclerView recyclerPending;
    private PendingRequestAdapter adapter;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_inbox);

        recyclerPending = findViewById(R.id.recyclerPending);
        recyclerPending.setLayoutManager(new LinearLayoutManager(this));

        db = new DatabaseHelper(this);
        ArrayList<RegistrationRequest> pendingList = db.getPendingRequests();

        if (pendingList == null || pendingList.isEmpty()) {
            Toast.makeText(this, "No pending registration requests found.", Toast.LENGTH_SHORT).show();
        }

        // Adapter includes callback to trigger notifications
        adapter = new PendingRequestAdapter(pendingList, db, this,
                (email, newStatus) -> handleStatusChange(email, newStatus));
        recyclerPending.setAdapter(adapter);
    }

    /**
     * Handles updating registration status and sending email notification.
     */
    private void handleStatusChange(String userEmail, String newStatus) {
        db.updateRequestStatus(userEmail, newStatus);

        String subject;
        String message;

        if ("APPROVED".equalsIgnoreCase(newStatus)) {
            subject = "Account Approved";
            message = "Your account has been approved by the admin. You may now log in.";
        } else if ("REJECTED".equalsIgnoreCase(newStatus)) {
            subject = "Account Rejected";
            message = "Your registration was rejected. Contact the admin for clarification.";
        } else {
            return;
        }

        sendEmailNotification(userEmail, subject, message);
    }

    /**
     * Launches email app to notify the user.
     */
    private void sendEmailNotification(String userEmail, String subject, String message) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{userEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email app found.", Toast.LENGTH_SHORT).show();
        }
    }
}
