package com.example.medisync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final View Frame1 = findViewById(R.id.FrameScroll);
        final View NavigationBar=findViewById(R.id.bottomNavigationView);



        final int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Calculate the height for the first FrameLayout (75% of screen height)
        final int firstFrameHeight = (int) (screenHeight * 0.95);
        ViewGroup.LayoutParams FrameParams = Frame1.getLayoutParams();
        FrameParams.height = firstFrameHeight;
        Frame1.setLayoutParams(FrameParams);





        String email = getIntent().getStringExtra("EMAIL");

        // Create or replace HomeFragment and set email as an argument
        NameDisplay nameDisplay = new NameDisplay();
        Bundle bundle = new Bundle();
        bundle.putString("EMAIL", email);
        nameDisplay.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.homeFrame, nameDisplay)
                .commit();
    }
}
