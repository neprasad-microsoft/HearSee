package com.microsoft.garage.hearsee;

import android.app.Application;

import com.microsoft.garage.hearsee.modules.BasicModule;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {
        BasicModule.class
})
public interface ApplicationComponent {

    @Component.Builder
    public interface Builder {
        @BindsInstance
        Builder application(Application application);

        ApplicationComponent build();
    }
}
