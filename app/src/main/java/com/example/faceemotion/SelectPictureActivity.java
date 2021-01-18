package com.example.faceemotion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SelectPictureActivity extends AppCompatActivity {
    private static final int FILE_CHOOSE = 100;
    private static final int REQUEST_CODE_PERMISSION = 101;

    public Button btnChooseImage;
    public ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectpicture);
        image = findViewById(R.id.imageView);
        btnChooseImage = findViewById(R.id.choosePic);

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
        startActivityForResult(intent, FILE_CHOOSE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Checks if permission was granted and if so, opens image browser
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            browseImage();
        }
        else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    //
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Checks if the user choose a file
        if (requestCode == FILE_CHOOSE && resultCode == Activity.RESULT_OK) {
            try {

                // Displays the selected image on the image view
                Uri imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(SelectPictureActivity.this.getContentResolver(), imageUri);
                String imagePath = imageUri.getPath();
                image.setImageBitmap(bitmap);
            }

            // Error message
            catch (Exception e) {
                Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Image not Picked", Toast.LENGTH_LONG).show();
        }
    }
}