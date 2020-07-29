package com.microsoft.garage.hearsee.service;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AzureSpeechSynthesizer {
    private static String speechSubscriptionKey = "7ab0dda023934eeba3880e79166683ac";
    private static String serviceRegion = "westus";
    private SpeechConfig speechConfig;
    private SpeechSynthesizer synthesizer;

    public void speak(String strSpeech)
    {
        speechConfig = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
        assert(speechConfig != null);

        synthesizer = new SpeechSynthesizer(speechConfig);
        assert(synthesizer != null);
        SpeechSynthesisResult result = synthesizer.SpeakText(strSpeech);
        assert(result != null);

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

    }

}
