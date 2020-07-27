package com.microsoft.garage.hearsee;

import android.app.Application;

public class HearSeeApplication extends Application {

    public ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.builder()
                .application(this)
                .build();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        applicationComponent = null;
    }
}
