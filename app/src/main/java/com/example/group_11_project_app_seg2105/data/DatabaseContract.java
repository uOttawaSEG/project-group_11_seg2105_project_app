package com.example.group_11_project_app_seg2105.data;

public final class DatabaseContract {
    public static final int VERSION = 6;
    public static final String NAME = "otams.db";

    private DatabaseContract() {
    }

    public static final class Users {
        public static final String TABLE = "users";
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
        public static final String ROLE = "role";
        public static final String CREATE = "CREATE TABLE " + TABLE + " (" + EMAIL + " TEXT PRIMARY KEY, " + PASSWORD + " TEXT NOT NULL, " + ROLE + " TEXT NOT NULL)";
    }

    public static final class StudentProfiles {
        public static final String TABLE = "student_profiles";
        public static final String EMAIL = "email";
        public static final String FIRST = "first_name";
        public static final String LAST = "last_name";
        public static final String PHONE = "phone";
        public static final String PROGRAM = "program";
        public static final String CREATE = "CREATE TABLE " + TABLE + " (" + EMAIL + " TEXT PRIMARY KEY, " + FIRST + " TEXT, " + LAST + " TEXT, " + PHONE + " TEXT, " + PROGRAM + " TEXT, FOREIGN KEY(" + EMAIL + ") REFERENCES " + Users.TABLE + "(" + Users.EMAIL + ") ON DELETE CASCADE)";
    }

    public static final class TutorProfiles {
        public static final String TABLE = "tutor_profiles";
        public static final String EMAIL = "email";
        public static final String FIRST = "first_name";
        public static final String LAST = "last_name";
        public static final String PHONE = "phone";
        public static final String DEGREE = "degree";
        public static final String CREATE = "CREATE TABLE " + TABLE + " (" + EMAIL + " TEXT PRIMARY KEY, " + FIRST + " TEXT NOT NULL, " + LAST + " TEXT NOT NULL, " + PHONE + " TEXT NOT NULL, " + DEGREE + " TEXT NOT NULL, FOREIGN KEY(" + EMAIL + ") REFERENCES " + Users.TABLE + "(" + Users.EMAIL + ") ON DELETE CASCADE)";
    }

    public static final class TutorCourses {
        public static final String TABLE = "tutor_courses";
        public static final String EMAIL = "email";
        public static final String COURSE = "course";
        public static final String CREATE = "CREATE TABLE " + TABLE + " (" + EMAIL + " TEXT NOT NULL, " + COURSE + " TEXT NOT NULL, PRIMARY KEY (" + EMAIL + ", " + COURSE + "), FOREIGN KEY(" + EMAIL + ") REFERENCES " + Users.TABLE + "(" + Users.EMAIL + ") ON DELETE CASCADE)";
    }

    public static final class RegistrationRequests {
        public static final String TABLE = "registration_requests";
        public static final String ID = "id";
        public static final String EMAIL = "email";
        public static final String CREATED_AT = "created_at";
        public static final String STATUS = "status";
        public static final String REASON = "reason";
        public static final String DECIDED_BY = "decided_by_admin";
        public static final String DECIDED_AT = "decided_at";
        public static final String CREATE = "CREATE TABLE " + TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + EMAIL + " TEXT NOT NULL, " + CREATED_AT + " INTEGER NOT NULL, " + STATUS + " TEXT NOT NULL CHECK(" + STATUS + " IN ('PENDING', 'APPROVED', 'REJECTED')), " + REASON + " TEXT, " + DECIDED_BY + " TEXT, " + DECIDED_AT + " INTEGER, FOREIGN KEY(" + EMAIL + ") REFERENCES " + Users.TABLE + "(" + Users.EMAIL + ") ON DELETE CASCADE)";
        public static final String INDEX_STATUS = "CREATE INDEX IF NOT EXISTS idx_reg_status ON " + TABLE + "(" + STATUS + ")";
    }
}
