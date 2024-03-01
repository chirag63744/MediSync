package com.example.medisync;

public class AmbFire {
    String Hospital_Name;
    String Vehicle_no;
    boolean isChecked;
    public AmbFire() {
        // Default constructor required for Firestore
    }

    public AmbFire(String Hospital_Name, String Vehicle_no) {
        this.Hospital_Name = Hospital_Name;
        this.Vehicle_no = Vehicle_no;
        isChecked = false; // Set the initial state to unchecked
    }

    public String getHospital_Name() {
        return Hospital_Name;
    }

    public void setHospital_Name(String Hospital_Name) {
        this.Hospital_Name = Hospital_Name;
    }

    public String getVehicle_no() {
        return Vehicle_no;
    }

    public void setVehicle_no(String Vehicle_no) {
        this.Vehicle_no = Vehicle_no;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}