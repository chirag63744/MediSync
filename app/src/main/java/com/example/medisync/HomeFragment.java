package com.example.medisync;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.medisync.BookAmbulance;
import com.example.medisync.HomeFragment_2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {
    Button showAmbulance;

    String email;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        showAmbulance = root.findViewById(R.id.bookAmbulance);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
             email = getArguments().getString("EMAIL");

            // Query to get the user's name based on the email
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            Query query = usersRef.orderByChild("email").equalTo(email);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Assuming your User class has a "name" field
                        String name = userSnapshot.child("name").getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle an error while fetching data
                    // For simplicity, you can add some logging or display an error message
                }
            });
        }

        showAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), BookAmbulance.class);
                startActivity(i);
            }
        });

        FragmentManager fragmentManager = getChildFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("EMAIL", email);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        HomeFragment_2 firstFragment = new HomeFragment_2();
        firstFragment.setArguments(bundle);
        transaction.add(R.id.fragment2, firstFragment);
        transaction.commit();

        return root;
    }
}
