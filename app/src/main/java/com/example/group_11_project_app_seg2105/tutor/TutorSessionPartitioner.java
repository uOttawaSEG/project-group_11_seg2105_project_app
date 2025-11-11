package com.example.group_11_project_app_seg2105.tutor;

import com.example.group_11_project_app_seg2105.data.SessionRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class TutorSessionPartitioner {

    private static final Set<String> UPCOMING_STATUSES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("APPROVED"))
    );

    private TutorSessionPartitioner() {}

    public static Result partition(List<SessionRequest> sessions, long nowMillis) {
        List<SessionRow> upcoming = new ArrayList<>();
        List<SessionRow> past = new ArrayList<>();
        Set<String> emails = new LinkedHashSet<>();

        if (sessions != null) {
            ZoneId zone = ZoneId.systemDefault();
            for (SessionRequest session : sessions) {
                if (session == null || session.date == null || session.start == null || session.end == null) continue;
                LocalDateTime startDateTime = toDateTime(session.date, session.start);
                LocalDateTime endDateTime = toDateTime(session.date, session.end);
                if (startDateTime == null || endDateTime == null) continue;

                long startMillis = startDateTime.atZone(zone).toInstant().toEpochMilli();
                long endMillis = endDateTime.atZone(zone).toInstant().toEpochMilli();

                boolean approvedStatus = session.status != null && UPCOMING_STATUSES.contains(session.status.toUpperCase());
                boolean futureStart = startMillis >= nowMillis;
                boolean ended = endMillis <= nowMillis;
                boolean canOpen = approvedStatus && futureStart;

                SessionRow row = new SessionRow(session, startMillis, endMillis, canOpen);
                boolean added = false;
                if (approvedStatus && futureStart) {
                    upcoming.add(row);
                    added = true;
                } else if (ended) {
                    past.add(row);
                    added = true;
                }

                if (added && session.studentEmail != null) {
                    emails.add(session.studentEmail);
                }
            }
        }

        Collections.sort(upcoming, (a, b) -> Long.compare(a.startMillis, b.startMillis));
        Collections.sort(past, (a, b) -> Long.compare(b.startMillis, a.startMillis));

        return new Result(upcoming, past, emails);
    }

    private static LocalDateTime toDateTime(String date, String time) {
        try {
            LocalDate d = LocalDate.parse(date);
            LocalTime t = LocalTime.parse(time);
            return LocalDateTime.of(d, t);
        } catch (Exception e) {
            return null;
        }
    }

    public static final class SessionRow {
        public final SessionRequest session;
        public final long startMillis;
        public final long endMillis;
        public final boolean canOpenStudentInfo;

        SessionRow(SessionRequest session, long startMillis, long endMillis, boolean canOpenStudentInfo) {
            this.session = session;
            this.startMillis = startMillis;
            this.endMillis = endMillis;
            this.canOpenStudentInfo = canOpenStudentInfo;
        }
    }

    public static final class Result {
        public final List<SessionRow> upcoming;
        public final List<SessionRow> past;
        public final Set<String> studentEmails;

        Result(List<SessionRow> upcoming, List<SessionRow> past, Set<String> emails) {
            this.upcoming = Collections.unmodifiableList(upcoming);
            this.past = Collections.unmodifiableList(past);
            this.studentEmails = Collections.unmodifiableSet(emails);
        }
    }
}
