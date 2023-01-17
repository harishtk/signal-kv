package com.magikodes.practice.signalkv.database;

import net.zetetic.database.sqlcipher.SQLiteDatabase;

/**
 * Simple interface for common methods across ou various
 */
public interface SignalDatabaseOpenHelper {
    SQLiteDatabase getSqlCipherDatabase();
    String getDatabaseName();
}
