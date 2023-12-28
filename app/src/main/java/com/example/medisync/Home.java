package com.example.medisync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.medisync.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {

    ActivityHomeBinding binding;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        final View Frame1 = findViewById(R.id.FrameScroll);
        email = getIntent().getStringExtra("EMAIL");

        final int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Calculate the height for the first FrameLayout (75% of screen height)
        final int firstFrameHeight = (int) (screenHeight * 0.95);
        ViewGroup.LayoutParams FrameParams = Frame1.getLayoutParams();
        FrameParams.height = firstFrameHeight;
        Frame1.setLayoutParams(FrameParams);

        if (savedInstanceState == null) {
            // Create or replace HomeFragment and set email as an argument
            NameDisplay nameDisplay = new NameDisplay();
            Bundle bundle = new Bundle();
            bundle.putString("EMAIL", email);
            nameDisplay.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.homeFrame, nameDisplay)
                    .commit();
        }

        try {
            NameDisplay nameDisplay1= new NameDisplay();
            Bundle bundle1 = new Bundle();
            bundle1.putString("EMAIL", email);
            nameDisplay1.setArguments(bundle1);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.homeFrame, nameDisplay1)
                    .commit();

            binding.btm.setOnItemSelectedListener(item -> {
                switch (item.getItemId()) {
                    case R.id.home:
                        NameDisplay nameDisplay = new NameDisplay();
                        Bundle bundle = new Bundle();
                        bundle.putString("EMAIL", email);
                        nameDisplay.setArguments(bundle);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.homeFrame, nameDisplay)
                                .commit();
                        break;
                    case R.id.Account:
                        replaceFragment(new Account());
                        break;
                }
                return true;
            });
        } catch (Exception e) {
            Toast.makeText(Home.this, "Fragment Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.homeFrame, fragment)
                .addToBackStack(null)
                .commit();
    }
}
