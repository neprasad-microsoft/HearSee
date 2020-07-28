package com.microsoft.garage.hearsee.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.microsoft.garage.hearsee.HearSeeApplication;
import com.microsoft.garage.hearsee.R;
import com.microsoft.garage.hearsee.service.ImageAnalyzer;

import java.io.IOException;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageViewActivity extends AppCompatActivity {
    public static final String EXTRA_IMAGE_URI = "com.microsoft.garage.hearsee.ImageViewActivity.imageUri";

    private ImageView imageView;

    @Inject
    ImageAnalyzer imageAnalyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ((HearSeeApplication) getApplication()).applicationComponent.inject(this);
        imageView = findViewById(R.id.imageView);

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
            Bitmap bitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            // Ignore
            log.error("Error getting EXIF interface", e);
        }
    }
}