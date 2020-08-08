package com.technion.android.medicart;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CaregiversActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private CaregiverAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregivers);
        if(getIntent().getStringExtra("ID") != null) {
            setUpRecyclerView(getIntent().getStringExtra("ID"));
        }
    }
    private void setUpRecyclerView(String ID){
        Query query = db.collection("patients").document(ID).collection("CaregiversLog")
                .orderBy("Date", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Caregiver> options = new FirestoreRecyclerOptions.Builder<Caregiver>()
                .setQuery(query,Caregiver.class)
                .build();
        adapter = new CaregiverAdapter(options);
        recyclerView = findViewById(R.id.caregivers_recyclerview);
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
