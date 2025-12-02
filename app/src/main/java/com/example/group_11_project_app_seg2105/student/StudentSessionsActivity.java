package com.example.group_11_project_app_seg2105.student;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.SessionRequest;
import com.example.group_11_project_app_seg2105.sessions.SessionEvents;

import java.util.List;

public class StudentSessionsActivity extends AppCompatActivity implements SessionEvents.Listener {

    private DatabaseHelper db;
    private String studentEmail;
    private StudentSessionsAdapter adapter;
    private TextView emptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sessions);

        studentEmail = getIntent().getStringExtra("email");
        if (studentEmail == null) {
            studentEmail = getSharedPreferences("auth", MODE_PRIVATE)
                    .getString("email", "student@uottawa.ca");
        }

        db = new DatabaseHelper(this);

        RecyclerView recycler = findViewById(R.id.recyclerStudentSessions);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentSessionsAdapter(this, db, this::refresh);
        recycler.setAdapter(adapter);

        emptyView = findViewById(R.id.textEmptySessions);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SessionEvents.register(this);
        refresh();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SessionEvents.unregister(this);
    }

    private void refresh() {
        List<SessionRequest> items = db.getAllSessionsForStudent(studentEmail);
        adapter.setData(items);
        emptyView.setVisibility(items == null || items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onSessionStatusChanged(long sessionId, String newStatus) {
        runOnUiThread(this::refresh);
    }
}
