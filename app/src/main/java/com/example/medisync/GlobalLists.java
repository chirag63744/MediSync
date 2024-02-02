package com.example.medisync;

import java.util.ArrayList;
import java.util.List;

public class GlobalLists {
    private static GlobalLists instance;

    // Lists to store latitude, longitude, and AmbNo
    private List<Double> latitudeList = new ArrayList<>();
    private List<Double> longitudeList = new ArrayList<>();
    private List<String> ambNoList = new ArrayList<>();

    private GlobalLists() {
        // Private constructor to prevent instantiation
    }

    public static GlobalLists getInstance() {
        if (instance == null) {
            instance = new GlobalLists();
        }
        return instance;
    }

    public List<Double> getLatitudeList() {
        return latitudeList;
    }

    public List<Double> getLongitudeList() {
        return longitudeList;
    }

    public List<String> getAmbNoList() {
        return ambNoList;
    }

    public void clearLists() {
        latitudeList.clear();
        longitudeList.clear();
        ambNoList.clear();
    }
}
