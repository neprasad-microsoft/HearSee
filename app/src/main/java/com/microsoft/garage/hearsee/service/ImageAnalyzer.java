package com.microsoft.garage.hearsee.service;

import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageAnalysis;

import io.reactivex.rxjava3.core.Observable;

public interface ImageAnalyzer {
    Observable<ImageAnalysis> analyze(String url);
}
