package com.example.medisync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BookAmbulance extends AppCompatActivity implements MapsFragment.MarkerClickListener {
    Button bookAmb;
    RecyclerView recyclerView;
   // CardView cardView1,cardView2;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private adapter_rec Adapter;

    //CheckBox c1,c2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookambulance);
        final View mapFrame = findViewById(R.id.mapframe);
        final View bottomSheet = findViewById(R.id.sheet);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirestoreRecyclerOptions<AmbFire> options = new FirestoreRecyclerOptions.Builder<AmbFire>()
                .setQuery(FirebaseFirestore.getInstance().collection("Ambulance"),AmbFire.class)
                .build();
        Adapter = new adapter_rec(options);
        recyclerView.setAdapter(Adapter);
       // cardView1=findViewById(R.id.card1);
       // c1=findViewById(R.id.myCheckBox);;
       // cardView2=findViewById(R.id.card2);
//        cardView1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                c1.setChecked(true);
//                c2.setChecked(false);
//
//            }
//        });
//        cardView2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                c2.setChecked(true);
//                c1.setChecked(false);
//            }
//        });

        // Get the BottomSheetBehavior from the FrameLayout
        //final BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        // Calculate the screen height
        final int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Calculate the height for the first FrameLayout (75% of screen height)
        final int firstFrameHeight = (int) (screenHeight * 0.75);
        bookAmb=findViewById(R.id.bookAmb);
        bookAmb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isAnyChecked = false;
                GlobalData globalData = GlobalData.getInstance();

                for (int i = 0; i < Adapter.getItemCount(); i++) {
                    AmbFire item = Adapter.getItem(i);
                    if (item != null && item.isChecked()) {
                        isAnyChecked = true;
                        globalData.setSelectedHospital(item.getHospital_Name());


                        break;
                    }
                }

                if (isAnyChecked) {
                    // Redirect to the Tracking_Details activity or perform desired action
                    Intent i = new Intent(BookAmbulance.this, Tracking_Details.class);
                    startActivity(i);
                } else {
                    // Display a toast or handle the case where no checkbox is checked
                    Toast.makeText(BookAmbulance.this, "Please select a checkbox", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });


        // Calculate the height for the second FrameLayout (25% of screen height)


        // Set the height for the first FrameLayout
        ViewGroup.LayoutParams mapFrameParams = mapFrame.getLayoutParams();
        mapFrameParams.height = firstFrameHeight;
        mapFrame.setLayoutParams(mapFrameParams);

//        GlobalLists globalLists = GlobalLists.getInstance();
//        double latitude = globalLists.getLatitudeList().get(0);

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Create a FragmentTransaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Add a fragment to the container (R.id.fragment_container) in your layout
        MapsFragment firstFragment = new MapsFragment();
        firstFragment.setMarkerClickListener(this);
        transaction.add(R.id.mapframe, firstFragment);

        // Commit the transaction
        transaction.commit();
        //View bottomSheet = findViewById(R.id.sheet);

// Get the BottomSheetBehavior from the FrameLayout
       bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        int peakHeightPixels = (int) (screenHeight * 0.29); // Replace with your desired value
        bottomSheetBehavior.setPeekHeight(peakHeightPixels);

        // Set the initial state of the BottomSheet to collapsed
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }

    public void onMarkerClick(Marker marker) {
        // Handle marker click and fetch marker details
        LatLng markerPosition = marker.getPosition();
        String markerTitle = marker.getTitle();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        // Now you can use the marker details as needed
        // For example, pass them to another method or show in the bottom sheet
        showMarkerDetails(markerPosition, markerTitle);
    }

    private void showMarkerDetails(LatLng position, String title) {
        // Implement how you want to display or use marker details in your bottom sheet
        // For example, update UI elements with the details
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onStart() {
        super.onStart();
        Adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Adapter.stopListening();
    }



    // You can define methods to handle fragment transactions based on user interactions

}

