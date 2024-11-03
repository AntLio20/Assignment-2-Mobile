package com.example.assignment2;

public class Location {
    private String address;
    private double latitude;
    private double longitude;

    // Specifying a location object with attributes: address, latitude and longitude
    public Location(String address, double latitude, double longitude) {
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public String getAddress() {
        return address;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
}