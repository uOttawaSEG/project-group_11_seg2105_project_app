package com.example.group_11_project_app_seg2105.student;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.User;

import java.util.List;

public class StudentTutorListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_tutor_list);

        RecyclerView recycler = findViewById(R.id.recyclerTutors);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        DatabaseHelper db = new DatabaseHelper(this);
        List<User> tutors = db.getUsersByRole("tutor");

        StudentTutorListAdapter adapter = new StudentTutorListAdapter(this, tutors);
        recycler.setAdapter(adapter);
    }
}
