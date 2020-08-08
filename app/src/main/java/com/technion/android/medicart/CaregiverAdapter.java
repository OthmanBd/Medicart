package com.technion.android.medicart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class CaregiverAdapter extends FirestoreRecyclerAdapter<Caregiver, CaregiverAdapter.CaregiverHolder> {

    public CaregiverAdapter(@NonNull FirestoreRecyclerOptions<Caregiver> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CaregiverHolder holder, int position, @NonNull Caregiver model) {
        holder.Date.setText(model.getDate());
        holder.Time.setText(model.getTime());
        holder.Email.setText(model.getEmail());
        holder.Name.setText(model.getName());
    }

    @NonNull
    @Override
    public CaregiverHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.caregiver_card,parent,false);
        return new CaregiverHolder(v);
    }

    class CaregiverHolder extends RecyclerView.ViewHolder{
        TextView Date;
        TextView Time;
        TextView Name;
        TextView Email;
        public CaregiverHolder(View view){
            super(view);
            Date = view.findViewById(R.id.caregiver_date_textview);
            Time = view.findViewById(R.id.caregiver_time_textview);
            Name = view.findViewById(R.id.caregiver_name_textview);
            Email = view.findViewById(R.id.caregiver_email_textview);
        }
    }
}
