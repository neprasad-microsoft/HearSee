package com.microsoft.garage.hearsee.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;


import com.microsoft.garage.hearsee.HearSeeApplication;
import com.microsoft.garage.hearsee.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Stream;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    static final int CAPTURE_IMAGE_REQUEST = 1;
    static final String APP_FILE_PROVIDER = "com.microsoft.garage.HearSeeApp.fileprovider";
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET
    };

    private File photoFile = null;
    private final SimpleDateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((HearSeeApplication) getApplication()).applicationComponent.inject(this);
        setContentView(R.layout.activity_main);

        if (allPermissionsGranted()) {
            String speech = "Hello, How are you?";
            startCameraAndTakePhoto();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCameraAndTakePhoto();
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

    private void startCameraAndTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {

           photoFile = createImageFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        APP_FILE_PROVIDER,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
            }
        } catch (Exception ex) {
            Log.e("startCamera", ex.getMessage());
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = fileFormat.format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            log.debug("Photo saved to {}", photoFile.toString());

            Intent imageViewIntent = new Intent(getApplicationContext(), ImageViewActivity.class);
            imageViewIntent.putExtra(ImageViewActivity.EXTRA_IMAGE_URI, photoFile.toString());
            startActivity(imageViewIntent);
        } else {
            log.error("event='ActivityFailure', result='Request cancelled or something went wrong.'");
        }
    }
}