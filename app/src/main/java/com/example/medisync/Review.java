package com.example.medisync;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class Review extends AppCompatActivity {

    private RatingBar ratingBar1, ratingBar2, ratingBar3, ratingBar4, ratingBar5, ratingBar6;
    private Button submitButton;
    private FirebaseFirestore firestore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Initialize views
        ratingBar1 = findViewById(R.id.ratingBar1);
        ratingBar2 = findViewById(R.id.ratingBar2);
        ratingBar3 = findViewById(R.id.ratingBar3);
        ratingBar4 = findViewById(R.id.ratingBar4);
        ratingBar5 = findViewById(R.id.ratingBar5);
        ratingBar6 = findViewById(R.id.ratingBar6);
        submitButton = findViewById(R.id.button);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Set click listener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRatings();
            }
        });
    }

    private void submitRatings() {
        // Get ratings from RatingBars
        float driverBehaviorRating = ratingBar1.getRating();
        float staffBehaviorRating = ratingBar2.getRating();
        float abilityToTrackRating = ratingBar3.getRating();
        float ambulanceFacilitiesRating = ratingBar4.getRating();
        float easeOfUseRating = ratingBar5.getRating();
        float overallAppRating = ratingBar6.getRating();

        // Create a map to store ratings
        Map<String, Float> ratings = new HashMap<>();
        ratings.put("driverBehavior", driverBehaviorRating);
        ratings.put("staffBehavior", staffBehaviorRating);
        ratings.put("abilityToTrack", abilityToTrackRating);
        ratings.put("ambulanceFacilities", ambulanceFacilitiesRating);
        ratings.put("easeOfUse", easeOfUseRating);
        ratings.put("overallAppRating", overallAppRating);

        // Add the ratings to Firestore under the document with the ID "user1"
        firestore.collection("ratings")
                .document("user1")
                .set(ratings)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Reviews Submitted", Toast.LENGTH_SHORT).show();
                    // Handle success
                    // You can add a toast or other UI feedback here
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    // You can add a toast or other UI feedback here
                });
    }
}
