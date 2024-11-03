package com.example.assignment2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateGeocode extends AppCompatActivity {

    private EditText addressInput;
    private EditText latitudeInput;
    private EditText longitudeInput;
    private Button updateButton;
    private Button deleteButton;

    private DBHandler dbHelper;
    private String originalAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);

        // Top bar colour setter
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        addressInput = findViewById(R.id.addressInput);
        latitudeInput = findViewById(R.id.latitudeInput);
        longitudeInput = findViewById(R.id.longitudeInput);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);

        dbHelper = new DBHandler(this);

        Intent intent = getIntent();
        double latitude = 0;
        double longitude = 0;
        if (intent != null) {
            // Retrieve the passed data from the intent: which address was clicked on in main
            originalAddress = getIntent().getStringExtra("address");
            latitude = getIntent().getDoubleExtra("latitude", 0);
            longitude = getIntent().getDoubleExtra("longitude", 0);
        }

        // Set the data to the EditText fields
        addressInput.setText(originalAddress);
        latitudeInput.setText(String.valueOf(latitude));
        longitudeInput.setText(String.valueOf(longitude));

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocation();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLocation();
            }
        });
    }

    // Sets the location or longitude or latitude to whatever the user desires without altering the ID num
    private void updateLocation() {
        String newAddress = addressInput.getText().toString();
        double newLatitude = Double.parseDouble(latitudeInput.getText().toString());
        double newLongitude = Double.parseDouble(longitudeInput.getText().toString());

        // Update the database with the altered data
        if (dbHelper.updateLoc(originalAddress, newAddress, newLatitude, newLongitude)) {
            Toast.makeText(this, "Location updated successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity and return to the previous one
        } else {
            Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
        }
    }

    // Deletes the selected item
    private void deleteLocation(){
        if (dbHelper.delLoc(originalAddress)) {
            Toast.makeText(this, "Location deleted successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity and return to the previous one
        } else {
            Toast.makeText(this, "Deletion failed", Toast.LENGTH_SHORT).show();
        }
    }
}