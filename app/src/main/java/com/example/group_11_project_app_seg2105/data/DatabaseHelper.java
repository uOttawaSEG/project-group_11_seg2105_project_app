package com.example.group_11_project_app_seg2105.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DB";

    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.NAME, null, DatabaseContract.VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.Users.CREATE);
        db.execSQL(DatabaseContract.StudentProfiles.CREATE);
        db.execSQL(DatabaseContract.TutorProfiles.CREATE);
        db.execSQL(DatabaseContract.TutorCourses.CREATE);
        db.execSQL(DatabaseContract.RegistrationRequests.CREATE);
        db.execSQL(DatabaseContract.RegistrationRequests.INDEX_STATUS);
        seedDefaults(db);
        seedPart4Rejected(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(DatabaseContract.StudentProfiles.CREATE);
        }
        if (oldVersion < 3) {
            db.execSQL(DatabaseContract.TutorProfiles.CREATE);
        }
        if (oldVersion < 4) {
            db.execSQL(DatabaseContract.RegistrationRequests.CREATE);
            db.execSQL(DatabaseContract.RegistrationRequests.INDEX_STATUS);
            seedPart4Rejected(db);
        }
        if (oldVersion < 5) {
            ensureColumn(db, DatabaseContract.StudentProfiles.TABLE, DatabaseContract.StudentProfiles.FIRST);
            ensureColumn(db, DatabaseContract.StudentProfiles.TABLE, DatabaseContract.StudentProfiles.LAST);
            ensureColumn(db, DatabaseContract.TutorProfiles.TABLE, DatabaseContract.TutorProfiles.FIRST);
            ensureColumn(db, DatabaseContract.TutorProfiles.TABLE, DatabaseContract.TutorProfiles.LAST);
        }
    }

    private void ensureColumn(SQLiteDatabase db, String table, String column) {
        try {
            db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + column + " TEXT");
        } catch (Exception ignored) {
        }
    }

    private void seedDefaults(SQLiteDatabase db) {
        insertUser(db, "admin@uottawa.ca", "admin123", "admin");
        insertUser(db, "student@uottawa.ca", "pass123", "student");
        insertUser(db, "tutor@uottawa.ca", "teach123", "tutor");
        ContentValues studentProfile = new ContentValues();
        studentProfile.put(DatabaseContract.StudentProfiles.EMAIL, "student@uottawa.ca");
        studentProfile.put(DatabaseContract.StudentProfiles.FIRST, "John");
        studentProfile.put(DatabaseContract.StudentProfiles.LAST, "Student");
        db.insertWithOnConflict(DatabaseContract.StudentProfiles.TABLE, null, studentProfile, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void insertUser(SQLiteDatabase db, String email, String password, String role) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Users.EMAIL, email);
        values.put(DatabaseContract.Users.PASSWORD, password);
        values.put(DatabaseContract.Users.ROLE, role);
        db.insertWithOnConflict(DatabaseContract.Users.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    private void seedPart4Rejected(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseContract.RegistrationRequests.TABLE, null);
        try {
            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                return;
            }
        } finally {
            cursor.close();
        }
        long now = System.currentTimeMillis();

        insertReg(db, "student@uottawa.ca", RegistrationStatus.REJECTED, now - 86_400_000L, "Rejected student reason");
        insertReg(db, "tutor@uottawa.ca", RegistrationStatus.REJECTED, now - 43_200_000L, "Rejected tutor reason");
        insertReg(db, "pending_tutor@uottawa.ca", RegistrationStatus.PENDING, System.currentTimeMillis(), null);
        insertReg(db, "pending_student@uottawa.ca", RegistrationStatus.PENDING, System.currentTimeMillis(), null);
    }

    private void insertReg(SQLiteDatabase db, String email, RegistrationStatus status, long createdAt, String reason) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.RegistrationRequests.EMAIL, email);
        values.put(DatabaseContract.RegistrationRequests.CREATED_AT, createdAt);
        values.put(DatabaseContract.RegistrationRequests.STATUS, status.name());
        values.put(DatabaseContract.RegistrationRequests.REASON, reason);
        db.insert(DatabaseContract.RegistrationRequests.TABLE, null, values);
    }

    public void saveUser(String role, String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Users.EMAIL, email);
        values.put(DatabaseContract.Users.PASSWORD, password);
        values.put(DatabaseContract.Users.ROLE, role);
        db.insertWithOnConflict(DatabaseContract.Users.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        Log.d(TAG, "Inserted user: " + email + " | Role: " + role);
    }

    public boolean validateLogin(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + DatabaseContract.Users.TABLE + " WHERE " + DatabaseContract.Users.EMAIL + "=? AND " + DatabaseContract.Users.PASSWORD + "=?", new String[]{email, password});
        try {
            boolean ok = cursor.moveToFirst();
            Log.d("LOGIN", "Login attempt for " + email + " | Success: " + ok);
            return ok;
        } finally {
            cursor.close();
        }
    }

    public String getUserRole(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + DatabaseContract.Users.ROLE + " FROM " + DatabaseContract.Users.TABLE + " WHERE " + DatabaseContract.Users.EMAIL + "=?", new String[]{email});
        try {
            String role = cursor.moveToFirst() ? cursor.getString(0) : null;
            Log.d(TAG, "Fetched role for " + email + ": " + role);
            return role;
        } finally {
            cursor.close();
        }
    }

    public void seedAdmin() {
        if (getUserRole("admin@uottawa.ca") == null) {
            saveUser("admin", "admin@uottawa.ca", "admin123");
            Log.d(TAG, "Seeded admin user");
        }
    }

    public void saveStudentProfile(String email, String first, String last, String phone, String program) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.StudentProfiles.EMAIL, email);
        values.put(DatabaseContract.StudentProfiles.FIRST, first);
        values.put(DatabaseContract.StudentProfiles.LAST, last);
        values.put(DatabaseContract.StudentProfiles.PHONE, phone);
        values.put(DatabaseContract.StudentProfiles.PROGRAM, program);
        db.insertWithOnConflict(DatabaseContract.StudentProfiles.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        Log.d(TAG, "Saved student profile for: " + email);
    }

    public boolean createStudentWithProfile(String email, String password, String first, String last, String phone, String program) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues userValues = new ContentValues();
            userValues.put(DatabaseContract.Users.EMAIL, email);
            userValues.put(DatabaseContract.Users.PASSWORD, password);
            userValues.put(DatabaseContract.Users.ROLE, "student");
            long userResult = db.insertWithOnConflict(DatabaseContract.Users.TABLE, null, userValues, SQLiteDatabase.CONFLICT_IGNORE);
            if (userResult == -1) {
                Log.d(TAG, "Failed to create student (duplicate email): " + email);
                return false;
            }
            ContentValues profileValues = new ContentValues();
            profileValues.put(DatabaseContract.StudentProfiles.EMAIL, email);
            profileValues.put(DatabaseContract.StudentProfiles.FIRST, first);
            profileValues.put(DatabaseContract.StudentProfiles.LAST, last);
            profileValues.put(DatabaseContract.StudentProfiles.PHONE, phone);
            profileValues.put(DatabaseContract.StudentProfiles.PROGRAM, program);
            db.insertWithOnConflict(DatabaseContract.StudentProfiles.TABLE, null, profileValues, SQLiteDatabase.CONFLICT_REPLACE);
            db.setTransactionSuccessful();
            Log.d(TAG, "Created student with profile: " + email);
            return true;
        } finally {
            db.endTransaction();
        }
    }

    public boolean createTutorWithProfile(String email, String password, String first, String last, String phone, String degree, Collection<String> courses) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues userValues = new ContentValues();
            userValues.put(DatabaseContract.Users.EMAIL, email);
            userValues.put(DatabaseContract.Users.PASSWORD, password);
            userValues.put(DatabaseContract.Users.ROLE, "tutor");
            long userResult = db.insertWithOnConflict(DatabaseContract.Users.TABLE, null, userValues, SQLiteDatabase.CONFLICT_IGNORE);
            if (userResult == -1) {
                Log.d(TAG, "Failed to create tutor (duplicate email): " + email);
                return false;
            }
            ContentValues profileValues = new ContentValues();
            profileValues.put(DatabaseContract.TutorProfiles.EMAIL, email);
            profileValues.put(DatabaseContract.TutorProfiles.FIRST, first);
            profileValues.put(DatabaseContract.TutorProfiles.LAST, last);
            profileValues.put(DatabaseContract.TutorProfiles.PHONE, phone);
            profileValues.put(DatabaseContract.TutorProfiles.DEGREE, degree);
            db.insertWithOnConflict(DatabaseContract.TutorProfiles.TABLE, null, profileValues, SQLiteDatabase.CONFLICT_REPLACE);
            if (courses != null) {
                for (String course : courses) {
                    ContentValues courseValues = new ContentValues();
                    courseValues.put(DatabaseContract.TutorCourses.EMAIL, email);
                    courseValues.put(DatabaseContract.TutorCourses.COURSE, course);
                    db.insertWithOnConflict(DatabaseContract.TutorCourses.TABLE, null, courseValues, SQLiteDatabase.CONFLICT_IGNORE);
                }
            }
            db.setTransactionSuccessful();
            Log.d(TAG, "Created tutor with all data: " + email);
            return true;
        } finally {
            db.endTransaction();
        }
    }

    public static class StudentProfile {
        public final String email;
        public final String firstName;
        public final String lastName;
        public final String phone;
        public final String program;

        public StudentProfile(String email, String firstName, String lastName, String phone, String program) {
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.program = program;
        }
    }

    public List<RegistrationRequest> getRejectedRequests() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT r." + DatabaseContract.RegistrationRequests.ID + ", r." + DatabaseContract.RegistrationRequests.EMAIL + ", COALESCE(s." + DatabaseContract.StudentProfiles.FIRST + ", t." + DatabaseContract.TutorProfiles.FIRST + ") AS first_name, COALESCE(s." + DatabaseContract.StudentProfiles.LAST + ", t." + DatabaseContract.TutorProfiles.LAST + ") AS last_name, u." + DatabaseContract.Users.ROLE + ", r." + DatabaseContract.RegistrationRequests.STATUS + ", r." + DatabaseContract.RegistrationRequests.REASON + ", r." + DatabaseContract.RegistrationRequests.CREATED_AT + ", r." + DatabaseContract.RegistrationRequests.DECIDED_BY + ", r." + DatabaseContract.RegistrationRequests.DECIDED_AT + " FROM " + DatabaseContract.RegistrationRequests.TABLE + " r LEFT JOIN " + DatabaseContract.Users.TABLE + " u ON r." + DatabaseContract.RegistrationRequests.EMAIL + " = u." + DatabaseContract.Users.EMAIL + " LEFT JOIN " + DatabaseContract.StudentProfiles.TABLE + " s ON r." + DatabaseContract.RegistrationRequests.EMAIL + " = s." + DatabaseContract.StudentProfiles.EMAIL + " LEFT JOIN " + DatabaseContract.TutorProfiles.TABLE + " t ON r." + DatabaseContract.RegistrationRequests.EMAIL + " = t." + DatabaseContract.TutorProfiles.EMAIL + " WHERE r." + DatabaseContract.RegistrationRequests.STATUS + " = ? ORDER BY r." + DatabaseContract.RegistrationRequests.CREATED_AT + " DESC";
        List<RegistrationRequest> out = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, new String[]{RegistrationStatus.REJECTED.name()});
        try {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String email = cursor.getString(1);
                String first = cursor.isNull(2) ? null : cursor.getString(2);
                String last = cursor.isNull(3) ? null : cursor.getString(3);
                String role = cursor.isNull(4) ? null : cursor.getString(4);
                RegistrationStatus status = RegistrationStatus.valueOf(cursor.getString(5));
                String reason = cursor.isNull(6) ? null : cursor.getString(6);
                long createdAt = cursor.getLong(7);
                String decidedBy = cursor.isNull(8) ? null : cursor.getString(8);
                Long decidedAt = cursor.isNull(9) ? null : cursor.getLong(9);
                out.add(new RegistrationRequest(id, email, first, last, role, status, reason, createdAt, decidedBy, decidedAt));
            }
        } finally {
            cursor.close();
        }
        return out;
    }

    public boolean reapproveRejected(long requestId, String adminEmail, @Nullable String note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.RegistrationRequests.STATUS, RegistrationStatus.APPROVED.name());
        values.put(DatabaseContract.RegistrationRequests.DECIDED_BY, adminEmail);
        values.put(DatabaseContract.RegistrationRequests.DECIDED_AT, System.currentTimeMillis());
        if (note != null) {
            values.put(DatabaseContract.RegistrationRequests.REASON, note);
        }
        int rows = db.update(DatabaseContract.RegistrationRequests.TABLE, values, DatabaseContract.RegistrationRequests.ID + "=? AND " + DatabaseContract.RegistrationRequests.STATUS + "=?", new String[]{String.valueOf(requestId), RegistrationStatus.REJECTED.name()});
        return rows == 1;
    }

    public StudentProfile getStudentProfile(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + DatabaseContract.StudentProfiles.FIRST + ", " + DatabaseContract.StudentProfiles.LAST + ", " + DatabaseContract.StudentProfiles.PHONE + ", " + DatabaseContract.StudentProfiles.PROGRAM + " FROM " + DatabaseContract.StudentProfiles.TABLE + " WHERE " + DatabaseContract.StudentProfiles.EMAIL + "=?", new String[]{email});
        try {
            if (cursor.moveToFirst()) {
                Log.d(TAG, "Fetched student profile for: " + email);
                return new StudentProfile(email, cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            }
            Log.d(TAG, "No student profile found for: " + email);
            return null;
        } finally {
            cursor.close();
        }
    }
    // --- Admin Inbox Methods ---
    public ArrayList<RegistrationRequest> getPendingRequests() {
        SQLiteDatabase db = getReadableDatabase();
        String sql =
                "SELECT r." + R_ID + ", " +
                        "r." + R_EMAIL + ", " +
                        "COALESCE(s." + C_FIRST + ", t." + C_FIRST + ") AS first_name, " +
                        "COALESCE(s." + C_LAST + ", t." + C_LAST + ") AS last_name, " +
                        "u." + C_ROLE + ", " +
                        "r." + R_STATUS + ", " +
                        "r." + R_REASON + " " +
                        "FROM " + T_REG_REQUESTS + " r " +
                        "LEFT JOIN " + T_USERS + " u ON r." + R_EMAIL + " = u." + C_EMAIL + " " +
                        "LEFT JOIN " + T_STUDENT_PROFILES + " s ON r." + R_EMAIL + " = s." + C_EMAIL + " " +
                        "LEFT JOIN " + T_TUTOR_PROFILES + " t ON r." + R_EMAIL + " = t." + C_EMAIL + " " +
                        "WHERE r." + R_STATUS + " = 'PENDING' " +
                        "ORDER BY r." + R_CREATED_AT + " DESC";

        ArrayList<RegistrationRequest> out = new ArrayList<>();

        try (Cursor c = db.rawQuery(sql, null)) {
            while (c.moveToNext()) {
                long id = c.getLong(0);
                String email = c.getString(1);
                String first = c.isNull(2) ? "" : c.getString(2);
                String last = c.isNull(3) ? "" : c.getString(3);
                String role = c.isNull(4) ? "" : c.getString(4);
                String status = c.getString(5);
                String reason = c.isNull(6) ? "" : c.getString(6);

                out.add(new RegistrationRequest(id, email, first, last, role, status, reason));
            }
        }
        return out;
    }

    public void updateRequestStatus(String email, String newStatus) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(R_STATUS, newStatus);
        cv.put(R_DECIDED_AT, System.currentTimeMillis());
        db.update(T_REG_REQUESTS, cv, R_EMAIL + "=?", new String[]{email});
    }

}
