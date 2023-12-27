package com.example.medisync;

import static android.content.Intent.getIntent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NameDisplay#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NameDisplay extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView textView;
    String email;
    private loading loadingDialog;

    public NameDisplay() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NameDisplay.
     */
    // TODO: Rename and change types and number of parameters
    public static NameDisplay newInstance(String param1, String param2) {
        NameDisplay fragment = new NameDisplay();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_name_display, container, false);
        textView=root.findViewById(R.id.textView4);
        email = getArguments().getString("EMAIL");
        loadingDialog = new loading(getContext());
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            loadingDialog.show();

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
                        textView.setText("Welcome "+name);
                        loadingDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    loadingDialog.dismiss();
                    // Handle an error while fetching data
                    // For simplicity, you can add some logging or display an error message
                }
            });
        }
        FragmentManager fragmentManager = getChildFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("EMAIL", email);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        HomeFragment firstFragment = new HomeFragment();
        firstFragment.setArguments(bundle);
        transaction.add(R.id.homefromName, firstFragment);
        transaction.commit();
        // Inflate the layout for this fragment
        return root;

        // Create or replace HomeFragment and set email as an argument

    }
}