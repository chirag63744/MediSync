package com.example.medisync;
public class GlobalData {
    private static GlobalData instance;
    private String selectedHospital;

    private GlobalData() {
        // Private constructor to prevent instantiation
    }

    public static synchronized GlobalData getInstance() {
        if (instance == null) {
            instance = new GlobalData();
        }
        return instance;
    }

    public String getSelectedHospital() {
        return selectedHospital;
    }

    public void setSelectedHospital(String selectedHospital) {
        this.selectedHospital = selectedHospital;
    }
}
