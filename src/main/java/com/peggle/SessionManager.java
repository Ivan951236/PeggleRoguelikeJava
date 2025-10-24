package com.peggle;

import java.security.SecureRandom;

public final class SessionManager {
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{};:,.<>/?|~`'\\\"";
    private static final int SESSION_LENGTH = 128;
    private static final String SESSION_ID = generateSessionId();

    private SessionManager() {}

    public static String getSessionId() {
        return SESSION_ID;
    }

    private static String generateSessionId() {
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(SESSION_LENGTH);
        for (int i = 0; i < SESSION_LENGTH; i++) {
            int idx = rnd.nextInt(CHARSET.length());
            sb.append(CHARSET.charAt(idx));
        }
        return sb.toString();
    }
}
