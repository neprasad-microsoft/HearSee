package com.microsoft.garage.hearsee.service;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AzureSpeechSynthesizer implements SpeechService {

    private final SpeechSynthesizer synthesizer;

    @Inject
    public AzureSpeechSynthesizer(SpeechSynthesizer speechSynthesizer) {
        synthesizer = speechSynthesizer;
    }

    @Override
    public void speak(final String strSpeech) {
        Observable.fromCallable(() -> synthesizer.SpeakText(strSpeech))
                .subscribeOn(Schedulers.io())
                .subscribe(result -> {
                    if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                        log.error("Speech synthesis succeeded.");
                    } else {
                        log.error("Speech Synthesis FAILED");
                        String cancellationDetails =
                                SpeechSynthesisCancellationDetails.fromResult(result).toString();
                        log.error("Error synthesizing. Error detail: " +
                                System.lineSeparator() + cancellationDetails +
                                System.lineSeparator() + "Did you update the subscription info?");
                    }
                });
    }
}
