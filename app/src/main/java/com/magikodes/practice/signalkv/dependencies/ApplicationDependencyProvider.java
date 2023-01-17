package com.magikodes.practice.signalkv.dependencies;

import android.app.Application;

import androidx.annotation.NonNull;

import com.magikodes.practice.signalkv.util.AppForegroundObserver;

public class ApplicationDependencyProvider implements ApplicationDependencies.Provider {

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final Application context;

    public ApplicationDependencyProvider(Application context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AppForegroundObserver provideAppForegroundObserver() {
        return new AppForegroundObserver();
    }
}
