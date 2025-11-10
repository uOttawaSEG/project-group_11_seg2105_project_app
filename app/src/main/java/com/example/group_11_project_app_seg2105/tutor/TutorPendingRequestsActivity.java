package com.example.group_11_project_app_seg2105.tutor;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.SQLiteRegistrationRequestRepository;
import com.example.group_11_project_app_seg2105.data.RegistrationRequest;
import com.example.group_11_project_app_seg2105.data.RegistrationStatus;

import java.util.List;

public class TutorPendingRequestsActivity extends AppCompatActivity {

    private SQLiteRegistrationRequestRepository repo;
    private TutorPendingRequestsAdapter adapter;
    private String tutorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_pending);

        tutorEmail = getSharedPreferences("auth", MODE_PRIVATE).getString("email", "tutor@uottawa.ca");

        repo = new SQLiteRegistrationRequestRepository(this, new DatabaseHelper(this));

        RecyclerView list = findViewById(R.id.recyclerPending);
        list.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TutorPendingRequestsAdapter(this, repo, tutorEmail, this::refresh);
        list.setAdapter(adapter);

        refresh();
    }

    private void refresh() {
        List<RegistrationRequest> items = repo.findByStatus(RegistrationStatus.PENDING);
        adapter.setData(items);
    }
}
