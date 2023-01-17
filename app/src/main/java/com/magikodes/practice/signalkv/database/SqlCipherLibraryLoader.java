package com.magikodes.practice.signalkv.database;

public final class SqlCipherLibraryLoader {
    private SqlCipherLibraryLoader() {}

    private static volatile boolean loaded = false;
    private static final Object LOCK = new Object();

    public static void load() {
        if (!loaded) {
            synchronized (LOCK) {
                if (!loaded) {
                    System.loadLibrary("sqlcipher");
                    loaded = true;
                }
            }
        }
    }
}
