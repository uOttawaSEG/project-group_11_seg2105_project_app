package com.example.group_11_project_app_seg2105.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Collection;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "otams.db";
    private static final int DB_VERSION = 3;

    // Tables
    private static final String T_USERS = "users";
    private static final String T_STUDENT_PROFILES = "student_profiles";
    private static final String T_TUTOR_PROFILES = "tutor_profiles";
    private static final String T_TUTOR_COURSES = "tutor_courses";

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
        // User accounts
        db.execSQL("CREATE TABLE " + T_USERS + " (" +
                C_EMAIL + " TEXT PRIMARY KEY, " +
                C_PASSWORD + " TEXT NOT NULL, " +
                C_ROLE + " TEXT NOT NULL)");

        // Student profiles
        db.execSQL("CREATE TABLE " + T_STUDENT_PROFILES + " (" +
                C_EMAIL + " TEXT PRIMARY KEY, " +
                C_FIRST + " TEXT, " +
                C_LAST + " TEXT, " +
                C_PHONE + " TEXT, " +
                C_PROGRAM + " TEXT, " +
                "FOREIGN KEY(" + C_EMAIL + ") REFERENCES " + T_USERS + "(" + C_EMAIL + ") ON DELETE CASCADE)");

        // Tutor profiles
        db.execSQL("CREATE TABLE " + T_TUTOR_PROFILES + " (" +
                C_EMAIL + " TEXT PRIMARY KEY, " +
                C_FIRST + " TEXT NOT NULL, " +
                C_LAST + " TEXT NOT NULL, " +
                C_PHONE + " TEXT NOT NULL, " +
                C_DEGREE + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + C_EMAIL + ") REFERENCES " + T_USERS + "(" + C_EMAIL + ") ON DELETE CASCADE)");

        // Tutor courses (many-to-one)
        db.execSQL("CREATE TABLE " + T_TUTOR_COURSES + " (" +
                C_EMAIL + " TEXT NOT NULL, " +
                C_COURSE + " TEXT NOT NULL, " +
                "PRIMARY KEY (" + C_EMAIL + ", " + C_COURSE + "), " +
                "FOREIGN KEY(" + C_EMAIL + ") REFERENCES " + T_USERS + "(" + C_EMAIL + ") ON DELETE CASCADE)");

        // Default test users
        db.execSQL("INSERT INTO " + T_USERS + " (" + C_EMAIL + ", " + C_PASSWORD + ", " + C_ROLE + ") VALUES " +
                "('admin@uottawa.ca','admin123','admin')," +
                "('student@uottawa.ca','pass123','student')," +
                "('tutor@uottawa.ca','teach123','tutor')");

        db.execSQL("INSERT INTO " + T_STUDENT_PROFILES + " (" + C_EMAIL + ", " + C_FIRST + ", " + C_LAST + ") VALUES " +
                "('student@uottawa.ca', 'John', 'Student')");
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
    }

    // --- Core User Methods ---
    public void saveUser(String role, String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(C_EMAIL, email);
        v.put(C_PASSWORD, password);
        v.put(C_ROLE, role);
        db.insertWithOnConflict(T_USERS, null, v, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public boolean validateLogin(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT 1 FROM " + T_USERS + " WHERE email=? AND password=?",
                new String[]{email, password});
        boolean ok = c.moveToFirst();
        c.close();
        db.close();
        return ok;
    }

    public String getUserRole(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + C_ROLE + " FROM " + T_USERS + " WHERE email=?",
                new String[]{email});
        String role = c.moveToFirst() ? c.getString(0) : null;
        c.close();
        db.close();
        return role;
    }

    public void seedAdmin() {
        if (getUserRole("admin@uottawa.ca") == null) {
            saveUser("admin", "admin@uottawa.ca", "admin123");
        }
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
        } finally {
            db.close();
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
            if (userRes == -1) return false;

            ContentValues p = new ContentValues();
            p.put(C_EMAIL, email);
            p.put(C_FIRST, first);
            p.put(C_LAST, last);
            p.put(C_PHONE, phone);
            p.put(C_PROGRAM, program);
            db.insertWithOnConflict(T_STUDENT_PROFILES, null, p, SQLiteDatabase.CONFLICT_REPLACE);

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
            db.close();
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
            if (userRes == -1) return false;

            ContentValues p = new ContentValues();
            p.put(C_EMAIL, email);
            p.put(C_FIRST, first);
            p.put(C_LAST, last);
            p.put(C_PHONE, phone);
            p.put(C_DEGREE, degree);
            db.insertWithOnConflict(T_TUTOR_PROFILES, null, p, SQLiteDatabase.CONFLICT_REPLACE);

            if (courses != null) {
                for (String c : courses) {
                    ContentValues row = new ContentValues();
                    row.put(C_EMAIL, email);
                    row.put(C_COURSE, c);
                    db.insertWithOnConflict(T_TUTOR_COURSES, null, row, SQLiteDatabase.CONFLICT_IGNORE);
                }
            }

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
            db.close();
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

    public StudentProfile getStudentProfile(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + C_FIRST + ", " + C_LAST + ", " + C_PHONE + ", " + C_PROGRAM +
                " FROM " + T_STUDENT_PROFILES + " WHERE " + C_EMAIL + "=?",
                new String[]{email});
        try {
            if (c.moveToFirst()) {
                return new StudentProfile(email, c.getString(0), c.getString(1), c.getString(2), c.getString(3));
            }
            return null;
        } finally {
            c.close();
            db.close();
        }
    }
}
