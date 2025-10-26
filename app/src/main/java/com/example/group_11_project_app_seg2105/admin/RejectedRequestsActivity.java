package com.example.group_11_project_app_seg2105.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.group_11_project_app_seg2105.R;
import com.example.group_11_project_app_seg2105.data.DatabaseHelper;
import com.example.group_11_project_app_seg2105.data.RegistrationRequest;


import java.util.ArrayList;
import java.util.List;

public class RejectedRequestsActivity extends AppCompatActivity {
    private RecyclerView recycler;
    private View emptyState;
    private TextView countBadge;
    private RejectedRequestsAdapter adapter;
    private DatabaseHelper db;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_rejected_requests);

        db = new DatabaseHelper(this);
        recycler = findViewById(R.id.recyclerRejected);
        emptyState = findViewById(R.id.emptyState);
        countBadge = findViewById(R.id.countBadge);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RejectedRequestsAdapter(new ArrayList<>(), (req, pos)-> {
            boolean ok = db.reapproveRejected(req.id, "admin@uottawa.ca", "Re-approved");
            if(ok) {
                adapter.removeAt(pos);
                updateState(adapter.getItemCount());
                Toast.makeText(this, "Re-approved: "+ req.email, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No action (already changed?)", Toast.LENGTH_SHORT).show();

            }

        });
        recycler.setAdapter(adapter);

        findViewById(R.id.refreshButton).setOnClickListener(v ->refresh());


    }

    @Override protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        List<RegistrationRequest> items = db.getRejectedRequests();
        adapter.submit(items);
        updateState(items.size());
    }

    private void updateState(int count) {
        countBadge.setText(String.valueOf(count));
        emptyState.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        recycler.setVisibility(count == 0 ? View.GONE : View.VISIBLE);

    }

}
