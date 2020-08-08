package com.technion.android.medicart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class HistoryActivity extends AppCompatActivity {
    private String date,hour,email,patientID,doctorName;
    private Calendar calendar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mAuth = FirebaseAuth.getInstance();
        calendar = Calendar.getInstance();
        email = mAuth.getCurrentUser().getEmail();
        patientID = getIntent().getStringExtra("ID");
        db.collection("users").document(mAuth.getCurrentUser().getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        doctorName  = documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("LastName");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Error has occured",Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void medicalHistory(View v){
        Intent intent = new Intent(this,MedicalActivity.class);
        intent.putExtra("ID",getIntent().getStringExtra("ID"));
        startActivity(intent);
    }
    public void caregiversHistory(View v){
        Intent intent = new Intent(this,CaregiversActivity.class);
        intent.putExtra("ID",getIntent().getStringExtra("ID"));
        startActivity(intent);
    }
    public void giveTreatment(View view) {
        Intent intent = new Intent(this,BluetoothActivity.class);
        intent.putExtra("ID",getIntent().getStringExtra("ID"));
        intent.putExtra("Email",email);
        intent.putExtra("Name",doctorName);
        intent.putExtra("Pill 1",getIntent().getStringExtra("Pill 1"));
        intent.putExtra("Pill 2",getIntent().getStringExtra("Pill 2"));
        intent.putExtra("Pill 3",getIntent().getStringExtra("Pill 3"));
        startActivity(intent);
        /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd,MMM yyyy");
        date = simpleDateFormat.format(calendar.getTime());
        simpleDateFormat = new SimpleDateFormat("hh:mm a");
        hour = simpleDateFormat.format(calendar.getTime());
        Map<String, String> log = new HashMap<>();
        log.put("Name",doctorName);
        log.put("Date",date);
        log.put("Time",hour);
        log.put("Email",email);
        db.collection("patients").document(patientID).collection("CaregiversLog").add(log)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Toast.makeText(getApplicationContext(),"log saved successfully.",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"uploading log has failed.",Toast.LENGTH_SHORT).show();
                    }
                });*/
    }
}
