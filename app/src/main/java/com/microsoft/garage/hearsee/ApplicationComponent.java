package com.microsoft.garage.hearsee;

import android.app.Application;

import com.microsoft.garage.hearsee.activities.ImageViewActivity;
import com.microsoft.garage.hearsee.activities.MainActivity;
import com.microsoft.garage.hearsee.modules.BasicModule;
import com.microsoft.garage.hearsee.modules.ComputerVisionModule;
import com.microsoft.garage.hearsee.modules.SpeechSynthesizerModule;

import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {
        BasicModule.class,
        ComputerVisionModule.class,
        SpeechSynthesizerModule.class
})
public interface ApplicationComponent {

    @Component.Builder
    public interface Builder {
        @BindsInstance
        Builder application(Application application);

        ApplicationComponent build();
    }

    void inject(MainActivity activity);
    void inject(ImageViewActivity activity);
    void inject(ImageCustomView activity);
}
