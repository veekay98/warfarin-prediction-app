package com.example.amruth.warfarinpredictorclient.patient;

import org.json.JSONObject;

//Singelton because only one patient can login from one app
public class Patient {
    private static final Patient ourInstance = new Patient();
    private String authorizationToken;
    private PatientMedicalData patientMedicalData;
    private PatientMedicalDataTransformer patientMedicalDataJSONTransformer;
    public static Patient getInstance() {
        return ourInstance;
    }

    private Patient() {
        this.patientMedicalDataJSONTransformer = new PatientMedicalDataWarfarinToJSONTransformer();
    }


    public String getAuthorizationToken() {
        return this.authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }


    public PatientMedicalData getPatientMedicalData() {
        return this.patientMedicalData;
    }

    public JSONObject getPatientMedicalDataJSON(){
        return this.patientMedicalDataJSONTransformer.transform(this.patientMedicalData);
    }

    public void setPatientMedicalData(PatientMedicalData patientMedicalData) {
        this.patientMedicalData = patientMedicalData;
    }
}
