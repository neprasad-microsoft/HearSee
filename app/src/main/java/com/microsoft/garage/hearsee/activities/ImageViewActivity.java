package com.microsoft.garage.hearsee.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.microsoft.azure.cognitiveservices.vision.computervision.models.BoundingRect;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.DetectedObject;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageAnalysis;
import com.microsoft.garage.hearsee.HearSeeApplication;
import com.microsoft.garage.hearsee.ImageCustomView;
import com.microsoft.garage.hearsee.R;
import com.microsoft.garage.hearsee.service.ImageAnalyzer;
import com.microsoft.garage.hearsee.service.SpeechService;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ImageViewActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE_URI = "com.microsoft.garage.hearsee.ImageViewActivity.imageUri";

    private ImageCustomView imageView;
    private ProgressBar progressBar;
    private ImageAnalysis analysisResult = null;
    private ArrayList<Rect> analysedAreaList = new ArrayList<>();
    private ArrayList<String> descriptionList = new ArrayList<>();

    @Inject
    ImageAnalyzer imageAnalyzer;

    @Inject
    SpeechService speechService;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ((HearSeeApplication) getApplication()).applicationComponent.inject(this);
        imageView = findViewById(R.id.imageView);
        imageView.setBackgroundColor(Color.BLACK);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);

        final String imagePath = getIntent().getStringExtra(EXTRA_IMAGE_URI);
        log.debug("Image view triggered with path: {}", imagePath);

        try {
            ExifInterface exifInterface = new ExifInterface(imagePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    matrix.postRotate(0);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
            }
           Bitmap sourceBitmap = BitmapFactory.decodeFile(imagePath);
           // Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test_image);
            Bitmap bitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);

            imageView.setImageBitmap(bitmap);
            analyzeImage(bitmap);
        } catch (IOException e) {
            // Ignore
            log.error("Error getting EXIF interface", e);
        }
    }

    private void analyzeImage(Bitmap bitmap) {
        imageView.setImageAlpha(100);
        imageAnalyzer.analyze(bitmap)
                .subscribe(imageAnalysis -> {
                    log.debug("Image analysis complete: {}", imageAnalysis);
                    // TODO: Render squares that use TTS

                    // Hide progress
                    progressBar.setVisibility(View.INVISIBLE);
                    imageView.setImageAlpha(255);

                    descriptionList.clear();
                    analysedAreaList.clear();
                    int height = imageView.getHeight();
                    int width = imageView.getWidth();
                    float hRatio = (float) height/bitmap.getHeight();
                    float wRatio = (float) width/bitmap.getWidth();
                    if (imageAnalysis.objects() != null){
                        for (DetectedObject detectedObject : imageAnalysis.objects()){
                            BoundingRect bRect = detectedObject.rectangle();
                            Rect r = new Rect((int)(bRect.x()*wRatio), (int)(bRect.y() * hRatio), (int)((bRect.x() + bRect.w())*wRatio), (int)((bRect.y() + bRect.h())*hRatio));
                            analysedAreaList.add(r);
                            descriptionList.add(detectedObject.objectProperty());
                        }
                        imageView.setAnalysedAreaList(analysedAreaList);
                        imageView.setObjectDescriptionList(descriptionList);
                        String description = imageAnalysis.description().captions().get(0).text();
                        if (imageAnalysis.objects().size() == 0){
                            speechService.speak("Oops! No objects detected, but, it feels like " + description);
                        }
                        else {
                            speechService.speak(description);
                        }
                        imageView.invalidate();
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(ImageViewActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // remove all the previous activities
        startActivity(intent);

    }

}