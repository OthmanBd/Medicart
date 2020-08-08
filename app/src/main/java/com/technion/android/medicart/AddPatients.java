package com.technion.android.medicart;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddPatients extends AppCompatActivity {
    private String TAG="AddPatientActivity";
    Button button;
    ImageView imageview;
    Uri imageUri;
    private boolean imageFlag;
    private Calendar calendar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    StorageReference storagereference;
    private EditText patientNameText,patientIDText,weightText,heightText;
    private EditText bloodPressureText,allergiesText,diagnosisText;
    private Spinner spinner1,spinner2,spinner3,bloodTypeSpinner;
    private String addedBy = "";
    private TextView choose;
    private static final int PICK_IMAGE=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patients);
        mAuth = FirebaseAuth.getInstance();
        getUserName();
        calendar = Calendar.getInstance();
        button = findViewById(R.id.button);
        imageview = findViewById(R.id.imageview);
        choose = findViewById(R.id.addPatient_choose);
        patientNameText = findViewById(R.id.name);
        patientIDText = findViewById(R.id.id);
        weightText = findViewById(R.id.weight);
        heightText = findViewById(R.id.height);
        bloodTypeSpinner = findViewById(R.id.blood_type_spinner);
        ArrayAdapter<String> myBloodType = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.BloodType));
        myBloodType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodTypeSpinner.setAdapter(myBloodType);
        bloodPressureText = findViewById(R.id.pressure);
        allergiesText = findViewById(R.id.allergies);
        diagnosisText = findViewById(R.id.diagnosis);
        spinner1 = findViewById(R.id.spinner1);
        ArrayAdapter<String> mySpinner1 = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.numbers));
        mySpinner1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(mySpinner1);
        spinner2 = findViewById(R.id.spinner2);
        ArrayAdapter<String> mySpinner2 = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.numbers));
        mySpinner2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(mySpinner2);
        spinner3 = findViewById(R.id.spinner3);
        ArrayAdapter<String> mySpinner3 = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.numbers));
        mySpinner3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(mySpinner3);
        storage = FirebaseStorage.getInstance();
        storagereference = storage.getReference().child("PatientsImages");
        imageFlag = false;
    }

    private void getUserName() {
        db.collection("users").document(mAuth.getCurrentUser().getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            addedBy = documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("LastName");
                        }
                    }
                });
    }

    public void PatientAdded(View v){
        String patientName = patientNameText.getText().toString();
        final String patientID = patientIDText.getText().toString();
        String weight = weightText.getText().toString();
        String height = heightText.getText().toString();
        String bloodPressure = bloodPressureText.getText().toString();
        String allergies = allergiesText.getText().toString();
        String diagnosis = diagnosisText.getText().toString();
        if(patientID.isEmpty() || patientName.isEmpty() || weight.isEmpty() || height.isEmpty()
        || bloodPressure.isEmpty() || diagnosis.isEmpty() ||
                bloodTypeSpinner.getSelectedItem().toString().equals("Blood type")){
            Toast.makeText(getApplicationContext(), "Some fields are missing.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if(!imageFlag){
            Toast.makeText(getApplicationContext(), "Patient picture is missing.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        // Create a new user with a first and last name
        Map<String, Object> patient = new HashMap<>();
        final Map<String,String> log = new HashMap<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd,MMM yyyy");
        String date = simpleDateFormat.format(calendar.getTime());
        simpleDateFormat = new SimpleDateFormat("hh:mm a");
        String hour = simpleDateFormat.format(calendar.getTime());
        log.put("Date",date);
        log.put("Time",hour);
        log.put("Diagnosis",diagnosis);
        log.put("AddedBy",addedBy);
        if(allergies.isEmpty()){
            log.put("Allergies","None");
        }
        else log.put("Allergies",allergies);
        patient.put("Full name", patientName);
        patient.put("ID", patientID);
        patient.put("Weight", weight);
        patient.put("Height",height);
        patient.put("Blood type",bloodTypeSpinner.getSelectedItem().toString());
        patient.put("Blood pressure",bloodPressure);
        patient.put("AddedBy",addedBy);
        if(allergies.isEmpty()){
            patient.put("Allergies","None");
        }
        else patient.put("Allergies",allergies);
        patient.put("Diagnosis",diagnosis);
        patient.put("Pill 1",spinner1.getSelectedItem().toString());
        patient.put("Pill 2",spinner2.getSelectedItem().toString());
        patient.put("Pill 3",spinner3.getSelectedItem().toString());
        db.collection("patients").document(patientID).set(patient)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + patientID);
                        //Toast.makeText(getApplicationContext(), "Added Successfully.", Toast.LENGTH_SHORT).show();
                        db.collection("patients").document(patientID).collection("Medical history").add(log)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(getApplicationContext(), "Added Successfully.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        return;
                    }
                });
        uploadImage(patientID);
        finish();
    }
    public void AddImage(View v){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null && data.getData() != null){
            imageUri = data.getData();
            imageview.setImageURI(imageUri);
            imageFlag = true;
            choose.setVisibility(View.GONE);
        }
    }
    private void uploadImage(String id){
        StorageReference imageName = storagereference.child(id);
        imageName.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Uploading failed",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
