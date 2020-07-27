package com.microsoft.garage.hearsee.modules;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class BasicModule {

    @Provides
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }
}
