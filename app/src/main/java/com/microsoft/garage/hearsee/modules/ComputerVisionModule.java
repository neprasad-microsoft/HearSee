package com.microsoft.garage.hearsee.modules;

import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionClient;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionManager;
import com.microsoft.garage.hearsee.service.AzureImageAnalyzer;
import com.microsoft.garage.hearsee.service.ImageAnalyzer;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module(includes = ComputerVisionModule.Declarations.class)
public class ComputerVisionModule {

    @Provides
    @Named("ComputerVision.Azure.SubscriptionKey")
    public String provideAzureSubscriptionKey() {
        return "3df90fc352f444cca8ad6ab30b6e06a1";
    }

    @Provides
    @Named("ComputerVision.Azure.Endpoint")
    public String provideAzureEndpoint() {
        return "https://hearseecomputervision.cognitiveservices.azure.com/";
    }

    @Provides
    public ComputerVisionClient provideComputerVisionClient(@Named("ComputerVision.Azure.SubscriptionKey") String subscriptionKey,
                                                     @Named("ComputerVision.Azure.Endpoint") String endpoint) {
        return ComputerVisionManager.authenticate(subscriptionKey)
                .withEndpoint(endpoint);
    }

    @Module
    public interface Declarations {
        @Binds
        public ImageAnalyzer provideImageAnalyzer(AzureImageAnalyzer imageAnalyzer);
    }
}
