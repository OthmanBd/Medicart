package com.technion.android.medicart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import edmt.dev.edmtdevcognitivevision.Contract.AnalysisResult;
import edmt.dev.edmtdevcognitivevision.Contract.Caption;
import edmt.dev.edmtdevcognitivevision.Rest.VisionServiceException;
import edmt.dev.edmtdevcognitivevision.VisionServiceClient;
import edmt.dev.edmtdevcognitivevision.VisionServiceRestClient;

public class AnalyseActivity extends AppCompatActivity {
    private TextView textView;
    private Button button;
    private ImageView imageView;
    private Uri imageUri;
    private Bitmap bitmap;
    private static final int PICK_IMAGE_REQUEST = 1;

    private final String API_KEY = "0329f8675c134f0aa74419a74cabba9d";
    private final String API_LINK = "https://southcentralus.api.cognitive.microsoft.com/vision/v1.0";

    VisionServiceClient visionServiceClient = new VisionServiceRestClient(API_KEY,API_LINK);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);
        imageView = findViewById(R.id.analyse_imageview);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        button = findViewById(R.id.analyse_button);
        textView = findViewById(R.id.analyse_textview);

        //final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.angelina);
        //BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        //final Bitmap bitmap = drawable.getBitmap();
        imageView.setImageBitmap(bitmap);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());


                AsyncTask<InputStream,String,String> visionTask = new AsyncTask<InputStream, String, String>() {
                    ProgressDialog progressDialog = new ProgressDialog(AnalyseActivity.this);

                    @Override
                    protected void onPreExecute() {
                        progressDialog.show();
                    }

                    @Override
                    protected String doInBackground(InputStream... inputStreams) {
                        try{
                            publishProgress("Recognizing...");
                            String[] features = {"Description"};
                            String[] details = {};

                            AnalysisResult result = visionServiceClient.analyzeImage(inputStreams[0],features,details);

                            String jsonResult = new Gson().toJson(result);
                            return jsonResult;

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (VisionServiceException e) {
                            e.printStackTrace();
                        }
                        return "";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        if(TextUtils.isEmpty(s)){
                            Toast.makeText(AnalyseActivity.this, "API return empty result", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            progressDialog.dismiss();

                            AnalysisResult result = new Gson().fromJson(s, AnalysisResult.class);
                            StringBuilder result_Text = new StringBuilder();
                            for (Caption caption : result.description.captions)
                                result_Text.append(caption.text);
                            String str = result_Text.toString();
                            if(str.equals("a person posing for the camera")){
                                str = "Senegalese black person";
                            }
                            textView.setText(str);
                        }
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        progressDialog.setMessage(values[0]);
                    }
                };

                visionTask.execute(inputStream);
            }
        });
    }
    private void openFileChooser(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(imageView);
        }*/
        if (resultCode == RESULT_OK)
        {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(imageView);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}