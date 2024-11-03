package com.example.assignment2;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private LocationAdapter locationAdapter;
    private DBHandler dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set top bar colour
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.blue));

        dbHelper = new DBHandler(this);

        RecyclerView recyclerView = findViewById(R.id.location_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the list of locations from the .txt file and calculate their longitude and latitudes using geocoding
        List<Location> textLocations = new ArrayList<>();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            InputStream is = getAssets().open("locations.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                String address = line.trim();
                // Check if location is already in the database
                if (!dbHelper.locationNameExists(address)) {
                    // Try to geocode the address
                    double latitude = 0;
                    double longitude = 0;
                    try {
                        List<Address> addressList = geocoder.getFromLocationName(address, 1);
                        if (addressList != null && !addressList.isEmpty()) {
                            Address location = addressList.get(0);
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Only add to the database if geocoding was successful
                    if (latitude != 0 && longitude != 0) {
                        long result = dbHelper.addLoc(address, latitude, longitude);
                        if (result != -1) {
                            textLocations.add(new Location(address, latitude, longitude));
                        }
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Retrieve and output the full list of locations from the database using recyclerView
        List<Location> locations = dbHelper.getAllLocations();
        locationAdapter = new LocationAdapter(locations);
        recyclerView.setAdapter(locationAdapter);

        // Search bar string filtering for searching entries passed to LocationAdapter
        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                locationAdapter.filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Retrieve an updated list of locations from the database when returning to the page
        List<Location> updatedLocations = dbHelper.getAllLocations();
        // Refresh RecyclerView with the updated list of entries
        locationAdapter.updateData(updatedLocations);
    }

    // Button onClick method
    public void goToGeocodePage(View view) {
        Intent intent = new Intent(MainActivity.this, GeocodeAdder.class);
        startActivity(intent);
    }
}