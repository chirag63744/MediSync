package com.example.medisync;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class Upload_Reports extends AppCompatActivity {
    private static final int PICK_PDF_REQUEST = 1;

    private Spinner spinnerAllergies;
    private TextView OperationLabel,OperationReportLabel,FileNameLabel;
    ImageView upload;
    private EditText OperationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_reports);

        spinnerAllergies = findViewById(R.id.spinnerAllergies);
        OperationLabel = findViewById(R.id.operationText);
        OperationEditText = findViewById(R.id.operationEdit);
        OperationReportLabel=findViewById(R.id.OperationName);
        FileNameLabel = findViewById(R.id.OperationReport);
        upload=findViewById(R.id.imageView5);


        // Get the array of options from resources
        String[] spinnerOptions = getResources().getStringArray(R.array.spinner_options);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerOptions);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerAllergies.setAdapter(adapter);

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
}
