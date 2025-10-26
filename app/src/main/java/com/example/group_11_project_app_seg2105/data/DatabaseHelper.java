package com.example.group_11_project_app_seg2105.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;
import com.example.group_11_project_app_seg2105.data.RegistrationRequest;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "otams.db";
    private static final int DB_VERSION = 6;

    // Tables
    private static final String T_USERS = "users";
    private static final String T_STUDENT_PROFILES = "student_profiles";
    private static final String T_TUTOR_PROFILES = "tutor_profiles";
    private static final String T_TUTOR_COURSES = "tutor_courses";
    private static final String T_REG_REQUESTS = "registration_requests";

    // Common Columns
    private static final String C_EMAIL = "email";
    private static final String C_PASSWORD = "password";
    private static final String C_ROLE = "role";

    // Student Columns
    private static final String C_FIRST = "first_name";
    private static final String C_LAST = "last_name";
    private static final String C_PHONE = "phone";
    private static final String C_PROGRAM = "program";

    // Tutor Columns
    private static final String C_DEGREE = "degree";
    private static final String C_COURSE = "course";

    // Registration Request Columns
    private static final String R_ID = "id";
    private static final String R_EMAIL = "email";
    private static final String R_CREATED_AT = "created_at";
    private static final String R_STATUS = "status";
    private static final String R_REASON = "reason";
    private static final String R_DECIDED_BY = "decided_by_admin";
    private static final String R_DECIDED_AT = "decided_at";




    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_USERS + " (" +
                C_EMAIL + " TEXT PRIMARY KEY, " +
                C_PASSWORD + " TEXT NOT NULL, " +
                C_ROLE + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + T_STUDENT_PROFILES + " (" +
                C_EMAIL + " TEXT PRIMARY KEY, " +
                C_FIRST + " TEXT, " +
                C_LAST + " TEXT, " +
                C_PHONE + " TEXT, " +
                C_PROGRAM + " TEXT, " +
                "FOREIGN KEY(" + C_EMAIL + ") REFERENCES " + T_USERS + "(" + C_EMAIL + ") ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE " + T_TUTOR_PROFILES + " (" +
                C_EMAIL + " TEXT PRIMARY KEY, " +
                C_FIRST + " TEXT NOT NULL, " +
                C_LAST + " TEXT NOT NULL, " +
                C_PHONE + " TEXT NOT NULL, " +
                C_DEGREE + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + C_EMAIL + ") REFERENCES " + T_USERS + "(" + C_EMAIL + ") ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE " + T_TUTOR_COURSES + " (" +
                C_EMAIL + " TEXT NOT NULL, " +
                C_COURSE + " TEXT NOT NULL, " +
                "PRIMARY KEY (" + C_EMAIL + ", " + C_COURSE + "), " +
                "FOREIGN KEY(" + C_EMAIL + ") REFERENCES " + T_USERS + "(" + C_EMAIL + ") ON DELETE CASCADE)");

        db.execSQL("INSERT INTO " + T_USERS + " (" + C_EMAIL + ", " + C_PASSWORD + ", " + C_ROLE + ") VALUES " +
                "('admin@uottawa.ca','admin123','admin')," +
                "('student@uottawa.ca','pass123','student')," +
                "('tutor@uottawa.ca','teach123','tutor')");

        db.execSQL("INSERT INTO " + T_STUDENT_PROFILES + " (" + C_EMAIL + ", " + C_FIRST + ", " + C_LAST + ") VALUES " +
                "('student@uottawa.ca', 'John', 'Student')");

        db.execSQL("CREATE TABLE " + T_REG_REQUESTS + " (" +
                R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                R_EMAIL + " TEXT NOT NULL, " +
                R_CREATED_AT + " INTEGER NOT NULL, " +
                R_STATUS + " TEXT NOT NULL CHECK(" + R_STATUS + " IN ('PENDING', 'APPROVED', 'REJECTED')), " +
                R_REASON + " TEXT, " +
                R_DECIDED_BY + " TEXT, " +
                R_DECIDED_AT + " INTEGER, " +
                "FOREIGN KEY(" + R_EMAIL + ") REFERENCES " + T_USERS + "(" + C_EMAIL + ") ON DELETE CASCADE)"
        );
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_reg_status ON " + T_REG_REQUESTS + "(" + R_STATUS +")");

        //temp part 4 rejected users for testing
        seedPart4Rejected(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        if (oldV < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + T_STUDENT_PROFILES + " (" +
                    C_EMAIL + " TEXT PRIMARY KEY, " +
                    C_FIRST + " TEXT NOT NULL, " +
                    C_LAST + " TEXT NOT NULL, " +
                    C_PHONE + " TEXT NOT NULL, " +
                    C_PROGRAM + " TEXT NOT NULL, " +
                    "FOREIGN KEY(" + C_EMAIL + ") REFERENCES " + T_USERS + "(" + C_EMAIL + ") ON DELETE CASCADE)");
        }
        if (oldV < 3) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + T_TUTOR_PROFILES + " (" +
                    C_EMAIL + " TEXT PRIMARY KEY, " +
                    C_FIRST + " TEXT NOT NULL, " +
                    C_LAST + " TEXT NOT NULL, " +
                    C_PHONE + " TEXT NOT NULL, " +
                    C_DEGREE + " TEXT NOT NULL, " +
                    "FOREIGN KEY(" + C_EMAIL + ") REFERENCES " + T_USERS + "(" + C_EMAIL + ") ON DELETE CASCADE)");
        }
        if (oldV < 4) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + T_REG_REQUESTS + " (" +
                    R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    R_EMAIL + " TEXT NOT NULL, " +
                    R_CREATED_AT + " INTEGER NOT NULL, " +
                    R_STATUS + " TEXT NOT NULL CHECK(" + R_STATUS + " IN ('PENDING', 'APPROVED', 'REJECTED')), " +
                    R_REASON + " TEXT, " +
                    R_DECIDED_BY + " TEXT, " +
                    R_DECIDED_AT + " INTEGER, " +
                    "FOREIGN KEY(" + R_EMAIL + ") REFERENCES " + T_USERS + "(" + C_EMAIL + ") ON DELETE CASCADE)");
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_reg_status ON " + T_REG_REQUESTS + "(" + R_STATUS +")");
            seedPart4Rejected(db);

        }

        if(oldV < 5) {
            try { db.execSQL("ALTER TABLE " + T_STUDENT_PROFILES + " ADD COLUMN " + C_FIRST + " TEXT"); } catch (Exception ignore) {}
            try { db.execSQL("ALTER TABLE " + T_STUDENT_PROFILES + " ADD COLUMN " + C_LAST  + " TEXT"); } catch (Exception ignore) {}

            try { db.execSQL("ALTER TABLE " + T_TUTOR_PROFILES   + " ADD COLUMN " + C_FIRST + " TEXT"); } catch (Exception ignore) {}
            try { db.execSQL("ALTER TABLE " + T_TUTOR_PROFILES   + " ADD COLUMN " + C_LAST  + " TEXT"); } catch (Exception ignore) {}
        }
    }

    // --- Core User Methods ---
    public void saveUser(String role, String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(C_EMAIL, email);
        v.put(C_PASSWORD, password);
        v.put(C_ROLE, role);
        db.insertWithOnConflict(T_USERS, null, v, SQLiteDatabase.CONFLICT_REPLACE);
        Log.d("DB", "Inserted user: " + email + " | Role: " + role);
    }

    public boolean validateLogin(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT 1 FROM " + T_USERS + " WHERE email=? AND password=?",
                new String[]{email, password});
        boolean ok = c.moveToFirst();
        Log.d("LOGIN", "Login attempt for " + email + " | Success: " + ok);
        c.close();
        return ok;
    }

    public String getUserRole(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + C_ROLE + " FROM " + T_USERS + " WHERE email=?",
                new String[]{email});
        String role = c.moveToFirst() ? c.getString(0) : null;
        Log.d("DB", "Fetched role for " + email + ": " + role);
        c.close();
        return role;
    }

    public void seedAdmin() {
        if (getUserRole("admin@uottawa.ca") == null) {
            saveUser("admin", "admin@uottawa.ca", "admin123");
            Log.d("DB", "Seeded admin user");
        }
    }

    private void seedPart4Rejected(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + T_REG_REQUESTS, null);
        try {
            if (c.moveToFirst() && c.getInt(0) > 0) {
                return;
            }
        } finally {
            c.close();
        }

        long now = System.currentTimeMillis();
        insertReg(db, "student@uottawa.ca", "REJECTED", now - 86_400_000L,"Rejected student reason");
        insertReg(db, "tutor@uottawa.ca", "REJECTED", now - 43_200_000L,"Rejected tutor reason");
    }
    private void insertReg(SQLiteDatabase db, String email, String status, long createdAt, String reason) {
        ContentValues cv = new ContentValues();
        cv.put(R_EMAIL, email);
        cv.put(R_CREATED_AT, createdAt);
        cv.put(R_STATUS, status);
        cv.put(R_REASON, reason);
        db.insert(T_REG_REQUESTS, null, cv);



    }

    // --- Student Methods ---
    public void saveStudentProfile(String email, String first, String last, String phone, String program) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues v = new ContentValues();
            v.put(C_EMAIL, email);
            v.put(C_FIRST, first);
            v.put(C_LAST, last);
            v.put(C_PHONE, phone);
            v.put(C_PROGRAM, program);
            db.insertWithOnConflict(T_STUDENT_PROFILES, null, v, SQLiteDatabase.CONFLICT_REPLACE);
            Log.d("DB", "Saved student profile for: " + email);
        } finally {

        }
    }

    public boolean createStudentWithProfile(String email, String password, String first, String last,
                                            String phone, String program) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues u = new ContentValues();
            u.put(C_EMAIL, email);
            u.put(C_PASSWORD, password);
            u.put(C_ROLE, "student");
            long userRes = db.insertWithOnConflict(T_USERS, null, u, SQLiteDatabase.CONFLICT_IGNORE);
            if (userRes == -1) {
                Log.d("DB", "Failed to create student (duplicate email): " + email);
                return false;
            }

            ContentValues p = new ContentValues();
            p.put(C_EMAIL, email);
            p.put(C_FIRST, first);
            p.put(C_LAST, last);
            p.put(C_PHONE, phone);
            p.put(C_PROGRAM, program);
            db.insertWithOnConflict(T_STUDENT_PROFILES, null, p, SQLiteDatabase.CONFLICT_REPLACE);

            db.setTransactionSuccessful();
            Log.d("DB", "Created student with profile: " + email);
            return true;
        } finally {
            db.endTransaction();
        }
    }

    // --- Tutor Methods ---
    public boolean createTutorWithProfile(String email, String password, String first, String last,
                                          String phone, String degree, Collection<String> courses) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues u = new ContentValues();
            u.put(C_EMAIL, email);
            u.put(C_PASSWORD, password);
            u.put(C_ROLE, "tutor");
            long userRes = db.insertWithOnConflict(T_USERS, null, u, SQLiteDatabase.CONFLICT_IGNORE);
            if (userRes == -1) {
                Log.d("DB", "Failed to create tutor (duplicate email): " + email);
                return false;
            }

            ContentValues p = new ContentValues();
            p.put(C_EMAIL, email);
            p.put(C_FIRST, first);
            p.put(C_LAST, last);
            p.put(C_PHONE, phone);
            p.put(C_DEGREE, degree);
            db.insertWithOnConflict(T_TUTOR_PROFILES, null, p, SQLiteDatabase.CONFLICT_REPLACE);
            Log.d("DB", "Created tutor profile for: " + email);

            if (courses != null) {
                for (String c : courses) {
                    ContentValues row = new ContentValues();
                    row.put(C_EMAIL, email);
                    row.put(C_COURSE, c);
                    db.insertWithOnConflict(T_TUTOR_COURSES, null, row, SQLiteDatabase.CONFLICT_IGNORE);
                    Log.d("DB", "Added tutor course: " + c + " for " + email);
                }
            }

            db.setTransactionSuccessful();
            Log.d("DB", "Created tutor with all data: " + email);
            return true;
        } finally {
            db.endTransaction();
        }
    }

    // --- Student Profile Model ---
    public static class StudentProfile {
        public final String email;
        public final String firstName;
        public final String lastName;
        public final String phone;
        public final String program;

        public StudentProfile(String email, String firstName, String lastName,
                              String phone, String program) {
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.program = program;
        }
    }



    public List<RegistrationRequest> getRejectedRequests() {
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
                        "WHERE r." + R_STATUS + " = 'REJECTED' " +
                        "ORDER BY r." + R_CREATED_AT + " DESC";

        List<RegistrationRequest> out = new ArrayList<>();

        try(Cursor c = db.rawQuery(sql, null)) {

            while (c.moveToNext()) {
                long id = c.getLong(0);
                String email = c.getString(1);
                String first = c.isNull(2) ? null : c.getString(2);
                String last = c.isNull(3) ? null : c.getString(3);
                String role = c.isNull(4) ? null : c.getString(4);
                String status = c.getString(5);
                String reason = c.isNull(6) ? null : c.getString(6);

                out.add(new RegistrationRequest(id, email, first, last, role, status, reason));
            }


        }
        return out;

    }

    public boolean reapproveRejected(long requestID, String adminEmail, @Nullable String note) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(R_STATUS, "APPROVED");
        cv.put(R_DECIDED_BY, adminEmail);
        cv.put(R_DECIDED_AT, System.currentTimeMillis());
        if(note != null) {
            cv.put(R_REASON, note);
        }

        int rows = db.update(
                T_REG_REQUESTS,
                cv,
                R_ID + "=? AND "+R_STATUS+"='REJECTED'",
                new String[]{String.valueOf(requestID)}
        );
        return rows == 1;

    }


    public StudentProfile getStudentProfile(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + C_FIRST + ", " + C_LAST + ", " + C_PHONE + ", " + C_PROGRAM +
                        " FROM " + T_STUDENT_PROFILES + " WHERE " + C_EMAIL + "=?",
                new String[]{email});
        try {
            if (c.moveToFirst()) {
                Log.d("DB", "Fetched student profile for: " + email);
                return new StudentProfile(email, c.getString(0), c.getString(1), c.getString(2), c.getString(3));
            }
            Log.d("DB", "No student profile found for: " + email);
            return null;
        } finally {
            c.close();
            db.close();
        }
    }
}
