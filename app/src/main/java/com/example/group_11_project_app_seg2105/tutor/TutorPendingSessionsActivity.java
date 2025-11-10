package com.example.group_11_project_app_seg2105.tutor;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.SessionRequest;
import java.util.List;

public class TutorPendingSessionsActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private String tutorEmail;
    private TutorPendingSessionsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_pending);

        tutorEmail = getIntent().getStringExtra("email");
        if (tutorEmail == null) {
            tutorEmail = getSharedPreferences("auth", MODE_PRIVATE).getString("email", "tutor@uottawa.ca");
        }

        db = new DatabaseHelper(this);

        RecyclerView list = findViewById(R.id.recyclerPending);
        list.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TutorPendingSessionsAdapter(this, db, tutorEmail, this::refresh);
        list.setAdapter(adapter);

        refresh();
    }

    private void refresh() {
        List<SessionRequest> requests = db.getPendingSessionRequests(tutorEmail);
        adapter.setData(requests);
    }
}
