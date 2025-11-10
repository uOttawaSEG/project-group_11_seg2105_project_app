package com.example.group_11_project_app_seg2105.tutor;

import com.example.group_11_project_app_seg2105.data.SessionRequest;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TutorSessionPartitionerTest {

    @Test
    public void partitionSeparatesUpcomingAndPastAndSorts() {
        long now = LocalDateTime.of(2024, 1, 1, 12, 0)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        List<SessionRequest> sessions = Arrays.asList(
                session(1, "student1@uottawa.ca", "2024-01-02", "09:00", "09:30", "APPROVED"), // upcoming
                session(2, "student2@uottawa.ca", "2023-12-31", "10:00", "10:30", "APPROVED"), // past
                session(3, "student3@uottawa.ca", "2024-01-03", "08:00", "08:30", "PENDING")   // ignored for upcoming
        );

        TutorSessionPartitioner.Result result = TutorSessionPartitioner.partition(sessions, now);

        assertEquals(1, result.upcoming.size());
        assertEquals(1, result.past.size());
        assertEquals("student1@uottawa.ca", result.upcoming.get(0).session.studentEmail);
        assertEquals("student2@uottawa.ca", result.past.get(0).session.studentEmail);

        assertTrue(result.upcoming.get(0).canOpenStudentInfo);
        assertTrue(result.studentEmails.contains("student1@uottawa.ca"));
        assertTrue(result.studentEmails.contains("student2@uottawa.ca"));
        assertEquals(2, result.studentEmails.size());
    }

    @Test
    public void partitionMarksCanOpenOnlyForFutureApprovedSessions() {
        long now = LocalDateTime.of(2024, 1, 1, 8, 0)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        List<SessionRequest> sessions = Arrays.asList(
                session(4, "approved@uottawa.ca", "2024-01-02", "08:00", "08:30", "APPROVED"),
                session(5, "rejected@uottawa.ca", "2024-01-02", "09:00", "09:30", "REJECTED")
        );

        TutorSessionPartitioner.Result result = TutorSessionPartitioner.partition(sessions, now);
        assertEquals(1, result.upcoming.size());
        assertEquals("approved@uottawa.ca", result.upcoming.get(0).session.studentEmail);
        assertTrue(result.upcoming.get(0).canOpenStudentInfo);
        assertEquals(0, result.past.size());

        // Ensure rejected future session is not clickable/upcoming
        assertFalse(result.studentEmails.contains("rejected@uottawa.ca")); // filtered because not in lists
    }

    private SessionRequest session(long id, String studentEmail, String date, String start, String end, String status) {
        return new SessionRequest(id, studentEmail, "tutor@uottawa.ca", date, start, end, status);
    }
}
