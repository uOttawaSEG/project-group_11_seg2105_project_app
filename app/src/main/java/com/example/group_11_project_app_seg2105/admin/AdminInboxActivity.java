package com.example.group_11_project_app_seg2105.admin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.RegistrationRequest;
import java.util.ArrayList;

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

        adapter = new PendingRequestAdapter(pendingList, db, this);
        recyclerPending.setAdapter(adapter);
    }
}
