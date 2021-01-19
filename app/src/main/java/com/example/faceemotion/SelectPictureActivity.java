package com.example.faceemotion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SelectPictureActivity extends AppCompatActivity {

    // Request Codes
    private static final int FILE_CHOOSE_CODE = 100;
    private static final int REQUEST_CODE_PERMISSION = 101;
    private static final int IMAGE_CAPTURE_CODE = 102;

    public Button btnChooseImage, btnTakePicture;
    public ImageView image;
    public String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectpicture);

        image = findViewById(R.id.imageView);
        btnTakePicture = findViewById(R.id.takePic);
        btnChooseImage = findViewById(R.id.choosePic);

        btnTakePicture.setOnClickListener(view -> {
            openCamera();
        });
        btnChooseImage.setOnClickListener(view -> {

            // Android SDK >= 23, therefore we need to ask user for permission
            if (ContextCompat.checkSelfPermission(SelectPictureActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                askPermission();
            }
            browseImage();
        });

    }

    // Requesting permission from user
    public void askPermission() {
        ActivityCompat.requestPermissions(SelectPictureActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
    }

    // Opens file browser for user
    public void browseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*"); // only show images

        intent = Intent.createChooser(intent, "Choose a file");
        startActivityForResult(intent, FILE_CHOOSE_CODE);
    }

    public void openCamera() {
        Intent startCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile(); // gets a temporary file path

            // Getting content uri with fileprovider
            Uri photoUri = FileProvider.getUriForFile(this, "com.example.faceemotion.fileprovider", photoFile);
            //Uri photoUri = Uri.fromFile(photoFile); // converts photo path to uri
            startCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            startActivityForResult(startCamera, IMAGE_CAPTURE_CODE); // starting up camera
        } catch (ActivityNotFoundException e) {
            Toast.makeText(SelectPictureActivity.this, "No Camera Found", Toast.LENGTH_SHORT).show();
        } catch (IOException i) {
            Toast.makeText(SelectPictureActivity.this, "Failed to create file location", Toast.LENGTH_SHORT).show();
        }
    }

    // Creates temporary image file for camera photo
    public File createImageFile() throws IOException {

        // Using the current time as filename
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(time, ".jpg", storageDirectory);

        imagePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Checks if permission was granted and if so, opens image browser
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            browseImage();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    // Getting results from both selecting a image and taking an image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Checks if user took an image from camera
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == Activity.RESULT_OK) {
            //Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = false;

            // Turning imagepath to a bitmap and displaying it in the imageview
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmpFactoryOptions);
            image.setImageBitmap(bitmap);

        // Checks if the user chose a file
        } else if (requestCode == FILE_CHOOSE_CODE && resultCode == Activity.RESULT_OK) {
            try {
                // Displays the selected image on the image view
                Uri imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(SelectPictureActivity.this.getContentResolver(), imageUri);
                imagePath = imageUri.getPath();
                image.setImageBitmap(bitmap);

            // Error message
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to get Image", Toast.LENGTH_LONG).show();
        }
    }
}
