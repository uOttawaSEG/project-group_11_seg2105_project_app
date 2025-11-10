package com.example.group_11_project_app_seg2105.tutor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.SessionRequest;
import com.example.group_11_project_app_seg2105.data.StudentProfile;
import com.example.group_11_project_app_seg2105.sessions.SessionEvents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TutorSessionsActivity extends AppCompatActivity implements SessionEvents.Listener {

    private DatabaseHelper db;
    private String tutorEmail;
    private TutorSessionsAdapter upcomingAdapter;
    private TutorSessionsAdapter pastAdapter;
    private TextView emptyUpcoming;
    private TextView emptyPast;
    private final Map<String, StudentProfile> studentProfiles = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_sessions);

        tutorEmail = getIntent().getStringExtra("email");
        if (tutorEmail == null) {
            tutorEmail = getSharedPreferences("auth", MODE_PRIVATE).getString("email", "tutor@uottawa.ca");
        }

        db = new DatabaseHelper(this);

        RecyclerView listUpcoming = findViewById(R.id.recyclerUpcomingSessions);
        RecyclerView listPast = findViewById(R.id.recyclerPastSessions);
        emptyUpcoming = findViewById(R.id.textEmptyUpcoming);
        emptyPast = findViewById(R.id.textEmptyPast);

        listUpcoming.setLayoutManager(new LinearLayoutManager(this));
        listPast.setLayoutManager(new LinearLayoutManager(this));

        upcomingAdapter = new TutorSessionsAdapter(this::showStudentInfoDialog);
        pastAdapter = new TutorSessionsAdapter(null);

        listUpcoming.setAdapter(upcomingAdapter);
        listPast.setAdapter(pastAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SessionEvents.register(this);
        refreshSessions();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SessionEvents.unregister(this);
    }

    private void refreshSessions() {
        List<SessionRequest> sessions = db.getAllSessionsForTutor(tutorEmail);
        TutorSessionPartitioner.Result result = TutorSessionPartitioner.partition(sessions, System.currentTimeMillis());

        studentProfiles.clear();
        studentProfiles.putAll(db.getStudentProfilesByEmails(result.studentEmails));

        upcomingAdapter.submit(result.upcoming, studentProfiles);
        pastAdapter.submit(result.past, studentProfiles);

        emptyUpcoming.setVisibility(result.upcoming.isEmpty() ? View.VISIBLE : View.GONE);
        emptyPast.setVisibility(result.past.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showStudentInfoDialog(SessionRequest session) {
        StudentProfile profile = studentProfiles.get(session.studentEmail);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_student_info, null);

        TextView name = view.findViewById(R.id.textStudentName);
        TextView email = view.findViewById(R.id.textStudentEmail);
        TextView phone = view.findViewById(R.id.textStudentPhone);
        TextView program = view.findViewById(R.id.textStudentProgram);

        if (profile != null) {
            name.setText(profile.getDisplayName());
            email.setText(profile.email);
            phone.setText(profile.phone != null ? profile.phone : "Phone not provided");
            program.setText(profile.program != null ? profile.program : "Program not provided");
        } else {
            name.setText(session.studentEmail);
            email.setText(session.studentEmail);
            phone.setText("Phone not provided");
            program.setText("Program not provided");
        }

        new AlertDialog.Builder(this)
                .setTitle("Student Information")
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onSessionStatusChanged(long sessionId, String newStatus) {
        runOnUiThread(this::refreshSessions);
    }
}
