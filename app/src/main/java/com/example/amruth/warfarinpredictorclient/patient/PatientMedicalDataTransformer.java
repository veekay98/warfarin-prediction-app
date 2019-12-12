package com.example.amruth.warfarinpredictorclient.patient;

interface PatientMedicalDataTransformer {

    public <T extends Object> T transform(PatientMedicalData patientMedicalData);
}
