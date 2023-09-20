package com.example.medisync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Create a FragmentTransaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Add a fragment to the container (R.id.fragment_container) in your layout
        HomeFragment firstFragment = new HomeFragment();
        transaction.add(R.id.homeFrame, firstFragment);

        // Commit the transaction
        transaction.commit();
    }
}