package com.example.medisync;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class adapter_rec extends FirestoreRecyclerAdapter<AmbFire, adapter_rec.personsViewholder> {

    public adapter_rec(@NonNull FirestoreRecyclerOptions<AmbFire> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull personsViewholder holder, int position, @NonNull AmbFire model) {
        holder.bindData(model);
    }

    @NonNull
    @Override
    public personsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bottomlist_cardview, parent, false);
        personsViewholder viewHolder = new personsViewholder(view);
        viewHolder.setAdapter(this); // Set the adapter for the viewHolder
        return viewHolder;
    }

    static class personsViewholder extends RecyclerView.ViewHolder {
        TextView Vehicle_no, Hospital_Name;
        CheckBox checkbox;
        adapter_rec adapter;

        public personsViewholder(@NonNull View itemView) {
            super(itemView);
            Vehicle_no = itemView.findViewById(R.id.vehicle_no);
            Hospital_Name = itemView.findViewById(R.id.hospital_name);
            checkbox = itemView.findViewById(R.id.myCheckBox); // Replace with your checkbox id

            checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Update the isChecked field in your data model
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && adapter != null) {
                    AmbFire item = adapter.getItem(position);
                    if (item != null) {
                        item.setChecked(isChecked);
                    }
                }
            });
        }

        public void setAdapter(adapter_rec adapter) {
            this.adapter = adapter;
        }

        public void bindData(AmbFire model) {
            Vehicle_no.setText(model.getVehicle_no());
            Hospital_Name.setText(model.getHospital_Name());
            checkbox.setChecked(model.isChecked());
        }
    }
}
