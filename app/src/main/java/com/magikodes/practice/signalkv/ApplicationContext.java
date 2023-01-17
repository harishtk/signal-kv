package com.magikodes.practice.signalkv;

import android.app.Application;

import com.magikodes.practice.signalkv.crypto.DatabaseSecretProvider;
import com.magikodes.practice.signalkv.database.SqlCipherLibraryLoader;
import com.magikodes.practice.signalkv.dependencies.ApplicationDependencies;
import com.magikodes.practice.signalkv.dependencies.ApplicationDependencyProvider;
import com.magikodes.practice.signalkv.util.AppStartup;

public class ApplicationContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppStartup.getInstance().addBlocking("sqlcipher-init", () -> {
            SqlCipherLibraryLoader.load();
            DatabaseSecretProvider.getOrCreateDatabaseSecret(this);
        })
                .addBlocking("app-dependencies", this::initializeAppDependencies)
                .execute();
    }

    private void initializeAppDependencies() {
        ApplicationDependencies.init(this, new ApplicationDependencyProvider(this));
    }
}
