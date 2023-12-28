package com.example.medisync;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private TextView emailTextView;
    EditText nameEditText, phoneEditText;
    Button edit, save;
    String originalName, originalPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Retrieve the email from the Intent
        String email = getIntent().getStringExtra("EMAIL");

        // Initialize TextViews and EditTexts to display and edit user information
        nameEditText = findViewById(R.id.nameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        phoneEditText = findViewById(R.id.phoneTextView);
        edit = findViewById(R.id.Edit);
        save = findViewById(R.id.save);

        // Set the EditTexts as non-editable initially
        nameEditText.setEnabled(false);
        phoneEditText.setEnabled(false);

        // Retrieve user information from Firebase Realtime Database
        retrieveUserData(email);

        // Set OnClickListener for the Edit button
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enable EditTexts for editing
                nameEditText.setEnabled(true);
                phoneEditText.setEnabled(true);

                // Save the original values for comparison later
                originalName = nameEditText.getText().toString();
                originalPhone = phoneEditText.getText().toString();

                // Toggle button visibility
                edit.setVisibility(View.GONE);
                save.setVisibility(View.VISIBLE);
            }
        });

        // Set OnClickListener for the Save button
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update Firebase with the edited values
                updateFirebaseData(email);

                // Disable EditTexts after saving changes

            }
        });
    }

    private void retrieveUserData(String email) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Create a reference to the users node in the database
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

            // Query to retrieve user data based on email
            usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Extract user information
                        String name = userSnapshot.child("name").getValue(String.class);
                        String email = userSnapshot.child("email").getValue(String.class);
                        String phone = userSnapshot.child("phone").getValue(String.class);

                        // Display user information in EditTexts
                        nameEditText.setText("" + (name != null ? name : "N/A"));
                        emailTextView.setText("" + (email != null ? email : "N/A"));
                        phoneEditText.setText("" + (phone != null ? phone : "N/A"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }
    }

    private void updateFirebaseData(String email) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Create a reference to the users node in the database
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

            // Query to retrieve user data based on email
            usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Update user information in Firebase
                        userSnapshot.getRef().child("name").setValue(nameEditText.getText().toString());
                        userSnapshot.getRef().child("phone").setValue(phoneEditText.getText().toString());
                        nameEditText.setEnabled(false);
                        phoneEditText.setEnabled(false);
                        Toast.makeText(Profile.this, "Values Updated", Toast.LENGTH_SHORT).show();

                        // Toggle button visibility
                        save.setVisibility(View.GONE);
                        edit.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }
    }
}
