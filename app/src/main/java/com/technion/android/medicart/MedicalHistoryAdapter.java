package com.technion.android.medicart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MedicalHistoryAdapter extends FirestoreRecyclerAdapter<MedicalHistoryClass, MedicalHistoryAdapter.MedicalHistoryHolder> {

    public MedicalHistoryAdapter(@NonNull FirestoreRecyclerOptions options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MedicalHistoryHolder holder, int position, @NonNull MedicalHistoryClass model) {
        holder.Allergies.setText("Allergies: " + model.getAllergies());
        holder.Diagnosis.setText("Diagnosis: " + model.getDiagnosis());
        holder.AddedBy.setText("Added by: " + model.getAddedBy());
        holder.Date.setText(model.getDate());
        holder.Time.setText(model.getTime());
    }

    @NonNull
    @Override
    public MedicalHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medical_history_card,parent,false);
        return new MedicalHistoryHolder(v);
    }

    class MedicalHistoryHolder extends RecyclerView.ViewHolder{
        TextView Date;
        TextView Time;
        TextView AddedBy;
        TextView Diagnosis;
        TextView Allergies;
        public MedicalHistoryHolder(View view){
            super(view);
            Date = view.findViewById(R.id.medical_date);
            Time = view.findViewById(R.id.medical_time);
            AddedBy = view.findViewById(R.id.medical_addedby);
            Diagnosis = view.findViewById(R.id.medical_diagnosis);
            Allergies = view.findViewById(R.id.medical_allergies);
        }
    }
}
