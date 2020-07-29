package com.microsoft.garage.hearsee.modules;

import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import com.microsoft.garage.hearsee.service.AzureSpeechSynthesizer;
import com.microsoft.garage.hearsee.service.SpeechService;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module(includes = SpeechSynthesizerModule.Declarations.class)
public class SpeechSynthesizerModule {
    @Provides
    @Named("SpeechSynthesizer.Azure.SubscriptionKey")
    public String provideAzureSubscriptionKey() {
        return "7ab0dda023934eeba3880e79166683ac";
    }

    @Provides
    @Named("SpeechSynthesizer.Azure.Endpoint")
    public String provideAzureEndpoint() {
        return "westus";
    }

    @Provides
    public SpeechConfig provideAzureSpeechConfig(@Named("SpeechSynthesizer.Azure.SubscriptionKey") final String subscriptionKey,
                                                 @Named("SpeechSynthesizer.Azure.Endpoint") final String endpoint) {
        return SpeechConfig.fromSubscription(subscriptionKey, endpoint);
    }

    @Provides
    public SpeechSynthesizer provideAzureSpeechSynthesizer(final SpeechConfig config) {
        return new SpeechSynthesizer(config);
    }

    @Provides
    public AzureSpeechSynthesizer provideSpeechSynthesizer(final SpeechSynthesizer speechSynthesizer) {
        return new AzureSpeechSynthesizer(speechSynthesizer);
    }

    @Module
    public interface Declarations {
        @Binds
        public SpeechService provideSpeechService(AzureSpeechSynthesizer azureSpeechSynthesizer);
    }
}
