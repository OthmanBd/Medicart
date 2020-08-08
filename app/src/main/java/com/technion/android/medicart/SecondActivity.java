package com.technion.android.medicart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SecondActivity extends AppCompatActivity {
    private String TAG="SecondActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mAuth = FirebaseAuth.getInstance();
        db.collection("users").document(mAuth.getCurrentUser().getEmail()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            userType = documentSnapshot.getString("Type");
                        }
                    }
                });
    }
    public void AddPatient(View v){
        if(userType.equals("")){
            Toast.makeText(getApplicationContext(),"Something went wrong! check your connection.",Toast.LENGTH_SHORT).show();
        }
        else if(userType.equals("Doctor") || userType.equals("Chef nurse")){
            startActivity(new Intent(getApplicationContext(),AddPatients.class));
        }
        else {
            Toast.makeText(getApplicationContext(),"Access denied.",Toast.LENGTH_SHORT).show();
        }
    }
    public void ScanPatient(View v){
        startActivity(new Intent(getApplicationContext(),FaceRecognitionActivity.class));
    }
    public void signOut(View v){
        mAuth.signOut();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }

    public void loadName(View view) {
        startActivity(new Intent(getApplicationContext(),ViewPatientActivity.class));
    }
}
