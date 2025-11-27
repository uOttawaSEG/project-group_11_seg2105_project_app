package com.example.group_11_project_app_seg2105.student;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.SessionRequest;

import java.util.ArrayList;
import java.util.List;

public class StudentSessionsActivity extends AppCompatActivity {

    public static final String EXTRA_STUDENT_EMAIL = "student_email";

    private DatabaseHelper db;
    private StudentSessionAdapter adapter;
    private RecyclerView recycler;
    private View emptyState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sessions);

        db = new DatabaseHelper(this);
        recycler = findViewById(R.id.recyclerSessions);
        emptyState = findViewById(R.id.emptyState);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentSessionAdapter(new ArrayList<>());
        recycler.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        String studentEmail = getIntent().getStringExtra(EXTRA_STUDENT_EMAIL);
        if(studentEmail == null) {
            // fallback - no email passed, show empty
            adapter.submit(new ArrayList<>());
            updateState(0);
            return;
        }

        List<SessionRequest> sessions = db.getSessionsForStudent(studentEmail);
        adapter.submit(sessions);
        updateState(sessions.size());
    }

    private void updateState(int count) {
        emptyState.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        recycler.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
    }

}
