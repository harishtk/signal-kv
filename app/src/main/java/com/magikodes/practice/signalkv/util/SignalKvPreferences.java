package com.magikodes.practice.signalkv.util;

import android.content.Context;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SignalKvPreferences {

    private static final String TAG = "SignalKvPreferences";

    private static final String DATABASE_ENCRYPTED_SECRET     = "pref_database_encrypted_secret";
    private static final String DATABASE_UNENCRYPTED_SECRET   = "pref_database_unencrypted_secret";

    public static void setDatabaseUnencryptedSecret(@NonNull Context context, @Nullable String secret) {
        setStringPreferences(context, DATABASE_UNENCRYPTED_SECRET, secret);
    }

    public static @Nullable String getDatabaseUnencryptedSecret(@NonNull Context context) {
        return getStringPreference(context, DATABASE_UNENCRYPTED_SECRET, null);
    }

    public static void setDatabaseEncryptedSecret(@NonNull Context context, @Nullable String secret) {
        setStringPreferences(context, DATABASE_ENCRYPTED_SECRET, secret);
    }

    public static @Nullable String getDatabaseEncryptedSecret(@NonNull Context context) {
        return getStringPreference(context, DATABASE_ENCRYPTED_SECRET, null);
    }

    public static void setStringPreferences(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply();
    }


    public static String getStringPreference(Context context, String key, String value) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, value);
    }
}
