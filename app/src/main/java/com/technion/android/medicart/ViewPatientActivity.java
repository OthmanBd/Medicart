package com.technion.android.medicart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewPatientActivity extends AppCompatActivity {
    private TextView name,id,height,weight,bloodType,bloodPressure,doctor,allergies,diagnosis,prescription;
    private EditText patient;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView image;
    FirebaseStorage storage;
    StorageReference storagereference;
    private Intent logIntent;
    private Button logButton;
    private LinearLayout search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient);
        name = findViewById(R.id.name_view);
        id = findViewById(R.id.id_view);
        height = findViewById(R.id.height_view);
        weight = findViewById(R.id.weight_view);
        bloodType = findViewById(R.id.bloodtype_view);
        bloodPressure = findViewById(R.id.bloodpressure_view);
        doctor = findViewById(R.id.doctorsname_view);
        allergies = findViewById(R.id.allergies_view);
        diagnosis = findViewById(R.id.diagnosis_view);
        prescription = findViewById(R.id.prescription_view);
        patient = findViewById(R.id.id_edittext);
        image = findViewById(R.id.view_imageview);
        logButton = findViewById(R.id.log_button);
        search = findViewById(R.id.search_layout);
        storage = FirebaseStorage.getInstance();
        storagereference = storage.getReference().child("PatientsImages");
        logIntent = new Intent(getApplicationContext(),HistoryActivity.class);
        if(getIntent().getStringExtra("Name") != null ){
            String patientName = getIntent().getStringExtra("Name");
            search.setVisibility(View.GONE);
            getPatientID(patientName);
        }
    }

    public void view(View view) {
        String patientID = patient.getText().toString();
        if(patientID.isEmpty()){
            patient.setError("required.");
            return;
        }
        viewPatient(patientID);

    }
    private void viewPatient(final String patientID){
        db.collection("patients").document(patientID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            name.setText("Full name: " + documentSnapshot.getString("Full name"));
                            id.setText("ID: " + documentSnapshot.getString("ID"));
                            height.setText("Height: " + documentSnapshot.getString("Height"));
                            weight.setText("Weight: " + documentSnapshot.getString("Weight"));
                            bloodType.setText("Blood type: " + documentSnapshot.getString("Blood type"));
                            bloodPressure.setText("Blood pressure: " + documentSnapshot.getString("Blood pressure"));
                            doctor.setText("Added by: " + documentSnapshot.getString("Added by"));
                            allergies.setText("Allergies: " + documentSnapshot.getString("Allergies"));
                            diagnosis.setText("Diagnosis: " + documentSnapshot.getString("Diagnosis"));
                            prescription.setText("Pill 1: " + documentSnapshot.getString("Pill 1") + "  Pill 2: " + documentSnapshot.getString("Pill 2")
                                    + "  Pill 3: " + documentSnapshot.getString("Pill 3"));
                            storagereference.child(patientID).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Glide.with(getApplicationContext()).load(uri).into(image);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),"Picture loading error.",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            logIntent.putExtra("ID",patientID);
                            logIntent.putExtra("Pill 1",documentSnapshot.getString("Pill 1"));
                            logIntent.putExtra("Pill 2",documentSnapshot.getString("Pill 2"));
                            logIntent.putExtra("Pill 3",documentSnapshot.getString("Pill 3"));
                            logButton.setVisibility(View.VISIBLE);
                            patient.setText("");
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"No patient with the provided ID.",Toast.LENGTH_SHORT).show();
                            patient.setText("");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Error accured.",Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void getPatientID(String name){
        db.collection("patients").whereEqualTo("Full name",name).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null && queryDocumentSnapshots.getDocuments().get(0) != null) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            if (documentSnapshot.exists()) {
                                String ID = documentSnapshot.getString("ID");
                                viewPatient(ID);
                            } else
                                Toast.makeText(ViewPatientActivity.this, "Error finding patient", Toast.LENGTH_SHORT).show();
                        }
                        else Toast.makeText(ViewPatientActivity.this, "Error finding patient", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ViewPatientActivity.this, "Error finding patient in the system", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void viewLog(View view) {
        startActivity(logIntent);
    }
}
