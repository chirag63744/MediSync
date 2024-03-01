package com.example.medisync;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class Upload_Reports extends AppCompatActivity {
    private static final int PICK_PDF_REQUEST = 1;

    private Spinner spinnerAllergies;
    private Uri pdfUri;
    String email;
    private TextView OperationLabel, OperationReportLabel, FileNameLabel;
    ImageView upload;
    private EditText OperationEditText;
    private loading loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_reports);

        spinnerAllergies = findViewById(R.id.spinnerAllergies);
        OperationLabel = findViewById(R.id.operationText);
        OperationEditText = findViewById(R.id.operationEdit);
        OperationReportLabel = findViewById(R.id.OperationName);
        FileNameLabel = findViewById(R.id.OperationReport);
        upload = findViewById(R.id.imageView5);

        loadingDialog = new loading(this);

        // Get the array of options from resources
        String[] spinnerOptions = getResources().getStringArray(R.array.spinner_options);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerOptions);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerAllergies.setAdapter(adapter);

        // Set up the OnItemSelectedListener for the spinner
        // Set up the OnItemSelectedListener for the spinner
        spinnerAllergies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Check if the selected option is "Yes"
                if (spinnerOptions[position].equals("Yes")) {
                    // If "Yes" is selected, show the additional views
                    OperationLabel.setVisibility(View.VISIBLE);
                    OperationEditText.setVisibility(View.VISIBLE);
                    OperationReportLabel.setVisibility(View.VISIBLE);
                    FileNameLabel.setVisibility(View.VISIBLE);
                    upload.setVisibility(View.VISIBLE);

                    upload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectPDF();
                        }
                    });
                } else {
                    // If "No" is selected or any other option, hide the additional views
                    OperationLabel.setVisibility(View.GONE);
                    OperationEditText.setVisibility(View.GONE);
                    OperationReportLabel.setVisibility(View.GONE);
                    FileNameLabel.setVisibility(View.GONE);
                    upload.setVisibility(View.GONE);

                    // Get operation details from the OperationEditText
                    String operationDetails = OperationEditText.getText().toString();

                    // Set operation details to "0" and pdfLink to null
                    String pdfLink = null;

                    // Save the data to the Realtime Database
                    savePdfLinkToDatabase(email, pdfLink, operationDetails);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }

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

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pdfUri = data.getData();
            // File is selected, now you can proceed with the file upload
            uploadReports();
        }
    }

    // Method to handle file upload
    private void uploadReports() {
        if (pdfUri != null) {
            loadingDialog.show();

            // Check if the user is authenticated
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                // Use the email variable instead of getting it again
                 email = currentUser.getEmail();

                // Reference to the Firebase Storage
                FirebaseStorage storage = FirebaseStorage.getInstance();

                // Create a folder in Firebase Storage with the user's email
                String userFolder = "users/" + email;
                StorageReference userStorageReference = storage.getReference().child(userFolder);
                String fileName = getFileNameFromUri(pdfUri);
                FileNameLabel.setText(fileName);

                // Upload the PDF file to the user's folder
                StorageReference pdfReference = userStorageReference.child(System.currentTimeMillis() + ".pdf");
                pdfReference.putFile(pdfUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // File upload success
                            Toast.makeText(this, "File Upload Successful", Toast.LENGTH_SHORT).show();

                            // Get the download URL of the uploaded file
                            pdfReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                // Get operation details from the OperationEditText
                                String operationDetails = OperationEditText.getText().toString();

                                // Save the PDF link and operation details to the Realtime Database
                                savePdfLinkToDatabase(email, uri.toString(), operationDetails);

                                // Dismiss the loading dialog
                                loadingDialog.dismiss();
                            }).addOnFailureListener(e -> {
                                loadingDialog.dismiss();
                                Toast.makeText(this, "Failed to retrieve PDF link", Toast.LENGTH_SHORT).show();
                            });

                        })
                        .addOnFailureListener(e -> {
                            loadingDialog.dismiss();
                            // File upload failure
                            Toast.makeText(this, "File Upload Failed", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // User is not authenticated, prompt them to log in
                Toast.makeText(this, "Please log in to upload PDF files", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        } else {
            Toast.makeText(this, "Select a PDF file first", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePdfLinkToDatabase(String email, String pdfLink, String operationDetails) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Create a reference to the users node in the database
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

            // Create a HashMap to store multiple values
            HashMap<String, Object> data = new HashMap<>();
            data.put("pdfLink", pdfLink);
            data.put("operationDetails", operationDetails);

            // Update the data in the database where email matches
            usersRef.child(userId).updateChildren(data)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Data saved to Database", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save data to Database", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        String[] filePathColumn = {MediaStore.Images.Media.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            fileName = cursor.getString(columnIndex);
            cursor.close();
        }
        return fileName;
    }
}
