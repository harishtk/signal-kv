package com.magikodes.practice.signalkv.dependencies;

import android.app.Application;

import androidx.annotation.NonNull;

import com.magikodes.practice.signalkv.util.AppForegroundObserver;

import java.io.PrintStream;

public class ApplicationDependencies {

    private static final Object LOCK            = new Object();

    private static Application              application;
    private static Provider                 provider;
    private static AppForegroundObserver    appForeGroundObserver;

    public static void init(@NonNull Application application, @NonNull Provider provider) {
        synchronized (LOCK) {
            if (ApplicationDependencies.application != null || ApplicationDependencies.provider != null) {
                throw new IllegalStateException("Already initialized!");
            }

            ApplicationDependencies.application             = application;
            ApplicationDependencies.provider                = provider;
            ApplicationDependencies.appForeGroundObserver   = provider.provideAppForegroundObserver();

            ApplicationDependencies.appForeGroundObserver.begin();
        }
    }

    @NonNull
    public static Application getApplication() {
        return application;
    }

    interface Provider {
        @NonNull
        AppForegroundObserver provideAppForegroundObserver();
    }
}
