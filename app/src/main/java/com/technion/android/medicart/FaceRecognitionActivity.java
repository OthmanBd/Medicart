package com.technion.android.medicart;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import edmt.dev.edmtdevcognitiveface.Contract.Face;
import edmt.dev.edmtdevcognitiveface.Contract.IdentifyResult;
import edmt.dev.edmtdevcognitiveface.Contract.Person;
import edmt.dev.edmtdevcognitiveface.Contract.TrainingStatus;
import edmt.dev.edmtdevcognitiveface.FaceServiceClient;
import edmt.dev.edmtdevcognitiveface.FaceServiceRestClient;
import edmt.dev.edmtdevcognitiveface.Rest.ClientException;
import edmt.dev.edmtdevcognitiveface.Rest.Utils;

public class FaceRecognitionActivity extends AppCompatActivity {
    private TextView textView;
    private Button detect,identify,viewButton,gallery,camera;
    private ImageView imageView;
    private Uri imageUri;
    private Bitmap bitmap;
    private String Name ;
    private static final int PICK_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;

    private final String API_KEY = "fd1ac65c235b4a7190f693faff87ce77";
    private final String API_LINK = "https://eastus.api.cognitive.microsoft.com/face/v1.0";

    private FaceServiceClient faceServiceClient = new FaceServiceRestClient(API_LINK,API_KEY);
    private final String personGroupID = "patients";
    Face[] faceDetected;

    public void ViewPatient(View view) {
        Intent intent = new Intent(getApplicationContext(),ViewPatientActivity.class);
        intent.putExtra("Name",Name);
        startActivity(intent);
    }


    class detectTask extends AsyncTask<InputStream,String,Face[]>{

        AlertDialog alertDialog = new SpotsDialog.Builder()
                .setContext(FaceRecognitionActivity.this)
                .setCancelable(false).build();

        @Override
        protected void onPreExecute() {
            alertDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            alertDialog.setMessage(values[0]);
        }

        @Override
        protected Face[] doInBackground(InputStream... inputStreams) {
            try{
                publishProgress("Detecting...");
                Face[] result = faceServiceClient.detect(inputStreams[0],true,false,null);
                if(result == null)
                {
                    return null;
                }
                else {
                    return result;
                }
            } catch (ClientException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Face[] faces) {
            alertDialog.dismiss();
            if(faces ==null){
                Toast.makeText(FaceRecognitionActivity.this, "No face detected", Toast.LENGTH_SHORT).show();
            }
            else {
                imageView.setImageBitmap(Utils.drawFaceRectangleOnBitmap(bitmap,faces, Color.YELLOW));
                faceDetected = faces;
            }
        }
    }

    class  IdentificationTask extends AsyncTask<UUID,String, IdentifyResult[]>{

        AlertDialog alertDialog = new SpotsDialog.Builder()
                .setContext(FaceRecognitionActivity.this)
                .setCancelable(false).build();

        @Override
        protected void onPreExecute() {
            alertDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            alertDialog.setMessage(values[0]);
        }

        @Override
        protected IdentifyResult[] doInBackground(UUID... uuids) {
            try{
                publishProgress("Getting person group status...");
                TrainingStatus trainingStatus = faceServiceClient.getPersonGroupTrainingStatus(personGroupID);

                if(trainingStatus.status != TrainingStatus.Status.Succeeded){
                    Log.d("ERROR","Person Group Training status is " + trainingStatus.status);
                    return null;
                }

                publishProgress("Identifying...");
                IdentifyResult[] result = faceServiceClient.identity(personGroupID,uuids,1);
                return result;

            } catch (ClientException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(IdentifyResult[] identifyResults) {
            alertDialog.dismiss();
            if(identifyResults != null){
                for(IdentifyResult identifyResult : identifyResults)
                {
                    new PersonDetectionTask().execute(identifyResult.candidates.get(0).personId);
                }
            }
        }
    }

    class PersonDetectionTask extends AsyncTask<UUID,String, Person>{

        AlertDialog alertDialog = new SpotsDialog.Builder()
                .setContext(FaceRecognitionActivity.this)
                .setCancelable(false).build();

        @Override
        protected void onPreExecute() {
            alertDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            alertDialog.setMessage(values[0]);
        }

        @Override
        protected Person doInBackground(UUID... uuids) {
            try {
                return faceServiceClient.getPerson(personGroupID,uuids[0]);
            } catch (ClientException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Person person) {
            alertDialog.dismiss();
            //imageView.setImageBitmap(Utils.drawFaceRectangleWithTextOnBitmap(bitmap,faceDetected,person.name,Color.YELLOW,100));
            textView.setText(person.name);
            Name = person.name;
            viewButton.setVisibility(View.VISIBLE);
            //getPatientID(person.name);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);
        imageView = findViewById(R.id.face_recognition_imageview);
        viewButton = findViewById(R.id.face_recognition_viewButton);
        gallery = findViewById(R.id.face_recognition_gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
                textView.setText("");
                viewButton.setVisibility(View.GONE);
            }
        });
        camera = findViewById(R.id.face_recognition_camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermession();
            }
        });
        detect = findViewById(R.id.face_recognition_button_detect);
        identify = findViewById(R.id.face_recognition_button_identify);
        textView = findViewById(R.id.face_recognition_textview);

        //final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.angelina);
        imageView.setImageBitmap(bitmap);

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                new detectTask().execute(inputStream);
            }
        });
        identify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(faceDetected.length > 0){
                    final UUID[] faceIds = new UUID[faceDetected.length];
                    for(int i = 0; i < faceDetected.length ; i++){
                        faceIds[i] = faceDetected[i].faceId;
                        new IdentificationTask().execute(faceIds);
                    }
                }
                else {
                    Toast.makeText(FaceRecognitionActivity.this, "No face to detect", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void askCameraPermession(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA} , CAMERA_PERM_CODE);
        }
        else{
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openCamera();
            }
            else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera(){
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera,CAMERA_REQUEST_CODE);
    }
    private void openFileChooser(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                imageUri = data.getData();
                Glide.with(this).load(imageUri).into(imageView);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(requestCode == CAMERA_REQUEST_CODE){
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
    }
}