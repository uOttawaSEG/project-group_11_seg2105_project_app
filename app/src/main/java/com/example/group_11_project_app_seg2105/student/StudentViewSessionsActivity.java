package com.example.group_11_project_app_seg2105.student;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.SessionRequest;

import java.util.ArrayList;
import java.util.List;

public class StudentViewSessionsActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private String studentEmail;
    private StudentSessionsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_view_sessions);

        db = new DatabaseHelper(this);
        studentEmail = getSharedPreferences("auth", MODE_PRIVATE)
                .getString("email", "student@uottawa.ca");

        RecyclerView recycler = findViewById(R.id.recyclerSessions);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentSessionsAdapter(new ArrayList<>());
        recycler.setAdapter(adapter);

        loadSessions();
    }

    private void loadSessions() {
        List<SessionRequest> sessions = db.getSessionsForStudent(studentEmail);
        adapter.setData(sessions);
    }
}
