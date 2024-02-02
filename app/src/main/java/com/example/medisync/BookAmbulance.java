package com.example.medisync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;

public class BookAmbulance extends AppCompatActivity implements MapsFragment.MarkerClickListener {
    Button bookAmb;
    CardView cardView1,cardView2;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    CheckBox c1,c2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookambulance);
        final View mapFrame = findViewById(R.id.mapframe);
        final View bottomSheet = findViewById(R.id.sheet);
        cardView1=findViewById(R.id.card1);
        c1=findViewById(R.id.myCheckBox);;
        c2=findViewById(R.id.myCheckBox1);
        cardView2=findViewById(R.id.card2);
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c1.setChecked(true);
                c2.setChecked(false);

            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c2.setChecked(true);
                c1.setChecked(false);
            }
        });

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
                if (c1.isChecked() || c2.isChecked()) {
                    // Only redirect if either checkbox is checked
                    Intent i = new Intent(BookAmbulance.this, Tracking_Details.class);
                    startActivity(i);
                } else {
                    // Display a toast or handle the case where no checkbox is checked
                    Toast.makeText(BookAmbulance.this, "Please select a checkbox", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Calculate the height for the second FrameLayout (25% of screen height)


        // Set the height for the first FrameLayout
        ViewGroup.LayoutParams mapFrameParams = mapFrame.getLayoutParams();
        mapFrameParams.height = firstFrameHeight;
        mapFrame.setLayoutParams(mapFrameParams);

//        GlobalLists globalLists = GlobalLists.getInstance();
//        double latitude = globalLists.getLatitudeList().get(0);
        GlobalLists globalLists = GlobalLists.getInstance();
        List<Double> latitudeList = globalLists.getLatitudeList();

        if (!latitudeList.isEmpty()) {
            double latitude = latitudeList.get(0);
            Toast.makeText(this, ""+latitude, Toast.LENGTH_SHORT).show();

            // Now you can use the latitude value
        } else {
            Toast.makeText(this, "khali", Toast.LENGTH_SHORT).show();
            // Handle the case when the latitude list is empty
        }

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
    public void onMarkerClick() {
        // Handle marker click, change the state of the bottom sheet to the expanded state
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }


    // You can define methods to handle fragment transactions based on user interactions

}

