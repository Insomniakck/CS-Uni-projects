package com.example.androidgmail.repositories;

import android.content.Context;
import android.content.SharedPreferences;

public final class SessionManager {

    private static final String PREFS = "gmail_session";
    private static final String KEY_ID = "uid";
    private static String cachedId;           // in-memory cache
    private SessionManager() {}

    /* ----  write  ---- */
    public static void setUserId(Context ctx, String id) {
        cachedId = id;
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_ID, id).apply();
    }

    /* ----  read  ---- */
    public static String getUserId(Context ctx) {
        if (cachedId != null) return cachedId;
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        cachedId = prefs.getString(KEY_ID, null);
        return cachedId != null ? cachedId : "";
    }

    /* ----  clear on logout  ---- */
    public static void clear(Context ctx) {
        cachedId = null;
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().remove(KEY_ID).apply();
    }
    public static boolean isLoggedIn(Context ctx) {
        // If user ID exists and is not empty, consider user logged in
        String userId = getUserId(ctx);
        return userId != null && !userId.isEmpty();
    }
}

