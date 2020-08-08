package com.technion.android.medicart;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MedicalActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private MedicalHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical);
        if(getIntent().getStringExtra("ID") != null) {
            setUpRecyclerView(getIntent().getStringExtra("ID"));
        }
    }
    private void setUpRecyclerView(String ID){
        Query query = db.collection("patients").document(ID).collection("Medical history")
                .orderBy("Date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<MedicalHistoryClass> options = new FirestoreRecyclerOptions.Builder<MedicalHistoryClass>()
                .setQuery(query,MedicalHistoryClass.class)
                .build();
        adapter = new MedicalHistoryAdapter(options);
        recyclerView = findViewById(R.id.medical_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
