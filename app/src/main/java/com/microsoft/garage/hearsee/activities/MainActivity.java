package com.microsoft.garage.hearsee.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.garage.hearsee.HearSeeApplication;
import com.microsoft.garage.hearsee.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET
    };

    private Preview preview;
    private PreviewView viewFinder;
    private Camera camera;
    private ImageCapture imageCapture;
    private final SimpleDateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((HearSeeApplication) getApplication()).applicationComponent.inject(this);
        setContentView(R.layout.activity_main);

        viewFinder = findViewById(R.id.viewFinder);
        FloatingActionButton cameraCaptureButton = findViewById(R.id.cameraCaptureButton);

        cameraCaptureButton.setOnClickListener(button -> takePhoto());

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(getApplicationContext(), "Permissions need to be granted.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private Boolean allPermissionsGranted() {
        return Stream.of(REQUIRED_PERMISSIONS)
                .allMatch(permission -> ContextCompat.checkSelfPermission(getApplicationContext(), permission) ==
                        PackageManager.PERMISSION_GRANTED);
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                preview = new Preview.Builder()
                        .build();
                CameraSelector cameraSelector = (new CameraSelector.Builder()).requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                cameraProvider.unbindAll();

                imageCapture = new ImageCapture.Builder()
                        .build();

                camera = cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview);
                if (preview != null) {
                    preview.setSurfaceProvider(viewFinder.createSurfaceProvider());
                }
            } catch (Exception e) {
                log.error("Error processing camera request", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        File photoFile = new File(getOutputDirectory(),
                fileFormat.format(System.currentTimeMillis()) + ".jpg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile)
                .build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                log.debug("Photo saved to {}", photoFile.toString());

                Intent imageViewIntent = new Intent(getApplicationContext(), ImageViewActivity.class);
                imageViewIntent.putExtra(ImageViewActivity.EXTRA_IMAGE_URI, photoFile.toString());
                startActivity(imageViewIntent);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Toast.makeText(getApplicationContext(), "Error saving image", Toast.LENGTH_LONG).show();
            }
        });
    }

    private File getOutputDirectory() {
        return getFilesDir();
    }
}