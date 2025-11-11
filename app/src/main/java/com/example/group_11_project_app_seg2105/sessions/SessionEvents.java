package com.example.group_11_project_app_seg2105.sessions;

import java.util.concurrent.CopyOnWriteArrayList;

public final class SessionEvents {

    public interface Listener {
        void onSessionStatusChanged(long sessionId, String newStatus);
    }

    private static final CopyOnWriteArrayList<Listener> LISTENERS = new CopyOnWriteArrayList<>();

    private SessionEvents() {}

    public static void register(Listener listener) {
        if (listener == null) return;
        if (!LISTENERS.contains(listener)) {
            LISTENERS.add(listener);
        }
    }

    public static void unregister(Listener listener) {
        if (listener == null) return;
        LISTENERS.remove(listener);
    }

    public static void emitStatusChanged(long sessionId, String newStatus) {
        for (Listener listener : LISTENERS) {
            listener.onSessionStatusChanged(sessionId, newStatus);
        }
    }
}
