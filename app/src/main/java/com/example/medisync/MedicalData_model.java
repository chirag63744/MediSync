package com.example.medisync;

public class MedicalData_model {
    private String email;
    private String height;
    private String weight;
    private String bloodGroup;
    private String allergies;

    public MedicalData_model() {
        // Default constructor required for Firebase
    }

    public MedicalData_model(String email, String height, String weight, String bloodGroup, String allergies) {
        this.email = email;
        this.height = height;
        this.weight = weight;
        this.bloodGroup = bloodGroup;
        this.allergies = allergies;
    }

    // Getter and Setter methods for each property
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
}
