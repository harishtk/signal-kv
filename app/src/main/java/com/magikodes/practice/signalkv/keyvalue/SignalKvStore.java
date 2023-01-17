package com.magikodes.practice.signalkv.keyvalue;

import androidx.annotation.NonNull;

import com.magikodes.practice.signalkv.database.KeyValueDatabase;
import com.magikodes.practice.signalkv.dependencies.ApplicationDependencies;

public final class SignalKvStore {

    private KeyValueStore store;

    private final MiscellaneousValues misc;

    private static volatile SignalKvStore instance;

    @NonNull
    private static SignalKvStore getInstance() {
        if (instance == null) {
            synchronized (SignalKvStore.class) {
                if (instance == null) {
                    instance = new SignalKvStore(new KeyValueStore(KeyValueDatabase.getInstance(ApplicationDependencies.getApplication())));
                }
            }
        }
        return instance;
    }

    private SignalKvStore(@NonNull KeyValueStore store) {
        this.store          = store;
        this.misc           = new MiscellaneousValues(store);
    }

    public static void onFirstEverAppLaunch() {
        misc().onFirstEverAppLaunch();
    }

    @NonNull
    public static MiscellaneousValues misc() {
        return getInstance().misc;
    }

    /**
     * Ensures any pending writes are finished. Only intended to be called by
     * SignalUncaughtExceptionHandler
     */
    public static void blockUntilAllWritesFinished() {
        getStore().blockUntilAllWritesFinished();
    }

    private static @NonNull KeyValueStore getStore() {
        return getInstance().store;
    }
}
