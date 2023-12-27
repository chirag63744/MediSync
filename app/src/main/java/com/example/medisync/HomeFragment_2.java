package com.example.medisync;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class HomeFragment_2 extends Fragment {

    private static final int PICK_PDF_REQUEST = 1;
    private Uri pdfUri;
    ImageView upload;
    private loading loadingDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_2, container, false);

        // Your existing code for fragment transaction
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        HomeFragment3 firstFragment = new HomeFragment3();
        transaction.add(R.id.fragment2, firstFragment);
        transaction.commit();

        // Get references to UI elements
        CardView uploadReportsCardView = view.findViewById(R.id.cardView2);
        upload = view.findViewById(R.id.Upload_Image);
        loadingDialog = new loading(getContext());

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDF();
            }
        });

        // Set OnClickListener for the uploadReportsCardView
        uploadReportsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call a method to handle file upload
                // selectPDF();
            }
        });

        return view;
    }

    // Method to handle file selection
    private void selectPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_PDF_REQUEST);
    }

    // Handle the result of file selection
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            pdfUri = data.getData();
            // File is selected, now you can proceed with the file upload
            uploadReports();
        }
    }

    // Method to handle file upload
    private void uploadReports() {
        if (pdfUri != null) {
            loadingDialog.show();
            String email = getArguments().getString("EMAIL");

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference().child("pdfs/" + System.currentTimeMillis() + ".pdf");

            storageReference.putFile(pdfUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // File upload success
                        Toast.makeText(requireContext(), "File Upload Successful", Toast.LENGTH_SHORT).show();

                        // Get the download URL of the uploaded file
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Save the PDF link to the Realtime Database
                            savePdfLinkToDatabase(email, uri.toString());

                            // Dismiss the loading dialog
                            loadingDialog.dismiss();
                        }).addOnFailureListener(e -> {
                            loadingDialog.dismiss();
                            Toast.makeText(requireContext(), "Failed to retrieve PDF link", Toast.LENGTH_SHORT).show();
                        });

                    })
                    .addOnFailureListener(e -> {
                        loadingDialog.dismiss();
                        // File upload failure
                        Toast.makeText(requireContext(), "File Upload Failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(requireContext(), "Select a PDF file first", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to save the PDF link to the Realtime Database
    private void savePdfLinkToDatabase(String email, String pdfLink) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Create a reference to the users node in the database
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

            // Update the PDF link in the database where email matches
            usersRef.child(userId).child("pdfLink").setValue(pdfLink)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "PDF Link saved to Database", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to save PDF Link to Database", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
