package com.example.assignment2;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    private List<Location> locationList;
    private List<Location> locationListFull;

    public LocationAdapter(List<Location> locationList) {
        this.locationList = locationList;
        this.locationListFull = new ArrayList<>(locationList);
    }

    public void updateData(List<Location> newLocationList) {
        locationList.clear();
        locationList.addAll(newLocationList);
        locationListFull.clear();
        locationListFull.addAll(newLocationList);
        notifyDataSetChanged();
    }

    // Creates a view object that is referencable by main to display the data within the database
    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_object, parent, false);
        return new LocationViewHolder(view);
    }

    // Bind data to into each item in the list view
    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {
        Location location = locationList.get(position);
        holder.locationName.setText(location.getAddress());
        holder.locationLon.setText(String.format("Longitude: %.5f", location.getLongitude()));
        holder.locationLat.setText(String.format("Latitude: %.5f", location.getLatitude()));

        // Set an onClick method for each respective item
        holder.itemView.setOnClickListener(v -> {
            // Create an Intent to start UpdateGeocode activity
            Intent intent = new Intent(holder.itemView.getContext(), UpdateGeocode.class);

            // Pass the selected location details to UpdateGeocode
            intent.putExtra("address", location.getAddress());
            intent.putExtra("longitude", location.getLongitude());
            intent.putExtra("latitude", location.getLatitude());

            // Start the UpdateGeocode activity
            holder.itemView.getContext().startActivity(intent);
        });
    }


    // Return the size of the database
    @Override
    public int getItemCount() {
        return locationList.size();
    }

    // Filter the database results to only those that match the string
    public void filter(String text) {
        locationList.clear();
        if (text.isEmpty()) {
            locationList.addAll(locationListFull);
        } else {
            for (Location location : locationListFull) {
                if (location.getAddress().toLowerCase().contains(text.toLowerCase())) {
                    locationList.add(location);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Setting the LocationViewHolder object to be called above
    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        public TextView locationName;
        public TextView locationLat;
        public TextView locationLon;

        public LocationViewHolder(View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.location_name);
            locationLon = itemView.findViewById(R.id.location_lon);
            locationLat = itemView.findViewById(R.id.location_lat);
        }
    }
}