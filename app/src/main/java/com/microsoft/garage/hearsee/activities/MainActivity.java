package com.microsoft.garage.hearsee.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.garage.hearsee.HearSeeApplication;
import com.microsoft.garage.hearsee.R;
import com.microsoft.garage.hearsee.service.ImageAnalyzer;

import java.util.stream.Stream;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET
    };

    @Inject
    ImageAnalyzer imageAnalyzer;

    private Preview preview;
    private PreviewView viewFinder;
    private FloatingActionButton cameraCaptureButton;
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((HearSeeApplication) getApplication()).applicationComponent.inject(this);
        setContentView(R.layout.activity_main);

        viewFinder = findViewById(R.id.viewFinder);
        cameraCaptureButton = findViewById(R.id.cameraCaptureButton);

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

                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview);
                if (preview != null) {
                    preview.setSurfaceProvider(viewFinder.createSurfaceProvider());
                }
            } catch (Exception e) {
                log.error("Error processing camera request", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }
}