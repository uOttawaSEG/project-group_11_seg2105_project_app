package com.example.group_11_project_app_seg2105.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SQLiteRegistrationRequestRepository implements RegistrationRequestRepository {
    private static final String BASE_QUERY = "SELECT r." + DatabaseContract.RegistrationRequests.ID + ", r." + DatabaseContract.RegistrationRequests.EMAIL + ", COALESCE(s." + DatabaseContract.StudentProfiles.FIRST + ", t." + DatabaseContract.TutorProfiles.FIRST + ") AS first_name, COALESCE(s." + DatabaseContract.StudentProfiles.LAST + ", t." + DatabaseContract.TutorProfiles.LAST + ") AS last_name, u." + DatabaseContract.Users.ROLE + ", r." + DatabaseContract.RegistrationRequests.STATUS + ", r." + DatabaseContract.RegistrationRequests.REASON + ", r." + DatabaseContract.RegistrationRequests.CREATED_AT + ", r." + DatabaseContract.RegistrationRequests.DECIDED_BY + ", r." + DatabaseContract.RegistrationRequests.DECIDED_AT + " FROM " + DatabaseContract.RegistrationRequests.TABLE + " r LEFT JOIN " + DatabaseContract.Users.TABLE + " u ON r." + DatabaseContract.RegistrationRequests.EMAIL + " = u." + DatabaseContract.Users.EMAIL + " LEFT JOIN " + DatabaseContract.StudentProfiles.TABLE + " s ON r." + DatabaseContract.RegistrationRequests.EMAIL + " = s." + DatabaseContract.StudentProfiles.EMAIL + " LEFT JOIN " + DatabaseContract.TutorProfiles.TABLE + " t ON r." + DatabaseContract.RegistrationRequests.EMAIL + " = t." + DatabaseContract.TutorProfiles.EMAIL;
    private final DatabaseHelper helper;

    public SQLiteRegistrationRequestRepository(DatabaseHelper helper) {
        this.helper = helper;
    }

    @Override
    public RegistrationRequest create(String email, RegistrationStatus status, String reason) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.RegistrationRequests.EMAIL, email);
        values.put(DatabaseContract.RegistrationRequests.CREATED_AT, System.currentTimeMillis());
        values.put(DatabaseContract.RegistrationRequests.STATUS, status.name());
        if (reason == null) {
            values.putNull(DatabaseContract.RegistrationRequests.REASON);
        } else {
            values.put(DatabaseContract.RegistrationRequests.REASON, reason);
        }
        long id = db.insert(DatabaseContract.RegistrationRequests.TABLE, null, values);
        if (id == -1) {
            return null;
        }
        return findById(id);
    }

    @Override
    public RegistrationRequest findById(long id) {
        List<RegistrationRequest> results = query("r." + DatabaseContract.RegistrationRequests.ID + "=?", new String[]{String.valueOf(id)}, null);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<RegistrationRequest> findAll() {
        return query(null, null, "r." + DatabaseContract.RegistrationRequests.CREATED_AT + " DESC");
    }

    @Override
    public List<RegistrationRequest> findByStatus(RegistrationStatus status) {
        return query("r." + DatabaseContract.RegistrationRequests.STATUS + "=?", new String[]{status.name()}, "r." + DatabaseContract.RegistrationRequests.CREATED_AT + " DESC");
    }

    @Override
    public boolean updateStatus(long id, RegistrationStatus status, String decidedBy, String reason) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.RegistrationRequests.STATUS, status.name());
        if (status == RegistrationStatus.PENDING) {
            values.putNull(DatabaseContract.RegistrationRequests.DECIDED_BY);
            values.putNull(DatabaseContract.RegistrationRequests.DECIDED_AT);
        } else {
            values.put(DatabaseContract.RegistrationRequests.DECIDED_BY, decidedBy);
            values.put(DatabaseContract.RegistrationRequests.DECIDED_AT, System.currentTimeMillis());
        }
        if (reason == null) {
            values.putNull(DatabaseContract.RegistrationRequests.REASON);
        } else {
            values.put(DatabaseContract.RegistrationRequests.REASON, reason);
        }
        int affected = db.update(DatabaseContract.RegistrationRequests.TABLE, values, DatabaseContract.RegistrationRequests.ID + "=?", new String[]{String.valueOf(id)});
        return affected > 0;
    }

    @Override
    public boolean delete(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int affected = db.delete(DatabaseContract.RegistrationRequests.TABLE, DatabaseContract.RegistrationRequests.ID + "=?", new String[]{String.valueOf(id)});
        return affected > 0;
    }

    private List<RegistrationRequest> query(String where, String[] args, String orderBy) {
        SQLiteDatabase db = helper.getReadableDatabase();
        StringBuilder sql = new StringBuilder(BASE_QUERY);
        if (where != null && !where.isEmpty()) {
            sql.append(" WHERE ").append(where);
        }
        if (orderBy != null && !orderBy.isEmpty()) {
            sql.append(" ORDER BY ").append(orderBy);
        }
        Cursor cursor = db.rawQuery(sql.toString(), args);
        try {
            List<RegistrationRequest> items = new ArrayList<>();
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String email = cursor.getString(1);
                String first = cursor.isNull(2) ? null : cursor.getString(2);
                String last = cursor.isNull(3) ? null : cursor.getString(3);
                String role = cursor.isNull(4) ? null : cursor.getString(4);
                String statusValue = cursor.getString(5);
                String reason = cursor.isNull(6) ? null : cursor.getString(6);
                long createdAt = cursor.getLong(7);
                String decidedBy = cursor.isNull(8) ? null : cursor.getString(8);
                Long decidedAt = cursor.isNull(9) ? null : cursor.getLong(9);
                RegistrationStatus status = RegistrationStatus.valueOf(statusValue);
                items.add(new RegistrationRequest(id, email, first, last, role, status, reason, createdAt, decidedBy, decidedAt));
            }
            return items;
        } finally {
            cursor.close();
        }
    }
}
