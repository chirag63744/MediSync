package com.example.medisync;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Tracking_Details extends AppCompatActivity {
    Button b1,b2;
    String trustworthinessScore;
    private FirebaseFirestore firestore;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_details);
        final View mapFrame = findViewById(R.id.mapframe);
        final View bottomSheet = findViewById(R.id.sheet);

        b1 = findViewById(R.id.bookAmb);
        textView=findViewById(R.id.reviewtxt);

        b2=findViewById(R.id.review);

        firestore = FirebaseFirestore.getInstance();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String collectionName = "AmbulanceTrust";
//                String csvFilePath = "Ambulance_trustworthiness.csv";
//
//                uploadCsvToFirestore(collectionName, csvFilePath);
//
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Tracking_Details.this, Review.class);
               startActivity(i);

            }
        });
        DocumentReference docRef = firestore.collection("DriverTrust").document("101");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve the fields from the document
                        String field1 = document.getString("DriverID");
                        String field2 = document.getString("TrustworthinessScore");
                        Toast.makeText(Tracking_Details.this, field2, Toast.LENGTH_SHORT).show();

                        Log.d(TAG, "Field 1: " + field1);
                        Log.d(TAG, "Field 2: " + field2);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed w ith ", task.getException());
                }
            }
        });
        fetchDataFromFirestore("101");
        textView.setText(trustworthinessScore);
        // Get the BottomSheetBehavior from the FrameLayout
        //final BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // Calculate the screen height
        final int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Calculate the height for the first FrameLayout (75% of screen height)
        final int firstFrameHeight = (int) (screenHeight * 0.75);

        // Calculate the height for the second FrameLayout (25% of screen height)


        // Set the height for the first FrameLayout
        ViewGroup.LayoutParams mapFrameParams = mapFrame.getLayoutParams();
        mapFrameParams.height = firstFrameHeight;
        mapFrame.setLayoutParams(mapFrameParams);


        FragmentManager fragmentManager = getSupportFragmentManager();

        // Create a FragmentTransaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Add a fragment to the container (R.id.fragment_container) in your layout
        TrackingMap firstFragment = new TrackingMap();
        transaction.add(R.id.mapframe, firstFragment);

        // Commit the transaction
        transaction.commit();
        //View bottomSheet = findViewById(R.id.sheet);

// Get the BottomSheetBehavior from the FrameLayout
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        int peakHeightPixels = (int) (screenHeight * 0.29); // Replace with your desired value
        bottomSheetBehavior.setPeekHeight(peakHeightPixels);

        // Set the initial state of the BottomSheet to collapsed
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Override the back button behavior
        // You can start the Home activity or any other desired behavior
        Intent intent = new Intent(this, Profile.class);
       // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // This is important to finish the current activity
    }
    private void fetchDataFromFirestore(String documentId) {
        firestore.collection("DriverTrust") // Replace with your actual collection name
                .document(documentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // DocumentSnapshot contains the data from the document
                                String driverID = document.getString("DriverID");
                                trustworthinessScore = document.getString("TrustworthinessScore");
                                textView.setText(trustworthinessScore);

                                // Now you can use driverID and trustworthinessScore as needed
                                // For example, you can display them in TextViews or log them
                                // Log.d("FirestoreData", "DriverID: " + driverID + ", TrustworthinessScore: " + trustworthinessScore);
                            } else {
                                Toast.makeText(Tracking_Details.this, "bhiiii", Toast.LENGTH_SHORT).show();
                                // Document doesn't exist
                                // Handle accordingly
                            }
                        } else {
                            // Task failed with an exception
                            Exception exception = task.getException();
                            // Handle accordingly
                        }
                    }
                });
        //Toast.makeText(this, trustworthinessScore, Toast.LENGTH_SHORT).show();
    }

    private void uploadCsvToFirestore(String collectionName, String csvFileName) {
        try {
            // Read CSV file from assets
            InputStream inputStream = getResources().getAssets().open(csvFileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Assuming the first row contains headers
            String[] headers = reader.readLine().split(",");

            // Process each row and add to Firestore
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                // Use the first column (DriverID) as the document ID
                String documentId = values[0];

                // Create a Map with headers as keys and values as data
                Map<String, Object> data = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    data.put(headers[i], values[i]);
                }

                // Add the data to Firestore with the specified document ID
                firestore.collection(collectionName)
                        .document(documentId)
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Data added successfully
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                            }
                        });

            // Close the BufferedReader
        }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}