package com.example.group_11_project_app_seg2105.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SQLiteUserRepository implements UserRepository {
    private final DatabaseHelper helper;

    public SQLiteUserRepository(DatabaseHelper helper) {
        this.helper = helper;
    }

    @Override
    public boolean create(User user) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues userValues = new ContentValues();
            userValues.put(DatabaseContract.Users.EMAIL, user.email);
            userValues.put(DatabaseContract.Users.PASSWORD, user.password);
            userValues.put(DatabaseContract.Users.ROLE, user.role);
            long inserted = db.insertWithOnConflict(DatabaseContract.Users.TABLE, null, userValues, SQLiteDatabase.CONFLICT_IGNORE);
            if (inserted == -1) {
                return false;
            }
            upsertStudentProfile(db, user);
            upsertTutorProfile(db, user);
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public User findByEmail(String email) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.Users.TABLE, new String[]{DatabaseContract.Users.EMAIL, DatabaseContract.Users.PASSWORD, DatabaseContract.Users.ROLE}, DatabaseContract.Users.EMAIL + "=?", new String[]{email}, null, null, null);
        try {
            if (!cursor.moveToFirst()) {
                return null;
            }
            String password = cursor.isNull(1) ? null : cursor.getString(1);
            String role = cursor.isNull(2) ? null : cursor.getString(2);
            ProfileRecord profile = loadProfile(db, email, role);
            List<String> courses = loadCourses(db, email, role);
            return new User(email, password, role, profile.firstName, profile.lastName, profile.phone, profile.program, profile.degree, courses);
        } finally {
            cursor.close();
        }
    }

    @Override
    public boolean update(User user) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues userValues = new ContentValues();
            userValues.put(DatabaseContract.Users.PASSWORD, user.password);
            userValues.put(DatabaseContract.Users.ROLE, user.role);
            int affected = db.update(DatabaseContract.Users.TABLE, userValues, DatabaseContract.Users.EMAIL + "=?", new String[]{user.email});
            if (affected == 0) {
                return false;
            }
            clearProfiles(db, user.email);
            upsertStudentProfile(db, user);
            upsertTutorProfile(db, user);
            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public boolean delete(String email) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int affected = db.delete(DatabaseContract.Users.TABLE, DatabaseContract.Users.EMAIL + "=?", new String[]{email});
        return affected > 0;
    }

    @Override
    public List<User> findAll() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.Users.TABLE, new String[]{DatabaseContract.Users.EMAIL}, null, null, null, null, null);
        try {
            List<User> users = new ArrayList<>();
            while (cursor.moveToNext()) {
                String email = cursor.getString(0);
                User user = findByEmail(email);
                if (user != null) {
                    users.add(user);
                }
            }
            return users;
        } finally {
            cursor.close();
        }
    }

    @Override
    public List<User> findByRole(String role) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseContract.Users.TABLE, new String[]{DatabaseContract.Users.EMAIL}, DatabaseContract.Users.ROLE + "=?", new String[]{role}, null, null, null);
        try {
            List<User> users = new ArrayList<>();
            while (cursor.moveToNext()) {
                String email = cursor.getString(0);
                User user = findByEmail(email);
                if (user != null) {
                    users.add(user);
                }
            }
            return users;
        } finally {
            cursor.close();
        }
    }

    private void upsertStudentProfile(SQLiteDatabase db, User user) {
        if (!"student".equalsIgnoreCase(user.role)) {
            db.delete(DatabaseContract.StudentProfiles.TABLE, DatabaseContract.StudentProfiles.EMAIL + "=?", new String[]{user.email});
            return;
        }
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.StudentProfiles.EMAIL, user.email);
        values.put(DatabaseContract.StudentProfiles.FIRST, user.firstName);
        values.put(DatabaseContract.StudentProfiles.LAST, user.lastName);
        values.put(DatabaseContract.StudentProfiles.PHONE, user.phone);
        values.put(DatabaseContract.StudentProfiles.PROGRAM, user.program);
        db.insertWithOnConflict(DatabaseContract.StudentProfiles.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    private void upsertTutorProfile(SQLiteDatabase db, User user) {
        if (!"com/example/group_11_project_app_seg2105/tutor".equalsIgnoreCase(user.role)) {
            db.delete(DatabaseContract.TutorProfiles.TABLE, DatabaseContract.TutorProfiles.EMAIL + "=?", new String[]{user.email});
            db.delete(DatabaseContract.TutorCourses.TABLE, DatabaseContract.TutorCourses.EMAIL + "=?", new String[]{user.email});
            return;
        }
        ContentValues profile = new ContentValues();
        profile.put(DatabaseContract.TutorProfiles.EMAIL, user.email);
        profile.put(DatabaseContract.TutorProfiles.FIRST, user.firstName);
        profile.put(DatabaseContract.TutorProfiles.LAST, user.lastName);
        profile.put(DatabaseContract.TutorProfiles.PHONE, user.phone);
        profile.put(DatabaseContract.TutorProfiles.DEGREE, user.degree);
        db.insertWithOnConflict(DatabaseContract.TutorProfiles.TABLE, null, profile, SQLiteDatabase.CONFLICT_REPLACE);
        db.delete(DatabaseContract.TutorCourses.TABLE, DatabaseContract.TutorCourses.EMAIL + "=?", new String[]{user.email});
        if (user.courses != null) {
            for (String course : user.courses) {
                ContentValues courseValues = new ContentValues();
                courseValues.put(DatabaseContract.TutorCourses.EMAIL, user.email);
                courseValues.put(DatabaseContract.TutorCourses.COURSE, course);
                db.insertWithOnConflict(DatabaseContract.TutorCourses.TABLE, null, courseValues, SQLiteDatabase.CONFLICT_IGNORE);
            }
        }
    }

    private void clearProfiles(SQLiteDatabase db, String email) {
        db.delete(DatabaseContract.StudentProfiles.TABLE, DatabaseContract.StudentProfiles.EMAIL + "=?", new String[]{email});
        db.delete(DatabaseContract.TutorProfiles.TABLE, DatabaseContract.TutorProfiles.EMAIL + "=?", new String[]{email});
        db.delete(DatabaseContract.TutorCourses.TABLE, DatabaseContract.TutorCourses.EMAIL + "=?", new String[]{email});
    }

    private ProfileRecord loadProfile(SQLiteDatabase db, String email, String role) {
        if ("student".equalsIgnoreCase(role)) {
            Cursor cursor = db.query(DatabaseContract.StudentProfiles.TABLE, new String[]{DatabaseContract.StudentProfiles.FIRST, DatabaseContract.StudentProfiles.LAST, DatabaseContract.StudentProfiles.PHONE, DatabaseContract.StudentProfiles.PROGRAM}, DatabaseContract.StudentProfiles.EMAIL + "=?", new String[]{email}, null, null, null);
            try {
                if (cursor.moveToFirst()) {
                    return new ProfileRecord(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), null);
                }
            } finally {
                cursor.close();
            }
            return new ProfileRecord(null, null, null, null, null);
        }
        if ("com/example/group_11_project_app_seg2105/tutor".equalsIgnoreCase(role)) {
            Cursor cursor = db.query(DatabaseContract.TutorProfiles.TABLE, new String[]{DatabaseContract.TutorProfiles.FIRST, DatabaseContract.TutorProfiles.LAST, DatabaseContract.TutorProfiles.PHONE, DatabaseContract.TutorProfiles.DEGREE}, DatabaseContract.TutorProfiles.EMAIL + "=?", new String[]{email}, null, null, null);
            try {
                if (cursor.moveToFirst()) {
                    return new ProfileRecord(cursor.getString(0), cursor.getString(1), cursor.getString(2), null, cursor.getString(3));
                }
            } finally {
                cursor.close();
            }
            return new ProfileRecord(null, null, null, null, null);
        }
        return new ProfileRecord(null, null, null, null, null);
    }

    private List<String> loadCourses(SQLiteDatabase db, String email, String role) {
        if (!"com/example/group_11_project_app_seg2105/tutor".equalsIgnoreCase(role)) {
            return new ArrayList<>();
        }
        Cursor cursor = db.query(DatabaseContract.TutorCourses.TABLE, new String[]{DatabaseContract.TutorCourses.COURSE}, DatabaseContract.TutorCourses.EMAIL + "=?", new String[]{email}, null, null, null);
        try {
            List<String> courses = new ArrayList<>();
            while (cursor.moveToNext()) {
                courses.add(cursor.getString(0));
            }
            return courses;
        } finally {
            cursor.close();
        }
    }

    private static final class ProfileRecord {
        final String firstName;
        final String lastName;
        final String phone;
        final String program;
        final String degree;

        ProfileRecord(String firstName, String lastName, String phone, String program, String degree) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.program = program;
            this.degree = degree;
        }
    }
}
