package com.example.assignment2;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;

public class GeocodeAdder extends AppCompatActivity {

    private EditText addressInput, latitudeInput, longitudeInput;
    private TextView outputTextView;
    private DBHandler dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geocode);

        // Top bar colour setter
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue));

        // Initialize UI elements
        addressInput = findViewById(R.id.addressInput);
        latitudeInput = findViewById(R.id.latitudeInput);
        longitudeInput = findViewById(R.id.longitudeInput);
        outputTextView = findViewById(R.id.outputTextView);
        dbHelper = new DBHandler(this);

        Button findCoordinatesButton = findViewById(R.id.findCoordinatesButton);
        Button findAddressButton = findViewById(R.id.findAddressButton);
        Button saveToDatabaseButton = findViewById(R.id.saveToDatabaseButton);

        // onClick listener methods for the buttons
        findCoordinatesButton.setOnClickListener(v -> findCoordinates());
        findAddressButton.setOnClickListener(v -> findAddress());
        saveToDatabaseButton.setOnClickListener(v -> saveToDatabase());
    }

    // Method to find the longitude and latitude of a provided address
    private void findCoordinates() {
        String address = addressInput.getText().toString();
        if (!address.isEmpty()) {
            Location location = getLocationFromAddress(this, address);
            if (location != null) {
                outputTextView.setText("Longitude: " + location.getLongitude() +"\nLatitude: " + location.getLatitude());
            } else {
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to find the address of a given longitude and latitude
    private void findAddress() {
        String latStr = latitudeInput.getText().toString();
        String lonStr = longitudeInput.getText().toString();

        if (!latStr.isEmpty() && !lonStr.isEmpty()) {
            try {
                double latitude = Double.parseDouble(latStr);
                double longitude = Double.parseDouble(lonStr);
                Location location = getAddressFromCoordinates(this, latitude, longitude);

                if (location != null) {
                    outputTextView.setText("Address: " + location.getAddress());
                } else {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid latitude or longitude format", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter both latitude and longitude", Toast.LENGTH_SHORT).show();
        }
    }

    // Save to database method accepts either longitude and latitude or an address
    private void saveToDatabase() {
        String address = addressInput.getText().toString();
        String latStr = latitudeInput.getText().toString();
        String lonStr = longitudeInput.getText().toString();

        double latitude = 0;
        double longitude = 0;

        // Method for address entry
        if (!address.isEmpty()) {
            Location location = getLocationFromAddress(this, address);
            if (location != null) {
                latitude = location.getLatitude(); // Get the coordinates from the address
                longitude = location.getLongitude();
            } else {
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        // Method for longitude and latitude entry
        else if (!latStr.isEmpty() && !lonStr.isEmpty()) {
            try {
                latitude = Double.parseDouble(latStr);
                longitude = Double.parseDouble(lonStr);
                Location location = getAddressFromCoordinates(this, latitude, longitude);
                if (location != null) {
                    address = location.getAddress(); // Get the address from the coordinates
                } else {
                    Toast.makeText(this, "Address not found for provided coordinates", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid latitude or longitude format", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this, "Please enter either an address or both latitude and longitude", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if location is already in the database
        if (!dbHelper.locationExists(address, latitude, longitude)) {
            // Save to database
            long result = dbHelper.addLoc(address, latitude, longitude);
            if (result != -1) {
                Toast.makeText(this, "Location saved to database", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GeocodeAdder.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Location couldn't be saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Location already in database", Toast.LENGTH_SHORT).show();
        }
    }

    // Geocoding function to get the longitude and latitude from the address
    public Location getLocationFromAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new Location(address, location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Geocoding function to get the address from the longitude and latitude
    public Location getAddressFromCoordinates(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                return new Location(location.getAddressLine(0), latitude, longitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}