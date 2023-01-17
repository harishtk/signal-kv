package com.magikodes.practice.signalkv.keyvalue;

import android.media.audiofx.DynamicsProcessing;
import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.magikodes.practice.signalkv.util.concurrent.AppExecutors;

import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public final class KeyValueStore implements KeyValueReader {

    private static final String TAG = "KeyValueStore";

    private final ExecutorService           executor;
    private final KeyValuePersistentStorage storage;

    private KeyValueDataSet dataSet;

    public KeyValueStore(@NonNull KeyValuePersistentStorage storage) {
        this.executor = AppExecutors.newCachedSingleThreadExecutor("signalkv-keyValueStore");
        this.storage = storage;
    }

    @AnyThread
    @Override
    public synchronized byte[] getBlob(@NonNull String key, byte[] defaultValue) {
        initializeIfNecessary();
        return dataSet.getBlob(key, defaultValue);
    }

    @AnyThread
    @Override
    public synchronized boolean getBoolean(@NonNull String key, boolean defaultValue) {
        initializeIfNecessary();
        return dataSet.getBoolean(key, defaultValue);
    }

    @AnyThread
    @Override
    public synchronized float getFloat(@NonNull String key, float defaultValue) {
        initializeIfNecessary();
        return dataSet.getFloat(key, defaultValue);
    }

    @AnyThread
    @Override
    public synchronized int getInteger(@NonNull String key, int defaultValue) {
        initializeIfNecessary();
        return dataSet.getInteger(key, defaultValue);
    }

    @AnyThread
    @Override
    public synchronized long getLong(@NonNull String key, long defaultValue) {
        initializeIfNecessary();
        return dataSet.getLong(key, defaultValue);
    }

    @AnyThread
    @Override
    public synchronized String getString(@NonNull String key, String defaultValue) {
        initializeIfNecessary();
        return dataSet.getString(key, defaultValue);
    }

    @AnyThread
    @Override
    public synchronized boolean containsKey(@NonNull String key) {
        initializeIfNecessary();
        return dataSet.containsKey(key);
    }


    @AnyThread
    @NonNull
    Writer beginWrite() { return new Writer(); }


    /**
     * @return A reader that lets you read from an immutable snapshot of the store, ensuring that data
     *          is consistent between reads. If you're only reading a single value, it is more
     *          efficient to use the various get* methods instead.
     */
    @AnyThread
    @NonNull
    synchronized KeyValueReader beginRead() {
        initializeIfNecessary();

        KeyValueDataSet copy = new KeyValueDataSet();
        copy.putAll(dataSet);

        return copy;
    }

    /**
     * Ensures that any pending writes (such as those made via {@link Writer#apply()} are finished.
     */
    @AnyThread
    synchronized void blockUntilAllWritesFinished() {
        CountDownLatch latch = new CountDownLatch(1);

        executor.execute(latch::countDown);

        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.w(TAG, "Failed to wait for all writes");
        }
    }

    /**
     * Forces the store to re-fetch all of it's data from the database.
     */
    synchronized void resetCache() {
        dataSet = null;
        initializeIfNecessary();
    }

    private synchronized void write(@NonNull KeyValueDataSet newDataSet, @NonNull Collection<String> removes) {
        initializeIfNecessary();

        dataSet.putAll(newDataSet);
        dataSet.removeAll(removes);

        executor.execute(() -> storage.writeDataSet(newDataSet, removes));
    }

    private void initializeIfNecessary() {
        if (dataSet != null) return;
        this.dataSet = storage.getDataSet();
    }

    class Writer {
        private final KeyValueDataSet   dataSet = new KeyValueDataSet();
        private final Set<String>       removes = new HashSet<>();

        @NonNull Writer putBlob(@NonNull String key, @Nullable byte[] value) {
            dataSet.putBlob(key, value);
            return this;
        }

        @NonNull Writer putBoolean(@NonNull String key, boolean value) {
            dataSet.putBoolean(key, value);
            return this;
        }

        @NonNull Writer putFloat(@NonNull String key, float value) {
            dataSet.putFloat(key, value);
            return this;
        }

        @NonNull Writer putInteger(@NonNull String key, int value) {
            dataSet.putInteger(key, value);
            return this;
        }

        @NonNull Writer putLong(@NonNull String key, long value) {
            dataSet.putLong(key, value);
            return this;
        }

        @NonNull Writer putString(@NonNull String key, String value) {
            dataSet.putString(key, value);
            return this;
        }

        @NonNull Writer remove(@NonNull String key) {
            removes.add(key);
            return this;
        }

        @AnyThread
        void apply() {
            for (String key : removes) {
                if (dataSet.containsKey(key)) {
                    throw new IllegalStateException("Tried to remove a key while also setting it!");
                }
            }

            write(dataSet, removes);
        }

        @WorkerThread
        void commit() {
            apply();
            blockUntilAllWritesFinished();
        }
    }
}
