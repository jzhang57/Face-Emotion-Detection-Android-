package com.example.faceemotion;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmotionActivity extends AppCompatActivity {

    // Azure api subscription keys
    private static final String subscriptionKey = "";
    private static final String endpoint = "";

    public ImageView img;
    public TextView txt;
    public Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion);

        img = findViewById(R.id.processedImg);
        txt = findViewById(R.id.emotionTxt);

        // Retrieving image passed by uri or file path
        try {
            bitmap = MediaStore.Images.Media.getBitmap(EmotionActivity.this.getContentResolver(), getIntent().getParcelableExtra("URI"));
        } catch (Exception e) {
            bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("PATH"));
        }

        img.setImageBitmap(bitmap);

        new RequestEmotionsTask().execute();

    }

    private class RequestEmotionsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... param) {

            try {
                URL requestUrl = new URL(endpoint + "/face/v1.0/detect?returnFaceAttributes=emotion&returnFaceId=false&detectionModel=detection_01");
                HttpURLConnection http = (HttpURLConnection) requestUrl.openConnection();

                // Making Post Call
                http.setRequestMethod("POST");
                http.setDoOutput(true);
                http.setDoInput(true);

                // Setting headers
                http.setRequestProperty("Content-Type", "application/json");
                //http.setRequestProperty("Content-Type", "application/octet-stream");
                http.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);

                String jsonInputString = "{\"url\": \"https://www.thestatesman.com/wp-content/uploads/2017/08/1493458748-beauty-face-517.jpg\"}";

                // Converting bitmap to binary data
/*                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArr = stream.toByteArray();
                String jsonInputString = "{" + Base64.encodeToString(byteArr, Base64.DEFAULT) + "}";*/
                //byte[] temp = Base64.decode(jsonInputString, Base64.DEFAULT);

                // Adding bitmap to body of post request
                OutputStream output = http.getOutputStream();
                byte[] temp = jsonInputString.getBytes("UTF-8");
                output.write(temp);
                output.flush();
                output.close();

                BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream(), "utf-8"));

                String line;
                txt.setText(""); // clearing textview

                while ((line = input.readLine()) != null) {
                    Log.d("asdadsada", line);
                    txt.append(line);
                }

                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}