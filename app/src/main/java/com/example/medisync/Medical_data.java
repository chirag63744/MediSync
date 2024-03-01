package com.example.medisync;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Medical_data extends AppCompatActivity {
    Button b1;
    EditText heightEditText, weightEditText, bloodGroupEditText;
    Spinner allergiesSpinner;
    private loading loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_data);

        b1 = findViewById(R.id.Nextbtn);
        heightEditText = findViewById(R.id.editTextHeight);
        weightEditText = findViewById(R.id.editTextWeight);
        bloodGroupEditText = findViewById(R.id.editTextBloodGroup);
        allergiesSpinner = findViewById(R.id.spinnerAllergies);
        loadingDialog = new loading(this);
        loadingDialog.show();


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMedicalData();
                Intent i = new Intent(Medical_data.this, Upload_Reports.class);
                startActivity(i);
            }
        });

        // Fetch and display existing medical data
        fetchExistingMedicalData();
    }

    private void saveMedicalData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Create a Map to store only the fields you want to update
                        Map<String, Object> updates = new HashMap<>();

                        // Update the values only if the corresponding EditText is not empty
                        String newHeight = heightEditText.getText().toString();
                        String newWeight = weightEditText.getText().toString();
                        String newBloodGroup = bloodGroupEditText.getText().toString();
                        String newAllergies = allergiesSpinner.getSelectedItem().toString();

                        // Check if EditText fields are not empty before updating and add to updates Map
                        if (!newHeight.isEmpty()) {
                            updates.put("height", newHeight);
                        }
                        if (!newWeight.isEmpty()) {
                            updates.put("weight", newWeight);
                        }
                        if (!newBloodGroup.isEmpty()) {
                            updates.put("bloodGroup", newBloodGroup);
                        }
                        // Spinner is a little different, we don't update it if it's not selected
                        if (!newAllergies.isEmpty()) {
                            updates.put("allergies", newAllergies);
                        }

                        // Update the specified fields in the database
                        databaseReference.updateChildren(updates);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }





    private void fetchExistingMedicalData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        MedicalData_model existingMedicalData = dataSnapshot.getValue(MedicalData_model.class);

                        // Set existing medical data in their respective EditText fields
                        heightEditText.setText(existingMedicalData.getHeight());
                        weightEditText.setText(existingMedicalData.getWeight());
                        bloodGroupEditText.setText(existingMedicalData.getBloodGroup());
                        int spinnerPosition = findSpinnerPosition(existingMedicalData.getAllergies());
                        allergiesSpinner.setSelection(spinnerPosition);
                        loadingDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    private int findSpinnerPosition(String valueToFind) {
        // Get the array of options from resources
        String[] spinnerOptions = getResources().getStringArray(R.array.spinner_options);

        for (int i = 0; i < spinnerOptions.length; i++) {
            if (spinnerOptions[i].equals(valueToFind)) {
                return i;
            }
        }

        return 0; // Default position if not found
    }
}
