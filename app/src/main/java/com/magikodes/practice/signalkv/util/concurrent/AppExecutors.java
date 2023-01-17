package com.magikodes.practice.signalkv.util.concurrent;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class AppExecutors {

    public static final ExecutorService BOUNDED     = Executors.newFixedThreadPool(4, new NumberedThreadFactory("signalkv-bounded"));

    @NonNull
    public static ExecutorService newCachedSingleThreadExecutor(final String name) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), r -> new Thread(r, name));
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    private static class NumberedThreadFactory implements ThreadFactory {

        private final String baseName;
        private final AtomicInteger counter;

        NumberedThreadFactory(@NonNull  String baseName) {
            this.baseName = baseName;
            this.counter = new AtomicInteger();
        }

        @Override
        @NonNull
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, baseName + "-" + counter.getAndIncrement());
        }
    }

}