package com.microsoft.garage.hearsee.service;

import android.graphics.Bitmap;
import android.util.Log;

import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionClient;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageAnalysis;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.VisualFeatureTypes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AzureImageAnalyzer implements ImageAnalyzer {
    private static final List<VisualFeatureTypes> FEATURE_TYPES = Arrays.asList(
            VisualFeatureTypes.DESCRIPTION,
            VisualFeatureTypes.CATEGORIES,
            VisualFeatureTypes.COLOR,
            VisualFeatureTypes.FACES,
            VisualFeatureTypes.IMAGE_TYPE,
            VisualFeatureTypes.TAGS,
            VisualFeatureTypes.OBJECTS,
            VisualFeatureTypes.BRANDS
    );

    Lazy<ComputerVisionClient> computerVisionClient;

    @Inject
    public AzureImageAnalyzer(Lazy<ComputerVisionClient> cvClient) {
        computerVisionClient = cvClient;
    }

    @Override
    public Observable<ImageAnalysis> analyze(String url) {
        return Observable.fromCallable(() ->
            computerVisionClient.get().computerVision()
                    .analyzeImage()
                    .withUrl(url)
                    .withVisualFeatures(FEATURE_TYPES)
                    .execute()
        ).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<ImageAnalysis> analyze(Bitmap bitmap) {
        ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bitmapStream);
        return Observable.fromCallable(() ->

                computerVisionClient.get().computerVision()
                        .analyzeImageInStream()
                        .withImage(bitmapStream.toByteArray())
                        .withVisualFeatures(FEATURE_TYPES)
                        .execute()
        ).subscribeOn(Schedulers.io());
    }
}
