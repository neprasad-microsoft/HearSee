package com.microsoft.garage.hearsee.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.microsoft.garage.hearsee.HearSeeApplication;
import com.microsoft.garage.hearsee.R;
import com.microsoft.garage.hearsee.service.ImageAnalyzer;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainActivity extends AppCompatActivity {

    @Inject
    ImageAnalyzer imageAnalyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((HearSeeApplication) getApplication()).applicationComponent.inject(this);

        final String url = "https://images.unsplash.com/photo-1556685704-1985e999a0d4?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=2767&q=80";
        imageAnalyzer.analyze(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(imageAnalysis -> {
                    log.debug("Got image analysis {}", imageAnalysis.description().toString());
                });
    }
}